package com.epmapat.erp_epmapat.rrhh.servicio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.epmapat.erp_epmapat.rrhh.dto.AuditMetadataRequest;
import com.epmapat.erp_epmapat.rrhh.exception.RrhhNotFoundException;
import com.epmapat.erp_epmapat.rrhh.modelo.RrhhAuditableEntity;
import com.epmapat.erp_epmapat.rrhh.modelo.RrhhEmployee;

public final class RrhhSupport {

    private RrhhSupport() {
    }

    public static Pageable pageable(Integer page, Integer size) {
        int safePage = page == null || page < 0 ? 0 : page;
        int safeSize = size == null || size <= 0 || size > 100 ? 20 : size;
        return PageRequest.of(safePage, safeSize);
    }

    public static <T> T getOrThrow(Optional<T> optional, String message) {
        return optional.orElseThrow(() -> new RrhhNotFoundException(message));
    }

    public static void applyAudit(RrhhAuditableEntity entity, AuditMetadataRequest request, boolean isNew) {
        if (isNew) {
            entity.setCreatedBy(request.getCreatedBy());
        }
        entity.setUpdatedBy(request.getUpdatedBy());
    }

    public static String employeeFullName(RrhhEmployee employee) {
        if (employee == null) {
            return null;
        }
        return employee.getFirstName() + " " + employee.getLastName();
    }

    public static BigDecimal safe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    public static BigDecimal divide(BigDecimal numerator, BigDecimal denominator) {
        if (denominator == null || denominator.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }
}
