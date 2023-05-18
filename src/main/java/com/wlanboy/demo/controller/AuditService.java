package com.wlanboy.demo.controller;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.wlanboy.demo.model.AuditLog;
import com.wlanboy.demo.model.AuditMapper;
import com.wlanboy.demo.repository.AuditData;
import com.wlanboy.demo.repository.AuditRepositorySimple;

@Service
public class AuditService {

	private static final Logger logger = LoggerFactory.getLogger(AuditService.class);
	
	static AtomicInteger counter = new AtomicInteger(0);

	@Autowired
	private AuditRepositorySimple auditDB;

	public AuditLog saveAuditLog(AuditLog audit) {
		AuditData entity = AuditMapper.getAuditData(audit);
		entity.setCounter(Long.valueOf(counter.getAndIncrement()));

		entity = auditDB.save(entity);
		logger.info("AuditLog created ( {} ).", entity.getId());

		return AuditMapper.getAuditLog(entity);
	}

	public Optional<AuditLog> findById(Long identifier) {
		logger.info("AuditData byId: ( {} )",identifier);

		Optional<AuditData> entity = auditDB.findById(identifier);
		
		Optional<AuditLog> auditResponse = Optional.empty();
		
		if (entity.isPresent())
			auditResponse = Optional.of(AuditMapper.getAuditLog(entity.get()));
		return auditResponse;
	}

	public Page<AuditLog> findAll(PageRequest pagerequest) {
		Page<AuditLog> auditResponse = null;
		
		Page<AuditData> entity = auditDB.findAll(pagerequest);
		logger.info("AuditLogs found ( {} ).", entity.getNumberOfElements());
		auditResponse = entity.map(AuditMapper::getAuditLog);

		return auditResponse;
	}
}
