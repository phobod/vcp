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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.phobod.study.vcp.domain.User;
import com.phobod.study.vcp.domain.Video;
import com.phobod.study.vcp.exception.CantProcessAccessRecoveryException;
import com.phobod.study.vcp.form.RecoveryForm;
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
	
	private Pageable pageable;
	private Video sourseVideo;
	private User testUser;
	private String login;
	private String ip;
	private int videoViews;
	private String vcpUrl;
	private String testHash;
	private User testUserForRestorePassword;
	private String testPassword;

	@Before
	public void setUp() throws Exception {
		pageable = new PageRequest(0, 10);
		videoViews = 100;
		sourseVideo = new Video();
		sourseVideo.setId("videoId");
		sourseVideo.setViews(videoViews);
		login = "testLogin";
		testUser = new User();
		testUser.setId("id");
		testUser.setLogin(login);
		ip = "192.168.0.1";
		vcpUrl = "testHostUrl";
		testHash = "testHash";
		testUserForRestorePassword = new User();
		testUserForRestorePassword.setId("id");
		testUserForRestorePassword.setHash(testHash);
		testPassword = "testPassword";
		setUpPrivateField("vcpUrl",vcpUrl);
	}
	
	private void setUpPrivateField(String name, Object value) throws NoSuchFieldException, IllegalAccessException {
		Field fromEmailField = commonService.getClass().getDeclaredField(name);
		fromEmailField.setAccessible(true);
		fromEmailField.set(commonService,value);
	}

	@Test
	public final void testListPopularVideos() {
		commonService.listPopularVideos();
		verify(videoRepository).findTop3ByOrderByViewsDesc();
	}

	@Test
	public final void testListAllVideos() {
		commonService.listAllVideos(pageable);
		verify(videoRepository).findAll(pageable);
	}

	@Test
	public final void testFindVideoById() {
		when(videoRepository.findOne(sourseVideo.getId())).thenReturn(sourseVideo);
		Video resultVideo = commonService.findVideoById(sourseVideo.getId(), testUser, ip);
		verify(videoRepository).findOne(sourseVideo.getId());
		verify(videoStatisticsService).saveVideoViewStatistics(sourseVideo, testUser, ip);
		verify(videoRepository).save(sourseVideo);
		verify(videoSearchRepository).save(sourseVideo);
		assertEquals((videoViews + 1), resultVideo.getViews());
	}

	@Test
	public final void testListVideosBySearchQuery() {
		commonService.listVideosBySearchQuery(anyString(), pageable);
		verify(videoSearchRepository).search(any(SearchQuery.class));
	}

	@Test(expected = CantProcessAccessRecoveryException.class)
	public final void testSendRestoreEmailWithException() {
		when(userRepository.findByLogin(login)).thenThrow(new RuntimeException());
		commonService.sendRestoreEmail(login);
	}

	@Test
	public final void testSendRestoreEmailSuccess() {
		when(userRepository.findByLogin(login)).thenReturn(testUser);
		commonService.sendRestoreEmail(login);
		verify(userRepository).findByLogin(login);
		verify(userRepository).save(testUser);
		assertNotNull(testUser.getHash());
		String restoreLink = vcpUrl + "/#/recovery/acsess/" + testUser.getId() + "/" + testUser.getHash();
		verify(notificationService).sendRestoreAccessLink(testUser, restoreLink);
	}

	@Test(expected = CantProcessAccessRecoveryException.class)
	public final void testRestorePasswordWithIncorrectHash() {
		when(userRepository.findOne(testUserForRestorePassword.getId())).thenReturn(testUserForRestorePassword);
		commonService.restorePassword(new RecoveryForm(testUserForRestorePassword.getId(),testHash + "01",testPassword));;
	}

	@Test(expected = CantProcessAccessRecoveryException.class)
	public final void testRestorePasswordWithNullHash() {
		when(userRepository.findOne(testUserForRestorePassword.getId())).thenReturn(testUserForRestorePassword);
		commonService.restorePassword(new RecoveryForm(testUserForRestorePassword.getId(),null,testPassword));;
	}

	@Test(expected = CantProcessAccessRecoveryException.class)
	public final void testRestorePasswordWithUserWithoutHash() {
		testUserForRestorePassword.setHash(null);
		when(userRepository.findOne(testUserForRestorePassword.getId())).thenReturn(testUserForRestorePassword);
		commonService.restorePassword(new RecoveryForm(testUserForRestorePassword.getId(),testHash,testPassword));;
	}

	@Test
	public final void testRestorePasswordSuccess() {
		testUserForRestorePassword.setHash(testHash);
		when(userRepository.findOne(testUserForRestorePassword.getId())).thenReturn(testUserForRestorePassword);
		commonService.restorePassword(new RecoveryForm(testUserForRestorePassword.getId(),testHash,testPassword));
		verify(userRepository).findOne(testUserForRestorePassword.getId());
		verify(passwordEncoder).encode(testPassword);
		verify(userRepository).save(testUserForRestorePassword);
	}

}
