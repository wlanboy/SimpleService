package com.wlanboy.demo.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

	Optional<AuditData> findTopByOrderByIdDesc();
	List<AuditData> findAllByOrderByIdAsc();
	Optional<AuditData> findById(Long id);
	Optional<AuditData> findByHash(String hash);

}
