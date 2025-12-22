package com.wlanboy.demo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditRepository extends JpaRepository<AuditData, Long> {

    Page<AuditData> findAllByTarget(String target, Pageable pageable);
}
