package com.phobod.study.vcp.service.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.phobod.study.vcp.domain.Video;
import com.phobod.study.vcp.form.VideoUploadForm;
import com.phobod.study.vcp.repository.storage.VideoRepository;
import com.phobod.study.vcp.service.VideoProcessorService;

@Service("asyncVideoProcessorService")
public class AsyncVideoProcessorService implements VideoProcessorService{
	private ExecutorService executorService = Executors.newFixedThreadPool(3);
	
	@Autowired
	@Qualifier("simpleVideoProcessorService")
	private VideoProcessorService simpleVideoProcessorService;
	
	@Autowired
	private VideoRepository videoRepository;
	
	@PreDestroy
	private void preDestroy(){
		executorService.shutdown();
	}
	
	@Override
	public Video processVideo(VideoUploadForm uploadForm) {
		Video video = new Video("/static/img/thumbnail-stub.jpg", "/static/img/thumbnail-stub.jpg", "/static/img/thumbnail-stub.jpg", "/static/video/video-stub.mp4");
		executorService.submit(new VideoItem(uploadForm, video));
		return video;
	}

	private class VideoItem implements Runnable{
		private VideoUploadForm uploadForm;
		private Video video;
		
		public VideoItem(VideoUploadForm uploadForm, Video video) {
			super();
			this.uploadForm = uploadForm;
			this.video = video;
		}

		@Override
		public void run() {
			Video processedVideo = simpleVideoProcessorService.processVideo(uploadForm);
			video.setVideoUrl(processedVideo.getVideoUrl());
			video.setThumbnailUrl(processedVideo.getThumbnailUrl());
			video.setThumbnailUrlMedium(processedVideo.getThumbnailUrlMedium());
			video.setThumbnailUrlSmall(processedVideo.getThumbnailUrlSmall());
			videoRepository.save(video);
		}
		
	}
}
