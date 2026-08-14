package com.schoolfinance.service;

import com.schoolfinance.dto.finance.*;
import com.schoolfinance.entity.finance.*;
import com.schoolfinance.enums.ChargeStatus;
import com.schoolfinance.enums.PaymentMethod;
import com.schoolfinance.enums.PaymentStatus;
import com.schoolfinance.enums.StudentAccountStatus;
import com.schoolfinance.repository.finance.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final StudentAccountRepository studentAccountRepository;

    private final StudentChargeRepository studentChargeRepository;

    private final PaymentRepository paymentRepository;

    private final PaymentAllocationRepository allocationRepository;

    private final ReceiptRepository receiptRepository;


    @Transactional
    public PaymentResponse createPayment(
            PaymentRequest request
    ) {

        StudentAccount account =
                studentAccountRepository
                        .findByStudentIdAndAcademicYearId(
                                request.studentId(),
                                request.academicYearId()
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Compte financier eleve introuvable. Generez d'abord l'echeancier."
                                )
                        );


        recalculateAccount(account);


        if (request.amount().compareTo(
                BigDecimal.ZERO
        ) <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Le montant du paiement doit etre superieur a zero."
            );
        }


        if (request.amount().compareTo(
                account.getBalance()
        ) > 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Le montant du paiement depasse le solde restant de l'eleve."
            );
        }


        validatePaymentReference(
                request
        );


        String username =
                currentUsername();


        Payment payment =
                Payment.builder()
                        .studentAccount(account)
                        .paymentNumber(
                                generatePaymentNumber()
                        )
                        .amount(
                                request.amount()
                        )
                        .paymentMethod(
                                request.paymentMethod()
                        )
                        .status(
                                PaymentStatus.COMPLETED
                        )
                        .transactionReference(
                                clean(
                                        request.transactionReference()
                                )
                        )
                        .notes(
                                request.notes()
                        )
                        .paidAt(
                                LocalDateTime.now()
                        )
                        .receivedBy(
                                username
                        )
                        .active(true)
                        .build();


        payment =
                paymentRepository.save(
                        payment
                );


        BigDecimal remainingPayment =
                request.amount();


        List<StudentCharge> charges =
                studentChargeRepository
                        .findByStudentAccountIdAndActiveTrueOrderByDueDateAscInstallmentNumberAsc(
                                account.getId()
                        )
                        .stream()
                        .filter(c ->
                                c.getStatus()
                                        != ChargeStatus.CANCELLED
                        )
                        .sorted(
                                Comparator
                                        .comparing(
                                                StudentCharge::getDueDate,
                                                Comparator.nullsLast(
                                                        Comparator.naturalOrder()
                                                )
                                        )
                                        .thenComparing(
                                                StudentCharge::getInstallmentNumber
                                        )
                        )
                        .toList();


        List<PaymentAllocation> allocations =
                new ArrayList<>();


        for (StudentCharge charge : charges) {

            if (remainingPayment.compareTo(
                    BigDecimal.ZERO
            ) <= 0) {
                break;
            }


            BigDecimal outstanding =
                    charge.getAmount()
                            .subtract(
                                    charge.getPaidAmount()
                            )
                            .subtract(
                                    charge.getDiscountAmount()
                            );


            if (outstanding.compareTo(
                    BigDecimal.ZERO
            ) <= 0) {
                continue;
            }


            BigDecimal allocated =
                    remainingPayment.min(
                            outstanding
                    );


            charge.setPaidAmount(
                    charge.getPaidAmount()
                            .add(allocated)
            );


            updateChargeStatus(
                    charge
            );


            studentChargeRepository.save(
                    charge
            );


            PaymentAllocation allocation =
                    PaymentAllocation.builder()
                            .payment(payment)
                            .studentCharge(charge)
                            .allocatedAmount(
                                    allocated
                            )
                            .build();


            allocation =
                    allocationRepository.save(
                            allocation
                    );


            allocations.add(
                    allocation
            );


            remainingPayment =
                    remainingPayment.subtract(
                            allocated
                    );
        }


        if (remainingPayment.compareTo(
                BigDecimal.ZERO
        ) != 0) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Le paiement n'a pas pu etre totalement affecte aux echeances."
            );
        }


        recalculateAccount(
                account
        );


        Receipt receipt =
                Receipt.builder()
                        .payment(payment)
                        .receiptNumber(
                                generateReceiptNumber()
                        )
                        .verificationCode(
                                UUID.randomUUID()
                        )
                        .amount(
                                payment.getAmount()
                        )
                        .issuedAt(
                                LocalDateTime.now()
                        )
                        .cancelled(false)
                        .build();


        receipt =
                receiptRepository.save(
                        receipt
                );


        return toPaymentResponse(
                payment,
                allocations,
                receipt
        );
    }


    @Transactional(readOnly = true)
    public PaymentResponse getPayment(
            UUID paymentId
    ) {

        Payment payment =
                paymentRepository
                        .findById(paymentId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Paiement introuvable."
                                )
                        );


        List<PaymentAllocation> allocations =
                allocationRepository
                        .findByPaymentIdOrderByCreatedAtAsc(
                                paymentId
                        );


        Receipt receipt =
                receiptRepository
                        .findByPaymentId(
                                paymentId
                        )
                        .orElse(null);


        return toPaymentResponse(
                payment,
                allocations,
                receipt
        );
    }


    @Transactional(readOnly = true)
    public List<PaymentResponse> getStudentPayments(
            UUID studentId,
            UUID academicYearId
    ) {

        return paymentRepository
                .findByStudentAccountStudentIdAndStudentAccountAcademicYearIdOrderByPaidAtDesc(
                        studentId,
                        academicYearId
                )
                .stream()
                .map(payment -> {

                    List<PaymentAllocation> allocations =
                            allocationRepository
                                    .findByPaymentIdOrderByCreatedAtAsc(
                                            payment.getId()
                                    );


                    Receipt receipt =
                            receiptRepository
                                    .findByPaymentId(
                                            payment.getId()
                                    )
                                    .orElse(null);


                    return toPaymentResponse(
                            payment,
                            allocations,
                            receipt
                    );
                })
                .toList();
    }


    @Transactional(readOnly = true)
    public ReceiptResponse getReceipt(
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


        return toReceiptResponse(
                receipt
        );
    }


    private void validatePaymentReference(
            PaymentRequest request
    ) {

        if (request.paymentMethod()
                == PaymentMethod.CASH) {
            return;
        }


        if (
                request.transactionReference() == null
                || request.transactionReference().isBlank()
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Une reference de transaction est obligatoire pour ce mode de paiement."
            );
        }
    }


    private void updateChargeStatus(
            StudentCharge charge
    ) {

        BigDecimal remaining =
                charge.getAmount()
                        .subtract(
                                charge.getPaidAmount()
                        )
                        .subtract(
                                charge.getDiscountAmount()
                        );


        if (remaining.signum() < 0) {
            remaining = BigDecimal.ZERO;
        }


        charge.setRemainingAmount(
                remaining
        );


        if (remaining.compareTo(
                BigDecimal.ZERO
        ) == 0) {

            charge.setStatus(
                    ChargeStatus.PAID
            );

        }
        else if (
                charge.getPaidAmount()
                        .compareTo(BigDecimal.ZERO) > 0
        ) {

            charge.setStatus(
                    ChargeStatus.PARTIALLY_PAID
            );

        }
        else if (
                charge.getDueDate() != null
                && LocalDate.now().isAfter(
                        charge.getDueDate()
                                .plusDays(
                                        charge.getGracePeriodDays()
                                )
                )
        ) {

            charge.setStatus(
                    ChargeStatus.OVERDUE
            );

        }
        else {

            charge.setStatus(
                    ChargeStatus.PENDING
            );
        }
    }


    private void recalculateAccount(
            StudentAccount account
    ) {

        List<StudentCharge> charges =
                studentChargeRepository
                        .findByStudentAccountIdAndActiveTrueOrderByDueDateAscInstallmentNumberAsc(
                                account.getId()
                        );


        BigDecimal totalCharged =
                BigDecimal.ZERO;

        BigDecimal totalPaid =
                BigDecimal.ZERO;

        BigDecimal totalDiscount =
                BigDecimal.ZERO;


        long overdueCount = 0;


        for (StudentCharge charge : charges) {

            if (
                    charge.getStatus()
                            == ChargeStatus.CANCELLED
            ) {
                continue;
            }


            updateChargeStatus(
                    charge
            );


            totalCharged =
                    totalCharged.add(
                            charge.getAmount()
                    );

            totalPaid =
                    totalPaid.add(
                            charge.getPaidAmount()
                    );

            totalDiscount =
                    totalDiscount.add(
                            charge.getDiscountAmount()
                    );


            if (
                    charge.getStatus()
                            == ChargeStatus.OVERDUE
            ) {
                overdueCount++;
            }
        }


        studentChargeRepository.saveAll(
                charges
        );


        BigDecimal balance =
                totalCharged
                        .subtract(totalPaid)
                        .subtract(totalDiscount);


        if (balance.signum() < 0) {
            balance = BigDecimal.ZERO;
        }


        StudentAccountStatus status;


        if (balance.compareTo(
                BigDecimal.ZERO
        ) == 0) {

            status =
                    StudentAccountStatus.SETTLED;

        }
        else if (overdueCount > 0) {

            status =
                    StudentAccountStatus.OVERDUE;

        }
        else if (
                totalPaid.compareTo(
                        BigDecimal.ZERO
                ) > 0
        ) {

            status =
                    StudentAccountStatus.PARTIAL;

        }
        else {

            status =
                    StudentAccountStatus.UP_TO_DATE;
        }


        account.setTotalCharged(
                totalCharged
        );

        account.setTotalPaid(
                totalPaid
        );

        account.setTotalDiscount(
                totalDiscount
        );

        account.setBalance(
                balance
        );

        account.setStatus(
                status
        );


        studentAccountRepository.save(
                account
        );
    }


    private PaymentResponse toPaymentResponse(
            Payment payment,
            List<PaymentAllocation> allocations,
            Receipt receipt
    ) {

        List<PaymentAllocationResponse>
                allocationResponses =
                allocations.stream()
                        .map(this::toAllocationResponse)
                        .toList();


        ReceiptResponse receiptResponse =
                receipt == null
                        ? null
                        : toReceiptResponse(
                                receipt
                        );


        StudentAccount account =
                payment.getStudentAccount();


        return new PaymentResponse(
                payment.getId(),
                payment.getPaymentNumber(),
                account.getStudent().getId(),
                account.getStudent().getRegistrationNumber(),
                account.getStudent().getFirstName()
                        + " "
                        + account.getStudent().getLastName(),
                account.getAcademicYear().getId(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getStatus(),
                payment.getTransactionReference(),
                payment.getNotes(),
                payment.getPaidAt(),
                payment.getReceivedBy(),
                account.getBalance(),
                allocationResponses,
                receiptResponse
        );
    }


    private PaymentAllocationResponse toAllocationResponse(
            PaymentAllocation allocation
    ) {

        StudentCharge charge =
                allocation.getStudentCharge();


        return new PaymentAllocationResponse(
                allocation.getId(),
                charge.getId(),
                charge.getFeeStructure()
                        .getFeeType()
                        .getCode(),
                charge.getLabel(),
                charge.getDueDate(),
                allocation.getAllocatedAmount(),
                charge.getRemainingAmount()
        );
    }


    private ReceiptResponse toReceiptResponse(
            Receipt receipt
    ) {

        return new ReceiptResponse(
                receipt.getId(),
                receipt.getReceiptNumber(),
                receipt.getVerificationCode(),
                receipt.getAmount(),
                receipt.getIssuedAt(),
                receipt.getCancelled()
        );
    }


    private String currentUsername() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();


        if (
                authentication == null
                || authentication.getName() == null
        ) {
            return "SYSTEM";
        }


        return authentication.getName();
    }


    private String generatePaymentNumber() {

        String date =
                LocalDate.now()
                        .format(
                                DateTimeFormatter.BASIC_ISO_DATE
                        );


        String random =
                UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 12)
                        .toUpperCase();


        return "PAY-" + date + "-" + random;
    }


    private String generateReceiptNumber() {

        String date =
                LocalDate.now()
                        .format(
                                DateTimeFormatter.BASIC_ISO_DATE
                        );


        String random =
                UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 12)
                        .toUpperCase();


        return "REC-" + date + "-" + random;
    }


    private String clean(
            String value
    ) {

        if (value == null) {
            return null;
        }

        String cleaned =
                value.trim();

        return cleaned.isEmpty()
                ? null
                : cleaned;
    }
}