package com.phobod.study.vcp.repository.storage;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.phobod.study.vcp.domain.User;

public interface UserRepository extends PagingAndSortingRepository<User, String>{
	User findByLogin(String login);
	Page<User> findAllByOrderByNameAsc(Pageable pageable);
	List<User> findByCompanyId(String companyId);
}
