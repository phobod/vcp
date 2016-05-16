package com.phobod.study.vcp.service.impl;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import com.phobod.study.vcp.Constants.Role;
import com.phobod.study.vcp.domain.Company;
import com.phobod.study.vcp.domain.User;
import com.phobod.study.vcp.domain.Video;
import com.phobod.study.vcp.form.VideoUploadForm;
import com.phobod.study.vcp.repository.search.VideoSearchRepository;
import com.phobod.study.vcp.repository.storage.VideoRepository;
import com.phobod.study.vcp.service.UserService;
import com.phobod.study.vcp.service.VideoProcessorService;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {
	@InjectMocks
	private UserService userService = new UserServiceImpl();
	
	@Mock
	private VideoRepository videoRepository;

	@Mock
	private VideoSearchRepository videoSearchRepository;
	
	@Mock
	private VideoProcessorService videoProcessorService;
	
	private Pageable pageable;
	private User user;
	private User otherUser;
	private String videoId;
	private Video video;
	private VideoUploadForm form;

	@Before
	public void setUp() throws Exception {
		pageable = new PageRequest(0, 10);
		user = new User("TestUserName", "TestUserSurname", "TestUserLogin", "1111", "test.user@email.com", new Company(), Role.USER, "http://www.gravatar.com/avatar/00000000000000000000000000000000?d=mm");
		user.setId("id");
		otherUser = new User();
		otherUser.setId("otherId");
		videoId = "videoId";
		video = new Video();
		video.setId(videoId);
		video.setOwner(user);
		form = new VideoUploadForm("title","description",null);
	}

	@Test
	public final void testListVideosByUser() {
		userService.listVideosByUser(pageable, user.getId());
		verify(videoRepository).findByOwnerIdOrderByViewsDesc(pageable, user.getId());
	}

	@Test
	public final void testListVideosByUserExcludeOne() {
		userService.listVideosByUserExcludeOne(pageable, videoId, user.getId());
		verify(videoRepository).findByIdNotAndOwnerIdOrderByViewsDesc(pageable, videoId, user.getId());
	}

	@Test
	public final void testUploadVideo() {
		when(videoProcessorService.processVideo(form)).thenReturn(video);
		userService.uploadVideo(user, form);
		verify(videoProcessorService).processVideo(form);
		verify(videoRepository).save(argThat(new SavedVideoArgumentMatcher()));
		verify(videoSearchRepository).save(argThat(new SavedVideoArgumentMatcher()));
	}

	@Test(expected = AccessDeniedException.class)
	public final void testUpdateVideoWithException() {
		when(videoRepository.findOne(videoId)).thenReturn(video);
		userService.updateVideo(videoId, form, otherUser);
	}

	@Test
	public final void testUpdateVideoSuccess() {
		when(videoRepository.findOne(videoId)).thenReturn(video);
		userService.updateVideo(videoId, form, user);
		verify(videoRepository).findOne(videoId);
		verify(videoRepository).save(argThat(new SavedVideoArgumentMatcher()));
		verify(videoSearchRepository).save(argThat(new SavedVideoArgumentMatcher()));
	}

	@Test(expected = AccessDeniedException.class)
	public final void testDeleteVideoWithException() {
		when(videoRepository.findOne(videoId)).thenReturn(video);
		userService.deleteVideo(videoId, otherUser);
	}

	@Test
	public final void testDeleteVideoSuccess() {
		when(videoRepository.findOne(videoId)).thenReturn(video);
		userService.deleteVideo(videoId, user);
		verify(videoRepository).delete(videoId);
		verify(videoSearchRepository).delete(videoId);
	}

	@Test
	public final void testDeleteAllVideosByUser() {
		userService.deleteAllVideosByUser(user.getId());
		verify(videoRepository).deleteByOwnerId(user.getId());
		verify(videoSearchRepository).deleteByOwnerId(user.getId());
	}
	
	private class SavedVideoArgumentMatcher extends ArgumentMatcher<Video>{
		@Override
		public boolean matches(Object argument) {
			if (argument instanceof Video) {
				Video savedVideo = (Video)argument;
				if (form.getTitle().equals(savedVideo.getTitle()) && form.getDescription().equals(savedVideo.getDescription())) {
					return true;
				}
			}
			return false;
		}
	}

}
