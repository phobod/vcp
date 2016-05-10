package com.phobod.study.vcp.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.phobod.study.vcp.domain.User;
import com.phobod.study.vcp.domain.Video;
import com.phobod.study.vcp.form.VideoUploadForm;
import com.phobod.study.vcp.repository.search.VideoSearchRepository;
import com.phobod.study.vcp.repository.storage.VideoRepository;
import com.phobod.study.vcp.security.SecurityUtils;
import com.phobod.study.vcp.service.UserService;
import com.phobod.study.vcp.service.VideoProcessorService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private VideoRepository videoRepository;

	@Autowired
	private VideoSearchRepository videoSearchRepository;
	
	@Autowired
	@Qualifier("simpleVideoProcessorService")
	private VideoProcessorService videoProcessorService;

	@Override
	public Page<Video> listVideosByUser(Pageable pageable, String userId) {
		return videoRepository.findByOwnerIdOrderByViewsDesc(pageable, userId); 
	}

	@Override
	public Page<Video> listVideosByUserExcludeOne(Pageable pageable, String excludedVideoId, String userId) {
		return videoRepository.findByIdNotAndOwnerIdOrderByViewsDesc(pageable, excludedVideoId, userId); 
	}

	@Override
	public void uploadVideo(User currentUser, VideoUploadForm form) {
		Video video = videoProcessorService.processVideo(form);
		video.setOwner(currentUser);
		video.setTitle(form.getTitle());
		video.setDescription(form.getDescription());
		saveVideoToRepositories(video);
	}

	@Override
	public void updateVideo(String videoId, VideoUploadForm form, String userId) {
		Video video = videoRepository.findOne(videoId);
		if (!video.getOwner().getId().equals(userId)) {
			throw new AccessDeniedException(String.format("User %s denied access to edit video %s. This user is not the owner of the video.", SecurityUtils.getCurrentUser().getLogin(), video.getId()));
		}
		video.setTitle(form.getTitle());
		video.setDescription(form.getDescription());
		saveVideoToRepositories(video);
	}

	private void saveVideoToRepositories(Video video) {
		videoRepository.save(video);	
		videoSearchRepository.save(video);
	}

	@Override
	public void deleteVideo(String videoId, String userId) {
		Video video = videoRepository.findOne(videoId);
		if (!video.getOwner().getId().equals(userId)) {
			throw new AccessDeniedException(String.format("User %s denied access to delete video %s. This user is not the owner of the video.", SecurityUtils.getCurrentUser().getLogin(), video.getId()));
		}
		videoRepository.delete(videoId);
		videoSearchRepository.delete(videoId);
	}

	@Override
	public void deleteAllVideosByUser(String userId) {
		videoRepository.deleteByOwnerId(userId);
		videoSearchRepository.deleteByOwnerId(userId);
	}

}
