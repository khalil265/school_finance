package com.schoolfinance.repository.finance;

import com.schoolfinance.entity.finance.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository
        extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByPaymentNumber(
            String paymentNumber
    );

    List<Payment>
    findByStudentAccountStudentIdOrderByPaidAtDesc(
            UUID studentId
    );

    List<Payment>
    findByStudentAccountStudentIdAndStudentAccountAcademicYearIdOrderByPaidAtDesc(
            UUID studentId,
            UUID academicYearId
    );
}