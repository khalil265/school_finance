package com.schoolfinance.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.schoolfinance.entity.finance.Payment;
import com.schoolfinance.entity.finance.Receipt;
import com.schoolfinance.repository.finance.ReceiptRepository;
import lombok.RequiredArgsConstructor;
import org.openpdf.text.*;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReceiptPdfService {

    private final ReceiptRepository receiptRepository;


    @Value("${app.receipt.public-base-url}")
    private String publicBaseUrl;


    @Transactional(readOnly = true)
    public byte[] generatePdf(
            UUID receiptId
    ) {

        Receipt receipt =
                receiptRepository
                        .findById(receiptId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Recu introuvable."
                                )
                        );


        try {

            return buildPdf(
                    receipt
            );

        }
        catch (
                DocumentException
                | IOException
                | WriterException exception
        ) {

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Impossible de generer le PDF du recu.",
                    exception
            );
        }
    }


    private byte[] buildPdf(
            Receipt receipt
    )
            throws
            DocumentException,
            IOException,
            WriterException {

        Payment payment =
                receipt.getPayment();

        var account =
                payment.getStudentAccount();

        var student =
                account.getStudent();

        var establishment =
                account.getEstablishment();


        ByteArrayOutputStream output =
                new ByteArrayOutputStream();


        Document document =
                new Document(
                        PageSize.A4,
                        50,
                        50,
                        45,
                        45
                );


        PdfWriter.getInstance(
                document,
                output
        );


        document.addTitle(
                "Recu " +
                        receipt.getReceiptNumber()
        );

        document.addAuthor(
                establishment.getName()
        );


        document.open();


        Font titleFont =
                new Font(
                        Font.HELVETICA,
                        18,
                        Font.BOLD
                );

        Font sectionFont =
                new Font(
                        Font.HELVETICA,
                        12,
                        Font.BOLD
                );

        Font normalFont =
                new Font(
                        Font.HELVETICA,
                        10,
                        Font.NORMAL
                );


        Paragraph establishmentTitle =
                new Paragraph(
                        establishment.getName(),
                        titleFont
                );

        establishmentTitle.setAlignment(
                Element.ALIGN_CENTER
        );

        document.add(
                establishmentTitle
        );


        Paragraph subtitle =
                new Paragraph(
                        "RECU DE PAIEMENT",
                        sectionFont
                );

        subtitle.setAlignment(
                Element.ALIGN_CENTER
        );

        subtitle.setSpacingAfter(20);

        document.add(subtitle);


        document.add(
                paragraph(
                        "Numero de recu : ",
                        receipt.getReceiptNumber(),
                        normalFont
                )
        );

        document.add(
                paragraph(
                        "Numero de paiement : ",
                        payment.getPaymentNumber(),
                        normalFont
                )
        );

        document.add(
                paragraph(
                        "Date : ",
                        receipt.getIssuedAt()
                                .format(
                                        DateTimeFormatter.ofPattern(
                                                "dd/MM/yyyy HH:mm:ss"
                                        )
                                ),
                        normalFont
                )
        );


        document.add(
                new Paragraph(
                        "---------------------------------------------",
                        normalFont
                )
        );


        document.add(
                paragraph(
                        "Matricule eleve : ",
                        student.getRegistrationNumber(),
                        normalFont
                )
        );

        document.add(
                paragraph(
                        "Eleve : ",
                        student.getFirstName()
                                + " "
                                + student.getLastName(),
                        normalFont
                )
        );

        document.add(
                paragraph(
                        "Annee academique : ",
                        account.getAcademicYear().getLabel(),
                        normalFont
                )
        );


        document.add(
                new Paragraph(
                        "---------------------------------------------",
                        normalFont
                )
        );


        document.add(
                paragraph(
                        "Montant paye : ",
                        formatAmount(
                                receipt.getAmount()
                        )
                                + " "
                                + establishment.getCurrency(),
                        sectionFont
                )
        );

        document.add(
                paragraph(
                        "Mode de paiement : ",
                        payment.getPaymentMethod().name(),
                        normalFont
                )
        );


        if (
                payment.getTransactionReference()
                        != null
        ) {

            document.add(
                    paragraph(
                            "Reference transaction : ",
                            payment.getTransactionReference(),
                            normalFont
                    )
            );
        }


        document.add(
                paragraph(
                        "Encaisse par : ",
                        payment.getReceivedBy(),
                        normalFont
                )
        );

        document.add(
                paragraph(
                        "Solde restant : ",
                        formatAmount(
                                account.getBalance()
                        )
                                + " "
                                + establishment.getCurrency(),
                        sectionFont
                )
        );


        document.add(
                new Paragraph(
                        " ",
                        normalFont
                )
        );


        String verificationUrl =
                publicBaseUrl
                        + "/"
                        + receipt.getVerificationCode();


        byte[] qrBytes =
                generateQrCode(
                        verificationUrl
                );


        Image qrImage =
                Image.getInstance(
                        qrBytes
                );

        qrImage.scaleToFit(
                140,
                140
        );

        qrImage.setAlignment(
                Element.ALIGN_CENTER
        );

        document.add(
                qrImage
        );


        Paragraph verificationText =
                new Paragraph(
                        "Code de verification : "
                                + receipt.getVerificationCode(),
                        normalFont
                );

        verificationText.setAlignment(
                Element.ALIGN_CENTER
        );

        document.add(
                verificationText
        );


        Paragraph footer =
                new Paragraph(
                        "Ce recu est genere electroniquement. "
                        + "Scannez le QR Code pour verifier son authenticite.",
                        normalFont
                );

        footer.setAlignment(
                Element.ALIGN_CENTER
        );

        footer.setSpacingBefore(
                15
        );

        document.add(
                footer
        );


        document.close();

        return output.toByteArray();
    }


    private Paragraph paragraph(
            String label,
            String value,
            Font font
    ) {

        return new Paragraph(
                label
                        + (
                        value == null
                                ? ""
                                : value
                ),
                font
        );
    }


    private byte[] generateQrCode(
            String content
    )
            throws
            WriterException,
            IOException {

        QRCodeWriter writer =
                new QRCodeWriter();


        BitMatrix matrix =
                writer.encode(
                        content,
                        BarcodeFormat.QR_CODE,
                        300,
                        300
                );


        ByteArrayOutputStream output =
                new ByteArrayOutputStream();


        MatrixToImageWriter.writeToStream(
                matrix,
                "PNG",
                output
        );


        return output.toByteArray();
    }


    private String formatAmount(
            BigDecimal amount
    ) {

        if (amount == null) {
            return "0";
        }

        return String.format(
                "%,.0f",
                amount
        )
                .replace(
                        ",",
                        " "
                );
    }
}