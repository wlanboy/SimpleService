package com.wlanboy.demo.controller;

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
    private final AuditRepositorySimple auditDB;
    // Injiziere den Mapper (nicht mehr statisch!), um Compiler-Fehler zu vermeiden
    private final AuditMapper auditMapper; 

    public AuditLog saveAuditLog(AuditLog audit) {
        // Robustere Abfrage des previousHash (verhindert NoSuchElementException bei leerer DB)
        String previousHash = auditDB.findTopByOrderByIdDesc()
                .map(AuditData::getHash)
                .orElse("GENESIS");

        // Counter basierend auf dem letzten DB-Eintrag setzen (sicherer als AtomicLong nach Neustart)
        long nextCounter = auditDB.count();
        audit.setCounter(nextCounter);

        // Instanz-Aufruf statt statischer Utility-Aufruf
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

        // 1. Previous Hash Integrität prüfen
        if (!"GENESIS".equals(entry.getPreviousHash())) {
            boolean prevExists = auditDB.findByHash(entry.getPreviousHash()).isPresent();
            if (!prevExists) return Optional.of(false);
        }

        // 2. Daten-Integrität prüfen (BCrypt Hash)
        String input = entry.getTarget() + entry.getStatus() + entry.getCounter() + entry.getPreviousHash();
        return Optional.of(BCrypt.checkpw(input, entry.getHash()));
    }
}