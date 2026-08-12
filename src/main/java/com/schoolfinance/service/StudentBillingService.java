package com.schoolfinance.service;

import com.schoolfinance.dto.finance.*;
import com.schoolfinance.entity.academic.Enrollment;
import com.schoolfinance.entity.finance.FeeStructure;
import com.schoolfinance.entity.finance.StudentAccount;
import com.schoolfinance.entity.finance.StudentCharge;
import com.schoolfinance.enums.ChargeStatus;
import com.schoolfinance.enums.StudentAccountStatus;
import com.schoolfinance.repository.academic.EnrollmentRepository;
import com.schoolfinance.repository.finance.FeeStructureRepository;
import com.schoolfinance.repository.finance.StudentAccountRepository;
import com.schoolfinance.repository.finance.StudentChargeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentBillingService {

    private final EnrollmentRepository enrollmentRepository;

    private final FeeStructureRepository feeStructureRepository;

    private final StudentAccountRepository studentAccountRepository;

    private final StudentChargeRepository studentChargeRepository;


    @Transactional
    public ScheduleGenerationResponse generateSchedule(
            GenerateScheduleRequest request
    ) {

        Enrollment enrollment =
                enrollmentRepository
                        .findById(request.enrollmentId())
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Inscription introuvable."
                                )
                        );

        UUID studentId =
                enrollment.getStudent().getId();

        UUID academicYearId =
                enrollment.getAcademicYear().getId();

        UUID levelId =
                enrollment
                        .getSchoolClass()
                        .getLevel()
                        .getId();


        StudentAccount account =
                studentAccountRepository
                        .findByStudentIdAndAcademicYearId(
                                studentId,
                                academicYearId
                        )
                        .orElseGet(() -> {

                            StudentAccount newAccount =
                                    StudentAccount.builder()
                                            .student(
                                                    enrollment.getStudent()
                                            )
                                            .academicYear(
                                                    enrollment.getAcademicYear()
                                            )
                                            .establishment(
                                                    enrollment
                                                            .getStudent()
                                                            .getEstablishment()
                                            )
                                            .totalCharged(
                                                    BigDecimal.ZERO
                                            )
                                            .totalPaid(
                                                    BigDecimal.ZERO
                                            )
                                            .totalDiscount(
                                                    BigDecimal.ZERO
                                            )
                                            .balance(
                                                    BigDecimal.ZERO
                                            )
                                            .status(
                                                    StudentAccountStatus.UP_TO_DATE
                                            )
                                            .active(true)
                                            .build();

                            return studentAccountRepository.save(
                                    newAccount
                            );
                        });


        List<FeeStructure> structures =
                feeStructureRepository
                        .findByAcademicYearIdAndLevelIdAndActiveTrue(
                                academicYearId,
                                levelId
                        );


        if (structures.isEmpty()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Aucune structure tarifaire active n'est configuree pour ce niveau."
            );
        }


        int created = 0;
        int skipped = 0;


        for (FeeStructure structure : structures) {

            int count =
                    structure.getInstallmentCount() == null
                            ? 1
                            : structure.getInstallmentCount();

            LocalDate firstDueDate =
                    structure.getFirstDueDate();


            for (int installment = 1;
                 installment <= count;
                 installment++) {

                boolean exists =
                        studentChargeRepository
                                .existsByStudentAccountIdAndFeeStructureIdAndInstallmentNumber(
                                        account.getId(),
                                        structure.getId(),
                                        installment
                                );

                if (exists) {
                    skipped++;
                    continue;
                }


                LocalDate dueDate = null;

                if (firstDueDate != null) {

                    dueDate =
                            count == 1
                                    ? firstDueDate
                                    : firstDueDate.plusMonths(
                                            installment - 1L
                                    );
                }


                String label =
                        buildChargeLabel(
                                structure,
                                installment,
                                count,
                                dueDate
                        );


                StudentCharge charge =
                        StudentCharge.builder()
                                .studentAccount(account)
                                .feeStructure(structure)
                                .installmentNumber(installment)
                                .label(label)
                                .amount(
                                        structure.getAmount()
                                )
                                .paidAmount(
                                        BigDecimal.ZERO
                                )
                                .discountAmount(
                                        BigDecimal.ZERO
                                )
                                .remainingAmount(
                                        structure.getAmount()
                                )
                                .dueDate(dueDate)
                                .gracePeriodDays(
                                        structure.getGracePeriodDays()
                                                == null
                                                ? 0
                                                : structure.getGracePeriodDays()
                                )
                                .status(
                                        ChargeStatus.PENDING
                                )
                                .active(true)
                                .build();

                studentChargeRepository.save(
                        charge
                );

                created++;
            }
        }


        refreshAccount(account);


        List<StudentChargeResponse> charges =
                getChargeResponses(account.getId());


        return new ScheduleGenerationResponse(
                account.getId(),
                enrollment.getStudent().getId(),
                enrollment.getStudent().getRegistrationNumber(),
                created,
                skipped,
                account.getTotalCharged(),
                account.getBalance(),
                charges
        );
    }


    @Transactional(readOnly = true)
    public StudentFinancialSummaryResponse getFinancialSummary(
            UUID studentId,
            UUID academicYearId
    ) {

        StudentAccount account =
                studentAccountRepository
                        .findByStudentIdAndAcademicYearId(
                                studentId,
                                academicYearId
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Aucun compte financier n'existe pour cet eleve et cette annee."
                                )
                        );


        Enrollment enrollment =
                enrollmentRepository
                        .findByStudentIdOrderByEnrollmentDateDesc(
                                studentId
                        )
                        .stream()
                        .filter(e ->
                                e.getAcademicYear()
                                        .getId()
                                        .equals(academicYearId)
                        )
                        .findFirst()
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Inscription academique introuvable."
                                )
                        );


        List<StudentChargeResponse> charges =
                getChargeResponses(
                        account.getId()
                );


        long overdue =
                charges.stream()
                        .filter(c ->
                                c.status()
                                        == ChargeStatus.OVERDUE
                        )
                        .count();


        return new StudentFinancialSummaryResponse(
                account.getId(),
                account.getStudent().getId(),
                account.getStudent().getRegistrationNumber(),
                account.getStudent().getFirstName()
                        + " "
                        + account.getStudent().getLastName(),
                account.getAcademicYear().getId(),
                account.getAcademicYear().getLabel(),
                enrollment
                        .getSchoolClass()
                        .getLevel()
                        .getId(),
                enrollment
                        .getSchoolClass()
                        .getLevel()
                        .getName(),
                account.getTotalCharged(),
                account.getTotalPaid(),
                account.getTotalDiscount(),
                account.getBalance(),
                overdue,
                account.getStatus(),
                charges
        );
    }


    @Transactional
    public StudentFinancialSummaryResponse refreshFinancialSummary(
            UUID studentId,
            UUID academicYearId
    ) {

        StudentAccount account =
                studentAccountRepository
                        .findByStudentIdAndAcademicYearId(
                                studentId,
                                academicYearId
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Compte financier introuvable."
                                )
                        );


        refreshAccount(account);

        return getFinancialSummary(
                studentId,
                academicYearId
        );
    }


    private void refreshAccount(
            StudentAccount account
    ) {

        List<StudentCharge> charges =
                studentChargeRepository
                        .findByStudentAccountIdAndActiveTrueOrderByDueDateAscInstallmentNumberAsc(
                                account.getId()
                        );


        BigDecimal totalCharged =
                charges.stream()
                        .map(StudentCharge::getAmount)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );


        BigDecimal totalPaid =
                charges.stream()
                        .map(StudentCharge::getPaidAmount)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );


        BigDecimal totalDiscount =
                charges.stream()
                        .map(StudentCharge::getDiscountAmount)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );


        LocalDate today =
                LocalDate.now();


        for (StudentCharge charge : charges) {

            if (charge.getStatus()
                    == ChargeStatus.CANCELLED) {
                continue;
            }


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
            else if (charge.getPaidAmount()
                    .compareTo(BigDecimal.ZERO) > 0) {

                charge.setStatus(
                        ChargeStatus.PARTIALLY_PAID
                );

            }
            else if (
                    charge.getDueDate() != null
                    && today.isAfter(
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


        long overdueCount =
                charges.stream()
                        .filter(c ->
                                c.getStatus()
                                        == ChargeStatus.OVERDUE
                        )
                        .count();


        StudentAccountStatus accountStatus;


        if (balance.compareTo(
                BigDecimal.ZERO
        ) == 0) {

            accountStatus =
                    StudentAccountStatus.SETTLED;

        }
        else if (overdueCount > 0) {

            accountStatus =
                    StudentAccountStatus.OVERDUE;

        }
        else if (totalPaid.compareTo(
                BigDecimal.ZERO
        ) > 0) {

            accountStatus =
                    StudentAccountStatus.PARTIAL;

        }
        else {

            accountStatus =
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
                accountStatus
        );


        studentAccountRepository.save(
                account
        );
    }


    private String buildChargeLabel(
            FeeStructure structure,
            int installment,
            int count,
            LocalDate dueDate
    ) {

        String base =
                structure
                        .getFeeType()
                        .getName();


        if (count <= 1) {
            return base;
        }


        if (dueDate != null) {

            return base
                    + " - "
                    + dueDate.getMonth()
                    + " "
                    + dueDate.getYear();
        }


        return base
                + " - Echeance "
                + installment
                + "/"
                + count;
    }


    private List<StudentChargeResponse> getChargeResponses(
            UUID accountId
    ) {

        List<StudentCharge> charges =
                studentChargeRepository
                        .findByStudentAccountIdAndActiveTrueOrderByDueDateAscInstallmentNumberAsc(
                                accountId
                        );


        List<StudentChargeResponse> result =
                new ArrayList<>();


        for (StudentCharge charge : charges) {

            result.add(
                    toChargeResponse(
                            charge
                    )
            );
        }


        return result;
    }


    private StudentChargeResponse toChargeResponse(
            StudentCharge charge
    ) {

        return new StudentChargeResponse(
                charge.getId(),
                charge.getStudentAccount().getId(),
                charge.getFeeStructure().getId(),
                charge.getFeeStructure().getFeeType().getId(),
                charge.getFeeStructure().getFeeType().getCode(),
                charge.getFeeStructure().getFeeType().getName(),
                charge.getInstallmentNumber(),
                charge.getLabel(),
                charge.getAmount(),
                charge.getPaidAmount(),
                charge.getDiscountAmount(),
                charge.getRemainingAmount(),
                charge.getDueDate(),
                charge.getGracePeriodDays(),
                charge.getStatus()
        );
    }
}