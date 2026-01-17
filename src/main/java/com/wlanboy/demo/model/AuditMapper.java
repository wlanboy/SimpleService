package com.wlanboy.demo.model;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;
import com.wlanboy.demo.repository.AuditData;

@Component // Ermöglicht @Autowired im Service
public class AuditMapper {

    public AuditData toEntity(AuditLog log, String previousHash) {
        if (log == null) return null;

        // Nutzt die Builder von AuditData (Lombok muss aktiv sein!)
        return AuditData.builder()
                .target(log.getTarget())
                .status(log.getStatus())
                .previousHash(previousHash)
                .hash(generateHash(log, previousHash))
                .counter(log.getCounter() != null ? log.getCounter() : 0L)
                .build();
    }

    public AuditLog toModel(AuditData data) {
        if (data == null) return null;

        // Nutzt den Builder von AuditLog
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

    private String generateHash(AuditLog log, String previousHash) {
        Long counter = log.getCounter() != null ? log.getCounter() : 0L;
        String input = log.getTarget() + log.getStatus() + counter + previousHash;
        return BCrypt.hashpw(input, BCrypt.gensalt());
    }
}