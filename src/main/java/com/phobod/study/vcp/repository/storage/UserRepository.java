package com.phobod.study.vcp.repository.storage;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.phobod.study.vcp.domain.User;

public interface UserRepository extends PagingAndSortingRepository<User, String>{
	User findByLogin(String login);
}
