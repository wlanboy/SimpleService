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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/audit")
@Tag(name = "Audit", description = "Verwaltung und Abfrage von unveränderlichen Audit-Log-Einträgen mit Hash-Kette")
public class AuditController {

	private static final Logger logger = LoggerFactory.getLogger(AuditController.class);

	private final AuditService auditService;

	@Operation(summary = "Audit-Eintrag erstellen", description = "Legt einen neuen Audit-Log-Eintrag an und gibt ihn mit vergebener ID zurück.")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "Eintrag erfolgreich erstellt",
			content = @Content(schema = @Schema(implementation = AuditLog.class))),
		@ApiResponse(responseCode = "400", description = "Ungültige Eingabedaten (Validierungsfehler)", content = @Content)
	})
	@PostMapping
	public ResponseEntity<AuditLog> createAudit(@Valid @RequestBody AuditLog audit) {
		AuditLog saved = auditService.saveAuditLog(audit);
		logger.info("AuditLog created with ID {}", saved.getIdentifier());
		return ResponseEntity.status(201).body(saved);
	}

	@Operation(summary = "Audit-Eintrag per ID abrufen", description = "Gibt einen einzelnen Audit-Log-Eintrag anhand seiner Datenbank-ID zurück.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Eintrag gefunden",
			content = @Content(schema = @Schema(implementation = AuditLog.class))),
		@ApiResponse(responseCode = "404", description = "Kein Eintrag mit dieser ID vorhanden", content = @Content)
	})
	@GetMapping("/{id}")
	public ResponseEntity<AuditLog> getById(
			@Parameter(description = "Datenbank-ID des Audit-Eintrags", required = true, example = "1")
			@PathVariable Long id) {
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

	@Operation(summary = "Alle Audit-Einträge abrufen", description = "Gibt alle Audit-Log-Einträge paginiert zurück.")
	@ApiResponse(responseCode = "200", description = "Seite mit Audit-Einträgen",
		content = @Content(schema = @Schema(implementation = Page.class)))
	@GetMapping
	public ResponseEntity<Page<AuditLog>> getAll(
			@Parameter(description = "Seitennummer (0-basiert)", example = "0") @RequestParam(defaultValue = "0") int page,
			@Parameter(description = "Einträge pro Seite", example = "10") @RequestParam(defaultValue = "10") int size) {

		PageRequest pageRequest = PageRequest.of(page, size);
		Page<AuditLog> result = auditService.findAll(pageRequest);

		logger.info("AuditLogs returned: {}", result.getNumberOfElements());
		return ResponseEntity.ok(result);
	}

	@Operation(summary = "Serverzeit abrufen", description = "Gibt die aktuelle Serverzeit als ISO-8601-String zurück.")
	@ApiResponse(responseCode = "200", description = "Aktuelle Serverzeit",
		content = @Content(schema = @Schema(type = "string", example = "2026-02-23T10:15:30.123456")))
	@GetMapping("/datetime")
	public ResponseEntity<String> datetime() {
		logger.info("DateTime requested");
		return ResponseEntity.ok(LocalDateTime.now().toString());
	}

	@Operation(summary = "Audit-Einträge nach Ziel suchen", description = "Gibt alle Audit-Log-Einträge zurück, die dem angegebenen Ziel (target) entsprechen, paginiert.")
	@ApiResponse(responseCode = "200", description = "Seite mit gefundenen Audit-Einträgen",
		content = @Content(schema = @Schema(implementation = Page.class)))
	@GetMapping("/search")
	public ResponseEntity<Page<AuditLog>> searchByTarget(
			@Parameter(description = "Zielbezeichner, nach dem gefiltert wird", required = true, example = "user-service") @RequestParam String target,
			@Parameter(description = "Seitennummer (0-basiert)", example = "0") @RequestParam(defaultValue = "0") int page,
			@Parameter(description = "Einträge pro Seite", example = "10") @RequestParam(defaultValue = "10") int size) {

		PageRequest pageRequest = PageRequest.of(page, size);
		Page<AuditLog> result = auditService.findByTarget(target, pageRequest);

		logger.info("AuditLogs found for target {}: {}", target, result.getNumberOfElements());
		return ResponseEntity.ok(result);
	}

	@Operation(summary = "Audit-Eintrag verifizieren", description = "Prüft die Hash-Integrität eines Audit-Eintrags. Gibt 200 zurück wenn der Hash gültig ist, 409 wenn er manipuliert wurde.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Hash-Prüfung erfolgreich — Eintrag ist integer",
			content = @Content(schema = @Schema(type = "string", example = "Audit entry 1 is valid."))),
		@ApiResponse(responseCode = "404", description = "Kein Eintrag mit dieser ID vorhanden",
			content = @Content(schema = @Schema(type = "string", example = "Audit entry 1 not found."))),
		@ApiResponse(responseCode = "409", description = "Hash-Prüfung fehlgeschlagen — Eintrag wurde manipuliert",
			content = @Content(schema = @Schema(type = "string", example = "Audit entry 1 is INVALID.")))
	})
	@GetMapping("/{id}/verify")
	public ResponseEntity<String> verifyEntry(
			@Parameter(description = "Datenbank-ID des zu prüfenden Audit-Eintrags", required = true, example = "1")
			@PathVariable Long id) {

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
