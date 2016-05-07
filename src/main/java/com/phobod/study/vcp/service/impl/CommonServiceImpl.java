package com.phobod.study.vcp.service.impl;

import java.util.List;
import java.util.UUID;

import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.MatchQueryBuilder.Operator;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.phobod.study.vcp.domain.User;
import com.phobod.study.vcp.domain.Video;
import com.phobod.study.vcp.exception.CantProcessPasswordRestoreException;
import com.phobod.study.vcp.form.RecoveryForm;
import com.phobod.study.vcp.repository.search.VideoSearchRepository;
import com.phobod.study.vcp.repository.storage.UserRepository;
import com.phobod.study.vcp.repository.storage.VideoRepository;
import com.phobod.study.vcp.service.CommonService;
import com.phobod.study.vcp.service.NotificationService;

@Service
public class CommonServiceImpl implements CommonService {
	@Value("${vcp.url}")
	private String vcpUrl;
	
	@Autowired
	private VideoRepository videoRepository;
	
	@Autowired
	private VideoSearchRepository videoSearchRepository;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private NotificationService notificationService;
	
	@Override
	public List<Video> listPopularVideos() {
		return videoRepository.findTop3ByOrderByViewsDesc();
	}
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	public Page<Video> listAllVideos(Pageable pageable) {
		return videoRepository.findAll(pageable);
	}

	@Override
	public Video findVideoById(String id) {
		return videoRepository.findOne(id);
	}

	@Override
	public Page<Video> listVideosBySearchQuery(String query, Pageable pageable) {
		SearchQuery searchQuery = new NativeSearchQueryBuilder()
				.withQuery(QueryBuilders.multiMatchQuery(query)
						.field("title")
						.field("owner.company.name")
						.field("owner.name")
						.field("owner.surname")
						.type(MultiMatchQueryBuilder.Type.BEST_FIELDS)
						.fuzziness(Fuzziness.TWO)
						.operator(Operator.OR))
				.withSort(SortBuilders.fieldSort("views").order(SortOrder.DESC))
				.build();
		searchQuery.setPageable(pageable);
		return videoSearchRepository.search(searchQuery);
	}

	@Override
	public void sendRestoreEmail(String login) {
		try {
			processSendRestoreEmail(login);
		} catch (Exception e) {
			throw new CantProcessPasswordRestoreException("Sending email for password recovery failed for login: " + login + ". " + e.getMessage(), e);
		}
	}

	private void processSendRestoreEmail(String login) {
		User user = userRepository.findByLogin(login);
		String hash = UUID.randomUUID().toString();
		user.setHash(hash);
		userRepository.save(user);
		String restoreLink = vcpUrl + "/#/recovery/acsess/" + user.getId() + "/" + hash;
		notificationService.sendRestoreAccessLink(user, restoreLink);
	}

	@Override
	public void restorePassword(RecoveryForm form) {
		try {
			processPasswordRestoreRequest(form);
		} catch (Exception e) {
			throw new CantProcessPasswordRestoreException("The password recovery process failed for userId: " + form.getId() + ". " + e.getMessage(), e);
		}
	}

	private void processPasswordRestoreRequest(RecoveryForm form) {
		User user = userRepository.findOne(form.getId());
		if (user.getHash() == null || form.getHash() == null || !user.getHash().equals(form.getHash())) {
			throw new CantProcessPasswordRestoreException("Hash is Null or is not correct.");
		}
		user.setPassword(passwordEncoder.encode(form.getPassword()));
		user.setHash(null);
		userRepository.save(user);
		
	}
	
}
