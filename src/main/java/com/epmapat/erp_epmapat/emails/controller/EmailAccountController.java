package com.epmapat.erp_epmapat.emails.controller;

import com.epmapat.erp_epmapat.emails.dtos.EmailAccountRequest;
import com.epmapat.erp_epmapat.emails.dtos.EmailAccountResponse;
import com.epmapat.erp_epmapat.emails.service.EmailAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/email-accounts")
public class EmailAccountController {

    private final EmailAccountService accountService;

    public EmailAccountController(EmailAccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public List<EmailAccountResponse> list(@RequestParam(required = false) Boolean active) {
        return accountService.list(active);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmailAccountResponse> get(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(accountService.get(id));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody EmailAccountRequest req) {
        try {
            return ResponseEntity.ok(accountService.create(req));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody EmailAccountRequest req) {
        try {
            return ResponseEntity.ok(accountService.update(id, req));
        } catch (IllegalArgumentException ex) {
            String msg = ex.getMessage();
            if (msg != null && msg.contains("id " + id)) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().body(msg);
        }
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<?> activate(@PathVariable Long id) {
        return updateActive(id, true);
    }

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivate(@PathVariable Long id) {
        return updateActive(id, false);
    }

    private ResponseEntity<?> updateActive(Long id, boolean active) {
        try {
            return ResponseEntity.ok(accountService.activate(id, active));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
