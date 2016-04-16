package com.phobod.study.vcp.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.phobod.study.vcp.domain.Company;
import com.phobod.study.vcp.domain.Role;
import com.phobod.study.vcp.domain.User;
import com.phobod.study.vcp.domain.Video;
import com.phobod.study.vcp.service.CommonService;

@Service
public class CommonServiceImpl implements CommonService {

	@Override
	public Page<Video> listAllVideos(Pageable pageable) {
		List<Video> videos = new ArrayList<>();
		Company company = new Company("Coca-Cola", "Atlanta, USA", "info@coca-cola.com", "+1-800-438-2653");
		User user = new User("Jack", "Douu", "jactin", "qwerty123", "jactin@mail.ru", company, Role.USER,
				"http://www.radfaces.com/images/avatars/krumm.jpg");
		Video video = new Video("Piggy Tales - Pigs at Work - Mind The Gap",
				"Taking bridge building to another dimension. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
				"/media/thumbnail/63081380-7b45-4c4b-b3ba-4803e291cfaf.jpg",
				"/media/video/0c80ff83-58ef-4462-89ec-eb848e3bb88f.mp4", 2934, user);
		video.setId("11111");
		videos.add(video);
		video = new Video("Piggy Tales - Pigs at Work - Lights Out",
				"How many pigs does it take to screw in a lightbulb? We may never find out. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
				"/media/thumbnail/7c46e252-9693-4122-b53b-2cdd40549a8b.jpg",
				"/media/video/0d210cfc-9514-4794-9028-0b06cd3da545.mp4", 243, user);
		video.setId("22222");
		videos.add(video);
		video = new Video("Piggy Tales - It's a wrap",
				"You know that feeling when a super fun party is over and it's time to clean up everything and go home? Well, I guess even piggies can get a bit emotional at times like that.",
				"/media/thumbnail/f3114bb4-7377-4eb3-b3b8-b3d4d4a545a5.jpg",
				"/media/video/2b759b2c-32c6-4939-bd63-66b76d82f5a0.mp4", 4007, user);
		video.setId("33333");
		videos.add(video);
		return new PageImpl<>(videos, pageable, 8);
	}


	@Override
	public Video videoById(String id) {
		System.out.println(id);
		Company company = new Company("Coca-Cola", "Atlanta, USA", "info@coca-cola.com", "+1-800-438-2653");
		User user = new User("Jack", "Douu", "jactin", "qwerty123", "jactin@mail.ru", company, Role.USER,
				"http://www.radfaces.com/images/avatars/krumm.jpg");
		Video video;
		if ("11111".equals(id)) {
			video = new Video("Piggy Tales - Pigs at Work - Mind The Gap",
					"Taking bridge building to another dimension. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
					"/media/thumbnail/63081380-7b45-4c4b-b3ba-4803e291cfaf.jpg",
					"/media/video/0c80ff83-58ef-4462-89ec-eb848e3bb88f.mp4", 2934, user);
			video.setId("11111");
		} else if ("22222".equals(id)) {
			video = new Video("Piggy Tales - Pigs at Work - Lights Out",
					"How many pigs does it take to screw in a lightbulb? We may never find out. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
					"/media/thumbnail/7c46e252-9693-4122-b53b-2cdd40549a8b.jpg",
					"/media/video/0d210cfc-9514-4794-9028-0b06cd3da545.mp4", 243, user);
			video.setId("22222");
		} else {
			video = new Video("Piggy Tales - It's a wrap",
					"You know that feeling when a super fun party is over and it's time to clean up everything and go home? Well, I guess even piggies can get a bit emotional at times like that.",
					"/media/thumbnail/f3114bb4-7377-4eb3-b3b8-b3d4d4a545a5.jpg",
					"/media/video/2b759b2c-32c6-4939-bd63-66b76d82f5a0.mp4", 4007, user);
			video.setId("33333");
		}
		return video;
	}



}
