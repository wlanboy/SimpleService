package com.wlanboy.demo.controller;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.wlanboy.demo.model.AuditLog;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/audit")
public class AuditController {

	private static final Logger logger = LoggerFactory.getLogger(AuditController.class);

	private final AuditService auditService;

	@PostMapping
	public ResponseEntity<AuditLog> createAudit(@Valid @RequestBody AuditLog audit) {
		AuditLog saved = auditService.saveAuditLog(audit);
		logger.info("AuditLog created with ID {}", saved.getIdentifier());
		return ResponseEntity.status(201).body(saved);
	}

	@GetMapping("/{id}")
	public ResponseEntity<AuditLog> getById(@PathVariable Long id) {
		return auditService.findById(id)
				.map(audit -> {
					logger.info("AuditLog found: {}", audit);
					return ResponseEntity.ok(audit);
				})
				.orElseGet(() -> {
					logger.info("AuditLog not found: {}", id);
					return ResponseEntity.notFound().build();
				});
	}

	@GetMapping
	public ResponseEntity<Page<AuditLog>> getAll(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {

		PageRequest pageRequest = PageRequest.of(page, size);
		Page<AuditLog> result = auditService.findAll(pageRequest);

		logger.info("AuditLogs returned: {}", result.getNumberOfElements());
		return ResponseEntity.ok(result);
	}

	@GetMapping("/datetime")
	public ResponseEntity<String> datetime() {
		logger.info("DateTime requested");
		return ResponseEntity.ok(LocalDateTime.now().toString());
	}

	@GetMapping("/search")
	public ResponseEntity<Page<AuditLog>> searchByTarget(
			@RequestParam String target,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {

		PageRequest pageRequest = PageRequest.of(page, size);
		Page<AuditLog> result = auditService.findByTarget(target, pageRequest);

		logger.info("AuditLogs found for target {}: {}", target, result.getNumberOfElements());
		return ResponseEntity.ok(result);
	}

	@GetMapping("/{id}/verify")
	public ResponseEntity<String> verifyEntry(@PathVariable Long id) {

		Optional<Boolean> result = auditService.verifyEntry(id);

		if (result.isEmpty()) {
			return ResponseEntity.status(404).body("Audit entry " + id + " not found.");
		}

		if (result.get()) {
			return ResponseEntity.ok("Audit entry " + id + " is valid.");
		} else {
			return ResponseEntity.status(409).body("Audit entry " + id + " is INVALID.");
		}
	}

}
