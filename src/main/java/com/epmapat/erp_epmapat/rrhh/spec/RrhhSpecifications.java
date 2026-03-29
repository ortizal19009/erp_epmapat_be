package com.epmapat.erp_epmapat.rrhh.spec;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

public final class RrhhSpecifications {

    private RrhhSpecifications() {
    }

    public static <T> Specification<T> likeIgnoreCase(String field, String value) {
        return (root, query, cb) -> {
            if (value == null || value.isBlank()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get(field)), "%" + value.trim().toLowerCase() + "%");
        };
    }

    public static <T> Specification<T> likeNestedIgnoreCase(String path, String field, String value) {
        return (root, query, cb) -> {
            if (value == null || value.isBlank()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get(path).get(field)), "%" + value.trim().toLowerCase() + "%");
        };
    }

    public static <T> Specification<T> equalsTo(String field, Object value) {
        return (root, query, cb) -> value == null ? cb.conjunction() : cb.equal(root.get(field), value);
    }

    public static <T> Specification<T> equalsNested(String path, String field, Object value) {
        return (root, query, cb) -> value == null ? cb.conjunction() : cb.equal(root.get(path).get(field), value);
    }

    public static <T> Specification<T> dateGreaterOrEqual(String field, LocalDate value) {
        return (root, query, cb) -> value == null ? cb.conjunction() : cb.greaterThanOrEqualTo(root.get(field), value);
    }

    public static <T> Specification<T> dateLessOrEqual(String field, LocalDate value) {
        return (root, query, cb) -> value == null ? cb.conjunction() : cb.lessThanOrEqualTo(root.get(field), value);
    }
}
