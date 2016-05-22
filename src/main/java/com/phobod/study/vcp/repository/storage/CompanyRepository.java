package com.phobod.study.vcp.repository.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.phobod.study.vcp.domain.Company;

public interface CompanyRepository extends PagingAndSortingRepository<Company, String>{
	Page<Company> findAllByOrderByNameAsc(Pageable pageable);
}
