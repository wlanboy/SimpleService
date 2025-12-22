package com.wlanboy.demo.controller;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCrypt;
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
		Optional<AuditData> last = auditDB.findTopByOrderByIdDesc();
		String previousHash = last.get().getHash();

		audit.setCounter(counter.getAndIncrement());
		AuditData entity = AuditMapper.toEntity(audit, previousHash);

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

	public Optional<Boolean> verifyEntry(Long id) {

		Optional<AuditData> opt = auditDB.findById(id);
		if (opt.isEmpty()) {
			return Optional.empty(); // ← WICHTIG: Not found
		}

		AuditData entry = opt.get();

		// 1. Previous block prüfen
		if (!"GENESIS".equals(entry.getPreviousHash())) {

			Optional<AuditData> prev = auditDB.findByHash(entry.getPreviousHash());
			if (prev.isEmpty()) {
				return Optional.of(false);
			}

			if (!entry.getPreviousHash().equals(prev.get().getHash())) {
				return Optional.of(false);
			}
		}

		// 2. Hash prüfen
		String input = entry.getTarget()
				+ entry.getStatus()
				+ entry.getCounter()
				+ entry.getPreviousHash();

		boolean hashMatches = BCrypt.checkpw(input, entry.getHash());

		return Optional.of(hashMatches);
	}

}
