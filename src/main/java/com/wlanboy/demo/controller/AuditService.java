package com.wlanboy.demo.controller;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.wlanboy.demo.model.AuditLog;
import com.wlanboy.demo.model.AuditMapper;
import com.wlanboy.demo.repository.AuditData;
import com.wlanboy.demo.repository.AuditRepositorySimple;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuditService {

	private static final Logger logger = LoggerFactory.getLogger(AuditService.class);

	private static final AtomicLong counter = new AtomicLong(0);

	private final AuditRepositorySimple auditDB;

	public AuditLog saveAuditLog(AuditLog audit) {
		AuditData entity = AuditMapper.toEntity(audit);
		entity.setCounter(counter.getAndIncrement());

		entity = auditDB.save(entity);
		logger.info("AuditLog created ( {} ).", entity.getId());

		return AuditMapper.toModel(entity);
	}

	public Optional<AuditLog> findById(Long id) {
		logger.info("AuditData byId: ( {} )", id);

		return auditDB.findById(id)
				.map(AuditMapper::toModel);
	}

	public Page<AuditLog> findAll(PageRequest pageRequest) {
		Page<AuditData> entityPage = auditDB.findAll(pageRequest);

		logger.info("AuditLogs found ( {} ).", entityPage.getNumberOfElements());

		return entityPage.map(AuditMapper::toModel);
	}

	public Page<AuditLog> findByTarget(String target, PageRequest pageRequest) {
		Page<AuditData> entityPage = auditDB.findAllByTarget(target, pageRequest);

		logger.info("AuditLogs found for target '{}': {}", target, entityPage.getNumberOfElements());

		return entityPage.map(AuditMapper::toModel);
	}

}
