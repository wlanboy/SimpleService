package com.wlanboy.demo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditRepository extends PagingAndSortingRepository<AuditData, Long>{

	Page<AuditData> findAllByTarget(String target, Pageable pageable);
}
