package com.schoolfinance.controller;

import com.schoolfinance.service.AuditService;
import com.schoolfinance.service.ReceiptPdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/receipts")
@RequiredArgsConstructor
public class ReceiptDocumentController {

    private final ReceiptPdfService receiptPdfService;

    private final AuditService auditService;


    @GetMapping(
            value = "/{id}/pdf",
            produces = MediaType.APPLICATION_PDF_VALUE
    )
    @PreAuthorize("hasAuthority('RECEIPT_PRINT')")
    public ResponseEntity<byte[]> pdf(
            @PathVariable
            UUID id
    ) {

        byte[] pdf =
                receiptPdfService
                        .generatePdf(id);


        auditService.log(
                "RECEIPT_PDF_GENERATED",
                "Receipt",
                id,
                null,
                "PDF generated"
        );


        ContentDisposition disposition =
                ContentDisposition
                        .attachment()
                        .filename(
                                "receipt-" + id + ".pdf",
                                StandardCharsets.UTF_8
                        )
                        .build();


        return ResponseEntity
                .ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        disposition.toString()
                )
                .contentType(
                        MediaType.APPLICATION_PDF
                )
                .body(pdf);
    }
}