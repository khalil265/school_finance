package com.schoolfinance.repository.finance;

import com.schoolfinance.entity.finance.StudentCharge;
import com.schoolfinance.enums.ChargeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface StudentChargeRepository
        extends JpaRepository<StudentCharge, UUID> {

    boolean existsByStudentAccountIdAndFeeStructureIdAndInstallmentNumber(
            UUID studentAccountId,
            UUID feeStructureId,
            Integer installmentNumber
    );

    List<StudentCharge>
    findByStudentAccountIdAndActiveTrueOrderByDueDateAscInstallmentNumberAsc(
            UUID studentAccountId
    );

    long countByStudentAccountIdAndStatus(
            UUID studentAccountId,
            ChargeStatus status
    );

    List<StudentCharge>
    findByStudentAccountIdAndDueDateBeforeAndStatusIn(
            UUID studentAccountId,
            LocalDate date,
            List<ChargeStatus> statuses
    );
}