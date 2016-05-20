package com.phobod.study.vcp.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.phobod.study.vcp.domain.User;
import com.phobod.study.vcp.domain.Video;
import com.phobod.study.vcp.form.VideoUploadForm;
import com.phobod.study.vcp.repository.search.VideoSearchRepository;
import com.phobod.study.vcp.repository.storage.VideoRepository;
import com.phobod.study.vcp.service.UserService;
import com.phobod.study.vcp.service.VideoProcessorService;

@Service
public class UserServiceImpl implements UserService {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
	
	@Value("${media.dir}")
	private String mediaDir;

	@Autowired
	private VideoRepository videoRepository;

	@Autowired
	private VideoSearchRepository videoSearchRepository;
	
	@Autowired
	@Qualifier("asyncVideoProcessorService")
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
	public void updateVideo(String videoId, VideoUploadForm form, User user) throws AccessDeniedException{
		Video video = videoRepository.findOne(videoId);
		if (!video.getOwner().getId().equals(user.getId())) {
			throw new AccessDeniedException(String.format("User %s denied access to edit video %s. This user is not the owner of the video.", user.getLogin(), video.getId()));
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
	public void deleteVideo(String videoId, User user) throws AccessDeniedException{
		Video video = videoRepository.findOne(videoId);
		if (!video.getOwner().getId().equals(user.getId())) {
			throw new AccessDeniedException(String.format("User %s denied access to delete video %s. This user is not the owner of the video.", user.getLogin(), video.getId()));
		}
		videoRepository.delete(videoId);
		videoSearchRepository.delete(videoId);
		deleteMediaData(video);
	}

	@Override
	public void deleteAllVideosByUser(String userId) {
		for (Video video : videoRepository.findByOwnerId(userId)) {
			deleteMediaData(video);
		}
		videoRepository.deleteByOwnerId(userId);
		videoSearchRepository.deleteByOwnerId(userId);
	}
	
	private void deleteMediaData(Video video){
		deleteFile(video.getVideoUrl()); 
		deleteFile(video.getThumbnailUrl()); 
		deleteFile(video.getThumbnailUrlMedium()); 
		deleteFile(video.getThumbnailUrlSmall()); 
	}
	
	private void deleteFile(String fileUrl){
		if (!fileUrl.startsWith("/media/thumbnail/") && !fileUrl.startsWith("/media/video/")) {
			return;
		}
		Path path = Paths.get(mediaDir + fileUrl.replaceFirst("/media", ""));
		try {
			Files.deleteIfExists(path);
		} catch (IOException e) {
			LOGGER.warn("Can't delete file: " + path, e);
		}
	}

}
