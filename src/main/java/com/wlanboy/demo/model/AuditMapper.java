package com.wlanboy.demo.model;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Component;
import com.wlanboy.demo.repository.AuditData;

@Component
public class AuditMapper {

    public AuditData toEntity(AuditLog log, String previousHash) {
        if (log == null) return null;

        return AuditData.builder()
                .target(log.getTarget())
                .status(log.getStatus())
                .previousHash(previousHash)
                .hash(generateHash(log.getTarget(), log.getStatus(), log.getCounter(), previousHash))
                .counter(log.getCounter() != null ? log.getCounter() : 0L)
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

    public static String generateHash(String target, String status, Long counter, String previousHash) {
        String input = target + status + (counter != null ? counter : 0L) + previousHash;
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
