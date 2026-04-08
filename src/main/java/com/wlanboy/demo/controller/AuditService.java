package com.wlanboy.demo.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wlanboy.demo.model.AuditLog;
import com.wlanboy.demo.model.AuditMapper;
import com.wlanboy.demo.repository.AuditData;
import com.wlanboy.demo.repository.AuditRepositorySimple;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuditService {

    private static final Logger logger = LoggerFactory.getLogger(AuditService.class);
    private final AuditRepositorySimple auditDB;
    private final AuditMapper auditMapper;

    @Transactional
    public AuditLog saveAuditLog(AuditLog audit) {
        Optional<AuditData> lastEntry = auditDB.findTopByOrderByIdDesc();
        String previousHash = lastEntry.map(AuditData::getHash).orElse("GENESIS");
        long nextCounter = lastEntry.map(e -> e.getCounter() + 1).orElse(0L);

        audit.setCounter(nextCounter);

        AuditData entity = auditMapper.toEntity(audit, previousHash);
        entity = auditDB.save(entity);

        logger.info("AuditLog created with ID: {}", entity.getId());
        return auditMapper.toModel(entity);
    }

    public Optional<AuditLog> findById(Long id) {
        return auditDB.findById(id).map(auditMapper::toModel);
    }

    public Page<AuditLog> findAll(PageRequest pageRequest) {
        return auditDB.findAll(pageRequest).map(auditMapper::toModel);
    }

    public Page<AuditLog> findByTarget(String target, PageRequest pageRequest) {
        return auditDB.findAllByTarget(target, pageRequest).map(auditMapper::toModel);
    }

    public Optional<Boolean> verifyEntry(Long id) {
        Optional<AuditData> opt = auditDB.findById(id);
        if (opt.isEmpty()) return Optional.empty();

        AuditData entry = opt.get();

        // 1. Previous-Hash-Existenz prüfen (Genesis ist selbstreferenziell)
        if (!"GENESIS".equals(entry.getPreviousHash())) {
            boolean prevExists = auditDB.findByHash(entry.getPreviousHash()).isPresent();
            if (!prevExists) return Optional.of(false);
        }

        // 2. Daten-Integrität prüfen (SHA-256)
        String expected = AuditMapper.generateHash(
                entry.getTarget(), entry.getStatus(), entry.getCounter(), entry.getPreviousHash());
        return Optional.of(expected.equals(entry.getHash()));
    }
}
