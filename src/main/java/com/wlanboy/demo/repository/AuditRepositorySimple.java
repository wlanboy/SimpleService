package com.wlanboy.demo.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditRepositorySimple extends JpaRepository<AuditData, Long> {

	Page<AuditData> findAllByTarget(String target, Pageable pageable);

	Page<AuditData> findAllByStatus(String status, Pageable pageable);

	Page<AuditData> findAllByTargetAndStatus(String target, String status, Pageable pageable);

	Page<AuditData> findAllByCreateDateTimeBetween(
			LocalDateTime start,
			LocalDateTime end,
			Pageable pageable);

}
