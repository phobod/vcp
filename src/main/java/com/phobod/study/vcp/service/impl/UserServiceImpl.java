package com.phobod.study.vcp.service.impl;

import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.phobod.study.vcp.component.UploadVideoTempStorage;
import com.phobod.study.vcp.domain.User;
import com.phobod.study.vcp.domain.Video;
import com.phobod.study.vcp.form.UploadForm;
import com.phobod.study.vcp.repository.storage.VideoRepository;
import com.phobod.study.vcp.service.ImageService;
import com.phobod.study.vcp.service.ThumbnailService;
import com.phobod.study.vcp.service.UserService;
import com.phobod.study.vcp.service.VideoService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private VideoRepository videoRepository;

	@Autowired
	private VideoService videoService;

	@Autowired
	private ImageService imageService;

	@Autowired
	private ThumbnailService thumbnailService;

	@Autowired
	private UploadVideoTempStorage uploadVideoTempStorage;

	@Override
	public Page<Video> listVideosByUser(Pageable pageable, String userId) {
		return videoRepository.findByOwnerIdOrderByViewsDesc(pageable, userId); 
	}

	@Override
	public Page<Video> listVideosByUserExcludeOne(Pageable pageable, String excludedVideoId, String userId) {
		return videoRepository.findByIdNotAndOwnerIdOrderByViewsDesc(pageable, excludedVideoId, userId); 
	}

	@Override
	public void uploadVideo(User currentUser, UploadForm form) {
		Path tempUploadedVideoPath = uploadVideoTempStorage.getTempUploadedVideoPath();
		String videoUrl = videoService.saveVideo(tempUploadedVideoPath);
		byte[] thumbnailImageData = thumbnailService.createThumbnail(tempUploadedVideoPath);
		String thumbnailImageUrl = imageService.saveImageData(thumbnailImageData);
		Video video = new Video(form.getTitle(), form.getDescription(), thumbnailImageUrl, videoUrl, currentUser);
		videoRepository.save(video);
	}

}
