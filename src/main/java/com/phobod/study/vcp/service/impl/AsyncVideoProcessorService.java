package com.phobod.study.vcp.service.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.phobod.study.vcp.domain.Video;
import com.phobod.study.vcp.form.VideoUploadForm;
import com.phobod.study.vcp.repository.storage.VideoRepository;
import com.phobod.study.vcp.service.VideoProcessorService;

@Service("asyncVideoProcessorService")
public class AsyncVideoProcessorService implements VideoProcessorService{
	private ExecutorService executorService;
	
	@Value("${thread.pool.count}")
	private int threadPoolCount;
	
	@Autowired
	@Qualifier("simpleVideoProcessorService")
	private VideoProcessorService simpleVideoProcessorService;
	
	@Autowired
	private VideoRepository videoRepository;
	
	@PostConstruct
	private void postConstruct(){
		executorService = Executors.newFixedThreadPool(threadPoolCount);
	}
	
	@PreDestroy
	private void preDestroy(){
		executorService.shutdown();
	}
	
	@Override
	public Video processVideo(VideoUploadForm uploadForm) {
		Video video = new Video(null, "/static/img/thumbnail-stub.jpg");
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
			videoRepository.save(video);
		}
		
	}
}
