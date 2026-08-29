package com.wlanboy.demo.model;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import org.springframework.stereotype.Component;
import com.wlanboy.demo.repository.AuditData;

@Component
public class AuditMapper {

    public static final String GENESIS_HASH = "GENESIS";

    public AuditData toEntity(AuditLog log, Optional<AuditData> previousEntry) {
        if (log == null) return null;

        String previousHash = previousEntry.map(AuditData::getHash).orElse(GENESIS_HASH);
        long counter = previousEntry.map(e -> e.getCounter() + 1).orElse(0L);
        String hash = generateHash(new HashInput(log.getTarget(), log.getStatus(), counter, previousHash));

        return AuditData.builder()
                .target(log.getTarget())
                .status(log.getStatus())
                .previousHash(previousHash)
                .hash(hash)
                .counter(counter)
                .build();
    }

    public AuditLog toModel(AuditData data) {
        if (data == null) return null;

        return AuditLog.builder()
                .identifier(data.getId())
                .target(data.getTarget())
                .status(data.getStatus())
                .hash(data.getHash())
                .previousHash(data.getPreviousHash())
                .createDateTime(data.getCreateDateTime())
                .updateDateTime(data.getUpdateDateTime())
                .counter(data.getCounter())
                .build();
    }

    public static String generateHash(HashInput hashInput) {
        String input = hashInput.target() + hashInput.status()
                + (hashInput.counter() != null ? hashInput.counter() : 0L) + hashInput.previousHash();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(64);
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
