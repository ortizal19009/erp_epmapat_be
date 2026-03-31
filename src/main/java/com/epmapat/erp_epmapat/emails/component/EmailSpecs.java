package com.epmapat.erp_epmapat.emails.component;

import com.epmapat.erp_epmapat.emails.model.EmailMessage;
import com.epmapat.erp_epmapat.emails.model.EmailStatus;
import com.epmapat.erp_epmapat.emails.model.EmailType;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;

public class EmailSpecs {
    public static Specification<EmailMessage> filter(EmailStatus status, EmailType type, String correlationId, Long accountId) {
        return (root, query, cb) -> {
            var p = cb.conjunction();
            if (status != null) p.getExpressions().add(cb.equal(root.get("status"), status));
            if (type != null) p.getExpressions().add(cb.equal(root.get("type"), type));
            if (correlationId != null && !correlationId.isBlank()) {
                p.getExpressions().add(cb.equal(root.get("correlationId"), correlationId));
            }
            if (accountId != null) {
                p.getExpressions().add(cb.equal(root.get("account").get("id"), accountId));
            }
            return p;
        };
    }

    public static Specification<EmailMessage> pendingStale(OffsetDateTime createdBefore, Integer minAttempts) {
        return (root, query, cb) -> {
            var p = cb.conjunction();
            p.getExpressions().add(cb.equal(root.get("status"), EmailStatus.PENDING));
            if (createdBefore != null) {
                p.getExpressions().add(cb.lessThanOrEqualTo(root.get("createdAt"), createdBefore));
            }
            if (minAttempts != null) {
                p.getExpressions().add(cb.greaterThanOrEqualTo(root.get("attempts"), minAttempts));
            }
            return p;
        };
    }
}
