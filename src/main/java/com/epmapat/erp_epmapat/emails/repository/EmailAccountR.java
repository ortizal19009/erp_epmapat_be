package com.epmapat.erp_epmapat.emails.repository;

import com.epmapat.erp_epmapat.emails.model.EmailAccount;
import com.epmapat.erp_epmapat.emails.model.EmailType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmailAccountR extends JpaRepository<EmailAccount, Long> {

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCaseAndIdNot(String code, Long id);

    Optional<EmailAccount> findByCodeIgnoreCase(String code);

    Optional<EmailAccount> findFirstByActiveTrueAndDefaultForType(EmailType type);

    Optional<EmailAccount> findFirstByActiveTrueAndDefaultAccountTrue();

    List<EmailAccount> findByActive(boolean active);
}
