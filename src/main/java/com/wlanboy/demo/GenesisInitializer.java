package com.wlanboy.demo;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wlanboy.demo.model.AuditMapper;
import com.wlanboy.demo.model.HashInput;
import com.wlanboy.demo.repository.AuditData;
import com.wlanboy.demo.repository.AuditRepository;

@Configuration
public class GenesisInitializer {

    @Bean
    public ApplicationRunner createGenesisBlock(AuditRepository auditDB) {
        return args -> {
            if (auditDB.count() == 0) {
                // Hash der Genesis-Block-Inhalte berechnen (gleiche Formel wie AuditMapper)
                String genesisHash = AuditMapper.generateHash(
                        new HashInput("GENESIS", "INIT", 0L, AuditMapper.GENESIS_HASH));

                AuditData genesis = AuditData.builder()
                        .target("GENESIS")
                        .status("INIT")
                        .counter(0L)
                        .previousHash(AuditMapper.GENESIS_HASH)
                        .hash(genesisHash)
                        .build();

                auditDB.save(genesis);
            }
        };
    }
}
