package com.schoolfinance.audit;

import com.schoolfinance.dto.finance.PaymentResponse;
import com.schoolfinance.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class FinanceAuditAspect {

    private final AuditService auditService;


    @AfterReturning(
            pointcut =
                    "execution(* com.schoolfinance.service.PaymentService.createPayment(..))",
            returning = "result"
    )
    public void paymentCreated(
            Object result
    ) {

        if (!(result instanceof PaymentResponse payment)) {
            return;
        }


        auditService.log(
                "PAYMENT_CREATED",
                "Payment",
                payment.id(),
                null,
                payment
        );


        if (payment.receipt() != null) {

            auditService.log(
                    "RECEIPT_CREATED",
                    "Receipt",
                    payment.receipt().id(),
                    null,
                    payment.receipt()
            );
        }
    }
}