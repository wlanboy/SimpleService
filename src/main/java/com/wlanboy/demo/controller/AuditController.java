package com.wlanboy.demo.controller;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.wlanboy.demo.model.AuditLog;
import com.wlanboy.demo.model.AuditMapper;
import com.wlanboy.demo.repository.AuditData;
import com.wlanboy.demo.repository.AuditRepository;

@RestController
public class AuditController {

	private static final Logger logger = Logger.getLogger(AuditController.class.getCanonicalName());

	static AtomicInteger counter = new AtomicInteger(0);

	@Autowired
	private AuditRepository auditDB;

	@RequestMapping(value = "/audit", method = RequestMethod.POST)
	public HttpEntity<AuditLog> auditPost(@RequestBody AuditLog audit) {

		AuditData entity = AuditMapper.getAuditData(audit);
		entity.setCounter(Long.valueOf(counter.getAndIncrement()));

		entity = auditDB.save(entity);
		logger.info(String.format("AuditLog created ( %s ).", entity.getId()));
		AuditLog auditResponse = AuditMapper.getAuditLog(entity);

		return new ResponseEntity<AuditLog>(auditResponse, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/audit/{identifier}", method = RequestMethod.GET)
	public ResponseEntity<AuditLog> getById(@PathVariable(value = "identifier") Long identifier) {

		logger.info("ID: (" + identifier + ").");

		Optional<AuditData> entity = auditDB.findById(identifier);
		if (entity.isPresent()) {
			logger.info(String.format("AuditLog found ( %s ).", identifier));

			AuditLog auditResponse = AuditMapper.getAuditLog(entity.get());
			return ResponseEntity.ok(auditResponse);
		} else {
			logger.info(String.format("AuditLog not found ( %s ).", identifier));
			return ResponseEntity.notFound().build();
		}

	}

	@RequestMapping(value = "/audit", method = RequestMethod.GET)
	public ResponseEntity<Page<AuditLog>> getAll(@RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size, UriComponentsBuilder uriBuilder) {

		
		PageRequest pagerequest = PageRequest.of(page.orElse(0), size.orElse(10));

		Page<AuditData> entity = auditDB.findAll(pagerequest);
		if (entity != null) {
			logger.info(String.format("AuditLogs found ( %s ).", entity.getNumberOfElements()));

			Page<AuditLog> auditResponse = entity.map(data -> AuditMapper.getAuditLog(data));
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
	@RequestMapping(value = "/datetime", method = RequestMethod.GET)
	public HttpEntity<String> datetime() {

		logger.info("DateTime created.");
		LocalDateTime now = LocalDateTime.now();
		return new ResponseEntity<String>(now.toString(), HttpStatus.OK);
	}

}