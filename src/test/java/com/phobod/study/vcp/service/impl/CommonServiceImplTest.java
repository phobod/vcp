package com.phobod.study.vcp.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.phobod.study.vcp.component.TestUtils;
import com.phobod.study.vcp.domain.Video;
import com.phobod.study.vcp.exception.CantProcessAccessRecoveryException;
import com.phobod.study.vcp.repository.search.VideoSearchRepository;
import com.phobod.study.vcp.repository.storage.UserRepository;
import com.phobod.study.vcp.repository.storage.VideoRepository;
import com.phobod.study.vcp.service.CommonService;
import com.phobod.study.vcp.service.NotificationService;
import com.phobod.study.vcp.service.VideoStatisticsService;

@RunWith(MockitoJUnitRunner.class)
public class CommonServiceImplTest {
	@InjectMocks
	private CommonService commonService = new CommonServiceImpl();

	@Mock
	private VideoRepository videoRepository;

	@Mock
	private VideoSearchRepository videoSearchRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private NotificationService notificationService;

	@Mock
	private VideoStatisticsService videoStatisticsService;

	@Mock
	private PasswordEncoder passwordEncoder;

	private String userId;
	private String login;
	private String videoId;
	private String vcpUrl;
	private String testHash;
	private String correctPassword;

	@Before
	public void setUp() throws Exception {
		videoId = "videoId";
		userId = "userId";
		login = "testLogin";
		vcpUrl = "testHostUrl";
		testHash = "testHash";
		correctPassword = "123ASfsd";
		setUpPrivateField("vcpUrl", vcpUrl);
	}

	private void setUpPrivateField(String name, Object value) throws NoSuchFieldException, IllegalAccessException {
		Field fromEmailField = commonService.getClass().getDeclaredField(name);
		fromEmailField.setAccessible(true);
		fromEmailField.set(commonService, value);
	}

	@Test
	public final void testListPopularVideos() {
		commonService.listPopularVideos();
		verify(videoRepository).findTop3ByOrderByViewsDesc();
	}

	@Test
	public final void testListAllVideos() {
		commonService.listAllVideos(TestUtils.getTestPageable());
		verify(videoRepository).findAll(TestUtils.getTestPageable());
	}

	@Test
	public final void testFindVideoById() {
		int videoViews = TestUtils.getTestVideoWithId(videoId).getViews();
		when(videoRepository.findOne(videoId)).thenReturn(TestUtils.getTestVideoWithId(videoId));
		Video resultVideo = commonService.findVideoById(videoId, TestUtils.getTestUserWithoutId(), "192.168.0.1");
		verify(videoRepository).findOne(videoId);
		verify(videoStatisticsService).saveVideoViewStatistics(TestUtils.getTestVideoWithId(videoId), TestUtils.getTestUserWithoutId(), "192.168.0.1");
		verify(videoRepository).save(TestUtils.getTestVideoWithId(videoId));
		verify(videoSearchRepository).save(TestUtils.getTestVideoWithId(videoId));
		assertEquals((videoViews + 1), resultVideo.getViews());
	}

	@Test
	public final void testListVideosBySearchQuery() {
		commonService.listVideosBySearchQuery(anyString(), TestUtils.getTestPageable());
		verify(videoSearchRepository).search(any(SearchQuery.class));
	}

	@Test(expected = CantProcessAccessRecoveryException.class)
	public final void testSendRestoreEmailWithException() {
		when(userRepository.findByLogin(login)).thenThrow(new RuntimeException());
		commonService.sendRestoreEmail(login);
	}

	@Test
	public final void testSendRestoreEmailSuccess() {
		when(userRepository.findByLogin(login)).thenReturn(TestUtils.getTestUserWithId(userId));
		commonService.sendRestoreEmail(login);
		verify(userRepository).findByLogin(login);
		verify(userRepository).save(TestUtils.getTestUserWithId(userId));
		assertNotNull(TestUtils.getTestUserWithId(userId).getHash());
		String restoreLink = vcpUrl + "/recovery/acsess/" + userId + "/" + TestUtils.getTestUserWithId(userId).getHash();
		verify(notificationService).sendRestoreAccessLink(TestUtils.getTestUserWithId(userId), restoreLink);
	}

	@Test(expected = CantProcessAccessRecoveryException.class)
	public final void testCheckRestorePasswordLinkWithIncorrectHash() {
		when(userRepository.findOne(userId)).thenReturn(TestUtils.getTestUserWithId(userId));
		commonService.checkRestorePasswordLink(userId, testHash + "01");
		;
	}

	@Test(expected = CantProcessAccessRecoveryException.class)
	public final void testCheckRestorePasswordLinkWithNullHash() {
		when(userRepository.findOne(userId)).thenReturn(TestUtils.getTestUserWithId(userId));
		commonService.checkRestorePasswordLink(userId, null);
		;
	}

	@Test(expected = CantProcessAccessRecoveryException.class)
	public final void testCheckRestorePasswordLinkWithUserWithoutHash() {
		TestUtils.getTestUserWithId(userId).setHash(null);
		when(userRepository.findOne(userId)).thenReturn(TestUtils.getTestUserWithId(userId));
		commonService.checkRestorePasswordLink(userId, testHash);
		;
	}

	@Test
	public final void testCheckRestorePasswordLinkSuccess() {
		TestUtils.getTestUserWithId(userId).setHash(testHash);
		when(userRepository.findOne(userId)).thenReturn(TestUtils.getTestUserWithId(userId));
		commonService.checkRestorePasswordLink(userId, testHash);
		verify(userRepository).findOne(userId);
		verify(userRepository).save(TestUtils.getTestUserWithId(userId));		
	}

	@Test(expected = CantProcessAccessRecoveryException.class)
	public final void testRestorePasswordWithIncorrectPassword() {
		commonService.restorePassword(TestUtils.getTestUserWithId(userId), "1111");
	}

	@Test
	public final void testRestorePasswordSuccess() {
		commonService.restorePassword(TestUtils.getTestUserWithId(userId), correctPassword);
		verify(passwordEncoder).encode(correctPassword);
		verify(userRepository).save(TestUtils.getTestUserWithId(userId));
	}

}
