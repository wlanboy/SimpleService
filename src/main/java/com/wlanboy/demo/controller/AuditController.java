package com.wlanboy.demo.controller;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.wlanboy.demo.model.AuditLog;

@RestController
public class AuditController {

	private static final Logger logger = LoggerFactory.getLogger(AuditController.class);

	@Autowired
	private AuditService auditService;

	@PostMapping(value = "/audit")
	public HttpEntity<AuditLog> auditPost(@RequestBody AuditLog audit) {

		AuditLog auditResponse = auditService.saveAuditLog(audit);

		return new ResponseEntity<>(auditResponse, HttpStatus.CREATED);
	}

	@GetMapping(value = "/audit/{identifier}")
	public ResponseEntity<AuditLog> getById(@PathVariable(value = "identifier") Long identifier) {

		Optional<AuditLog> auditResponse = auditService.findById(identifier);

		if (auditResponse.isPresent()) {
			logger.info("AuditLog found ( {} )", auditResponse.get());
			return ResponseEntity.ok(auditResponse.get());
		} else {
			logger.info("AuditLog not found ( {} )", identifier);
			return ResponseEntity.notFound().build();
		}

	}

	@GetMapping(value = "/audit")
	public ResponseEntity<Page<AuditLog>> getAll(@RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size, UriComponentsBuilder uriBuilder) {

		PageRequest pagerequest = PageRequest.of(page.orElse(0), size.orElse(10));

		Page<AuditLog> auditResponse = auditService.findAll(pagerequest);
		if (auditResponse != null) {
			logger.info("AuditLogs found found {}",auditResponse.getNumberOfElements());
			return ResponseEntity.ok(auditResponse);
		} else {
			logger.info("AuditLogs not found.");
			return ResponseEntity.notFound().build();
		}

	}

	/**
	 * http://127.0.0.1:8001/datetime
	 * 
	 * @return String template
	 */
	@GetMapping(value = "/datetime")
	public HttpEntity<String> datetime() {

		logger.info("DateTime created.");
		LocalDateTime now = LocalDateTime.now();
		return new ResponseEntity<>(now.toString(), HttpStatus.OK);
	}

}