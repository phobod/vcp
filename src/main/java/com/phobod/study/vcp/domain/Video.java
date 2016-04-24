package com.phobod.study.vcp.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document
public class Video {
	@Id
	private String id;
	@Indexed
	private String title;
	private String description;
	@Field("thumbnail-url")
	private String thumbnailUrl;
	@Field("video-url")
	private String videoUrl;
	private int views;
	@DBRef
	private User owner;

	public Video() {
		super();
	}

	public Video(String title, String description, String thumbnailUrl, String videoUrl, int views, User owner) {
		super();
		this.title = title;
		this.description = description;
		this.thumbnailUrl = thumbnailUrl;
		this.videoUrl = videoUrl;
		this.views = views;
		this.owner = owner;
	}



	public Video(String title, String description, String thumbnailUrl, String videoUrl, User owner) {
		this(title, description, thumbnailUrl, videoUrl, 0, owner);
	}

	public Video(String title, String description, String thumbnailUrl, String videoUrl) {
		super();
		this.title = title;
		this.description = description;
		this.thumbnailUrl = thumbnailUrl;
		this.videoUrl = videoUrl;
	}

	public Video(String thumbnailUrl, String videoUrl) {
		super();
		this.thumbnailUrl = thumbnailUrl;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Video other = (Video) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("Video [id=%s, title=%s]", id, title);
	}
}
