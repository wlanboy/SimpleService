package com.wlanboy.demo;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wlanboy.demo.model.AuditMapper;
import com.wlanboy.demo.repository.AuditData;
import com.wlanboy.demo.repository.AuditRepositorySimple;

@Configuration
public class GenesisInitializer {

    private static final String GENESIS_SENTINEL = "GENESIS";

    @Bean
    public ApplicationRunner createGenesisBlock(AuditRepositorySimple auditDB) {
        return args -> {
            if (auditDB.count() == 0) {
                // Hash der Genesis-Block-Inhalte berechnen (gleiche Formel wie AuditMapper)
                String genesisHash = AuditMapper.generateHash("GENESIS", "INIT", 0L, GENESIS_SENTINEL);

                AuditData genesis = AuditData.builder()
                        .target("GENESIS")
                        .status("INIT")
                        .counter(0L)
                        .previousHash(GENESIS_SENTINEL)
                        .hash(genesisHash)
                        .build();

                auditDB.save(genesis);
            }
        };
    }
}
