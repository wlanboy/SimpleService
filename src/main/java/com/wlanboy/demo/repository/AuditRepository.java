package com.wlanboy.demo.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditRepository extends JpaRepository<AuditData, Long> {

	Page<AuditData> findAllByTarget(String target, Pageable pageable);

	Optional<AuditData> findTopByOrderByIdDesc();
	Optional<AuditData> findByHash(String hash);

}
