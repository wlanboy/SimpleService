package com.wlanboy.demo.controller;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.wlanboy.demo.model.AuditLog;
import com.wlanboy.demo.model.AuditMapper;
import com.wlanboy.demo.repository.AuditData;
import com.wlanboy.demo.repository.AuditRepository;

@Service
public class AuditService {

	private static final Logger logger = Logger.getLogger(AuditService.class.getCanonicalName());
	
	static AtomicInteger counter = new AtomicInteger(0);

	@Autowired
	private AuditRepository auditDB;

	public AuditLog saveAuditLog(AuditLog audit) {
		AuditData entity = AuditMapper.getAuditData(audit);
		entity.setCounter(Long.valueOf(counter.getAndIncrement()));

		entity = auditDB.save(entity);
		logger.info(String.format("AuditLog created ( %s ).", entity.getId()));
		AuditLog auditResponse = AuditMapper.getAuditLog(entity);
		
		return auditResponse;
	}

	public Optional<AuditLog> findById(Long identifier) {
		logger.info("AuditData byId: (" + identifier + ").");

		Optional<AuditData> entity = auditDB.findById(identifier);
		
		Optional<AuditLog> auditResponse = Optional.empty();
		
		if (entity.isPresent())
			auditResponse = Optional.of(AuditMapper.getAuditLog(entity.get()));
		return auditResponse;
	}

	public Page<AuditLog> findAll(PageRequest pagerequest) {
		Page<AuditLog> auditResponse = null;
		
		Page<AuditData> entity = auditDB.findAll(pagerequest);
		if (entity != null) { 
			logger.info(String.format("AuditLogs found ( %s ).", entity.getNumberOfElements()));
			auditResponse = entity.map(data -> AuditMapper.getAuditLog(data));
		}
		return auditResponse;
	}
}
