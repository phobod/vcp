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
import org.springframework.security.access.AccessDeniedException;

import com.phobod.study.vcp.component.TestUtils;
import com.phobod.study.vcp.domain.Video;
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

	private String videoId;
	private String userId;

	@Before
	public void setUp() throws Exception {
		videoId = "videoId";
		userId = "userId";
	}

	@Test
	public final void testListVideosByUser() {
		userService.listVideosByUser(TestUtils.getTestPageable(), userId);
		verify(videoRepository).findByOwnerIdOrderByViewsDesc(TestUtils.getTestPageable(), userId);
	}

	@Test
	public final void testListVideosByUserExcludeOne() {
		userService.listVideosByUserExcludeOne(TestUtils.getTestPageable(), videoId, userId);
		verify(videoRepository).findByIdNotAndOwnerIdOrderByViewsDesc(TestUtils.getTestPageable(), videoId, userId);
	}

	@Test
	public final void testUploadVideo() {
		when(videoProcessorService.processVideo(TestUtils.getVideoUploadForm())).thenReturn(TestUtils.getTestVideoWithoutId());
		userService.uploadVideo(TestUtils.getTestUserWithId(userId), TestUtils.getVideoUploadForm());
		verify(videoProcessorService).processVideo(TestUtils.getVideoUploadForm());
		verify(videoRepository).save(argThat(new SavedVideoArgumentMatcher()));
		verify(videoSearchRepository).save(argThat(new SavedVideoArgumentMatcher()));
	}

	@Test(expected = AccessDeniedException.class)
	public final void testUpdateVideoWithException() {
		when(videoRepository.findOne(videoId)).thenReturn(TestUtils.getTestVideoWithIdAndUser(videoId, TestUtils.getTestUserWithId(userId)));
		userService.updateVideo(videoId, TestUtils.getVideoUploadForm(), TestUtils.getTestOtherUserWithId(userId + "1"));
	}

	@Test
	public final void testUpdateVideoSuccess() {
		when(videoRepository.findOne(videoId)).thenReturn(TestUtils.getTestVideoWithIdAndUser(videoId, TestUtils.getTestUserWithId(userId)));
		userService.updateVideo(videoId, TestUtils.getVideoUploadForm(), TestUtils.getTestUserWithId(userId));
		verify(videoRepository).findOne(videoId);
		verify(videoRepository).save(argThat(new SavedVideoArgumentMatcher()));
		verify(videoSearchRepository).save(argThat(new SavedVideoArgumentMatcher()));
	}

	@Test(expected = AccessDeniedException.class)
	public final void testDeleteVideoWithException() {
		when(videoRepository.findOne(videoId)).thenReturn(TestUtils.getTestVideoWithIdAndUser(videoId, TestUtils.getTestUserWithId(userId)));
		userService.deleteVideo(videoId, TestUtils.getTestOtherUserWithId(userId + "1"));
	}

	@Test
	public final void testDeleteVideoSuccess() {
		when(videoRepository.findOne(videoId)).thenReturn(TestUtils.getTestVideoWithIdAndUser(videoId, TestUtils.getTestUserWithId(userId)));
		userService.deleteVideo(videoId, TestUtils.getTestUserWithId(userId));
		verify(videoRepository).delete(videoId);
		verify(videoSearchRepository).delete(videoId);
	}

	@Test
	public final void testDeleteAllVideosByUser() {
		userService.deleteAllVideosByUser(userId);
		verify(videoRepository).deleteByOwnerId(userId);
		verify(videoSearchRepository).deleteByOwnerId(userId);
	}

	private class SavedVideoArgumentMatcher extends ArgumentMatcher<Video> {
		@Override
		public boolean matches(Object argument) {
			if (argument instanceof Video) {
				Video savedVideo = (Video) argument;
				if (TestUtils.getVideoUploadForm().getTitle().equals(savedVideo.getTitle())
						&& TestUtils.getVideoUploadForm().getDescription().equals(savedVideo.getDescription())) {
					return true;
				}
			}
			return false;
		}
	}

}
