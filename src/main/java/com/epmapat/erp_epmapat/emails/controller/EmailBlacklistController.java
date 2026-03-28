package com.epmapat.erp_epmapat.emails.controller;

import com.epmapat.erp_epmapat.emails.dtos.EmailBlacklistRequest;
import com.epmapat.erp_epmapat.emails.service.EmailBlacklistService;
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

@RestController
@RequestMapping("/api/v1/email-blacklist")
public class EmailBlacklistController {

    private final EmailBlacklistService blacklistService;

    public EmailBlacklistController(EmailBlacklistService blacklistService) {
        this.blacklistService = blacklistService;
    }

    @GetMapping
    public ResponseEntity<?> list(@RequestParam(required = false) Boolean active) {
        return ResponseEntity.ok(blacklistService.list(active));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(blacklistService.get(id));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody EmailBlacklistRequest req) {
        try {
            return ResponseEntity.ok(blacklistService.create(req));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody EmailBlacklistRequest req) {
        try {
            return ResponseEntity.ok(blacklistService.update(id, req));
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
            return ResponseEntity.ok(blacklistService.activate(id, active));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
