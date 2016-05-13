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
import com.phobod.study.vcp.exception.CantProcessAccessRecoveryException;
import com.phobod.study.vcp.form.RecoveryForm;
import com.phobod.study.vcp.repository.search.VideoSearchRepository;
import com.phobod.study.vcp.repository.storage.UserRepository;
import com.phobod.study.vcp.repository.storage.VideoRepository;
import com.phobod.study.vcp.service.CommonService;
import com.phobod.study.vcp.service.NotificationService;
import com.phobod.study.vcp.service.VideoStatisticsService;

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
	
	@Autowired
	private VideoStatisticsService videoStatisticsService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	public List<Video> listPopularVideos() {
		return videoRepository.findTop3ByOrderByViewsDesc();
	}
	
	@Override
	public Page<Video> listAllVideos(Pageable pageable) {
		return videoRepository.findAll(pageable);
	}

	@Override
	public Video findVideoById(String videoId, User user, String userIP) {
		Video video = videoRepository.findOne(videoId);
		increaseVideoViewCount(video);
		videoStatisticsService.saveVideoViewStatistics(video, user, userIP);
		return video;
	}

	private void increaseVideoViewCount(Video video) {
		video.setViews(video.getViews() + 1);
		videoRepository.save(video);
		videoSearchRepository.save(video);
	}

	@Override
	public Page<Video> listVideosBySearchQuery(String query, Pageable pageable) {
		SearchQuery searchQuery = prepareSearchQuery(query, pageable);
		return videoSearchRepository.search(searchQuery);
	}

	private SearchQuery prepareSearchQuery(String query, Pageable pageable) {
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
		return searchQuery;
	}

	@Override
	public void sendRestoreEmail(String login) throws CantProcessAccessRecoveryException{
		try {
			sendRestoreEmailInternal(login);
		} catch (Exception e) {
			throw new CantProcessAccessRecoveryException("Sending email for password recovery failed for login: " + login + ". " + e.getMessage(), e);
		}
	}

	private void sendRestoreEmailInternal(String login) {
		User user = userRepository.findByLogin(login);
		addHashToUser(user);
		String restoreLink = vcpUrl + "/#/recovery/acsess/" + user.getId() + "/" + user.getHash();
		notificationService.sendRestoreAccessLink(user, restoreLink);
	}

	private void addHashToUser(User user) {
		user.setHash(generateUniqueHash());
		userRepository.save(user);
	}

	private String generateUniqueHash() {
		return UUID.randomUUID().toString();
	}
	
	@Override
	public void restorePassword(RecoveryForm form) throws CantProcessAccessRecoveryException{
		try {
			restorePasswordInternal(form);
		} catch (Exception e) {
			throw new CantProcessAccessRecoveryException("The password recovery process failed for userId: " + form.getId() + ". " + e.getMessage(), e);
		}
	}

	private void restorePasswordInternal(RecoveryForm form) {
		User user = userRepository.findOne(form.getId());
		if (user.getHash() == null || form.getHash() == null || !user.getHash().equals(form.getHash())) {
			throw new CantProcessAccessRecoveryException("Hash is Null or is not correct.");
		}
		user.setPassword(passwordEncoder.encode(form.getPassword()));
		user.setHash(null);
		userRepository.save(user);
	}
	
}
