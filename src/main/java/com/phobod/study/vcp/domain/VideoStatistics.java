package com.phobod.study.vcp.domain;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@RedisHash("statistics")
public class VideoStatistics {
	@Id
	private String videoId;
	@Indexed
	private String date;
	private String title;
	private long viewCount;
	private Set<String> userIdSet = new HashSet<>();
	private Set<String> ipAddressSet = new HashSet<>();
	@TimeToLive
	private Long expiration;

	public VideoStatistics() {
		super();
	}

	public VideoStatistics(String videoId) {
		super();
		this.videoId = videoId;
	}

	public VideoStatistics(String videoId, String title, long viewCount) {
		super();
		this.videoId = videoId;
		this.title = title;
		this.viewCount = viewCount;
		;
	}

	public String getVideoId() {
		return videoId;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public long getViewCount() {
		return viewCount;
	}

	public void setViewCount(long viewCount) {
		this.viewCount = viewCount;
	}

	public Set<String> getUserIdSet() {
		return userIdSet;
	}

	public void setUserIdSet(Set<String> userIdSet) {
		this.userIdSet = userIdSet;
	}

	public void addUserId(String userId) {
		this.userIdSet.add(userId);
	}

	public Set<String> getIpAddressSet() {
		return ipAddressSet;
	}

	public void setIpAddressSet(Set<String> ipAddressSet) {
		this.ipAddressSet = ipAddressSet;
	}

	public void addIpAddress(String ipAddress) {
		this.ipAddressSet.add(ipAddress);
	}

	public Long getExpiration() {
		return expiration;
	}

	public void setExpiration(Long expiration) {
		this.expiration = expiration;
	}

}
