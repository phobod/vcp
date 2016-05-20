package com.phobod.study.vcp.domain;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Field;

@org.springframework.data.mongodb.core.mapping.Document
@org.springframework.data.elasticsearch.annotations.Document(indexName="video")
public class Video implements Serializable{
	private static final long serialVersionUID = 4705714831192040100L;
	
	@Id
	private String id;
	private String title;
	private String description;
	@Field("thumbnail-url")
	private String thumbnailUrl;
	@Field("thumbnail-url-medium")
	private String thumbnailUrlMedium;
	@Field("thumbnail-url-small")
	private String thumbnailUrlSmall;
	@Field("video-url")
	private String videoUrl;
	@Indexed
	private int views;
	@DBRef
	@Indexed
	private User owner;

	public Video() {
		super();
	}

	public Video(String thumbnailUrl, String thumbnailUrlMedium, String thumbnailUrlSmall, String videoUrl) {
		super();
		this.thumbnailUrl = thumbnailUrl;
		this.thumbnailUrlMedium = thumbnailUrlMedium;
		this.thumbnailUrlSmall = thumbnailUrlSmall;
		this.videoUrl = videoUrl;
	}

	public Video(String title, String description, String thumbnailUrl, String thumbnailUrlMedium, String thumbnailUrlSmall, String videoUrl) {
		super();
		this.title = title;
		this.description = description;
		this.thumbnailUrl = thumbnailUrl;
		this.thumbnailUrlMedium = thumbnailUrlMedium;
		this.thumbnailUrlSmall = thumbnailUrlSmall;
		this.videoUrl = videoUrl;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getViews() {
		return views;
	}

	public void setViews(int views) {
		this.views = views;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}


	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public String getThumbnailUrlMedium() {
		return thumbnailUrlMedium;
	}

	public void setThumbnailUrlMedium(String thumbnailUrlMedium) {
		this.thumbnailUrlMedium = thumbnailUrlMedium;
	}

	public String getThumbnailUrlSmall() {
		return thumbnailUrlSmall;
	}

	public void setThumbnailUrlSmall(String thumbnailUrlSmall) {
		this.thumbnailUrlSmall = thumbnailUrlSmall;
	}

	@Override
	public String toString() {
		return String.format("Video [id=%s, title=%s]", id, title);
	}
}
