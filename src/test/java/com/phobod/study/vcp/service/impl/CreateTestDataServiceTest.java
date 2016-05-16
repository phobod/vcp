package com.phobod.study.vcp.service.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.phobod.study.vcp.domain.Company;
import com.phobod.study.vcp.domain.User;
import com.phobod.study.vcp.domain.Video;
import com.phobod.study.vcp.repository.statistics.VideoStatisticsRepository;

@RunWith(MockitoJUnitRunner.class)
public class CreateTestDataServiceTest {
	@InjectMocks
	private CreateTestDataService createTestDataService = new CreateTestDataService();

	@Mock
	private MongoTemplate mongoTemplate;
	
	@Mock
	private VideoStatisticsRepository videoStatisticsRepository;
	
	@Mock
	private PasswordEncoder passwordEncoder;
	
	@Before
	public void setUp() throws Exception {
	}

	private void setUpPrivateField(String name, Object value) throws NoSuchFieldException, IllegalAccessException {
		Field fromEmailField = createTestDataService.getClass().getDeclaredField(name);
		fromEmailField.setAccessible(true);
		fromEmailField.set(createTestDataService,value);
	}
	
	@Test
	public final void testCreateTestDataIfNecessaryWithRecreateFalseAndFullDb() throws Exception{
		setUpPrivateField("mongoRecreateDb",false);
		when(mongoTemplate.count(null, Company.class)).thenReturn(100L);
		when(mongoTemplate.count(null, User.class)).thenReturn(100L);
		when(mongoTemplate.count(null, Video.class)).thenReturn(100L);
		createTestDataService.createTestDataIfNecessary();
		verifyZeroInteractions(videoStatisticsRepository);
		verifyZeroInteractions(passwordEncoder);
		verify(mongoTemplate,times(0)).remove(any());
		verify(mongoTemplate,times(0)).insert(any());
	}

	@Test
	public final void testCreateTestDataIfNecessaryWithRecreateTrue() throws Exception{
		setUpPrivateField("mongoRecreateDb",true);
		testRecreateData();
	}

	@Test
	public final void testCreateTestDataIfNecessaryWithRecreateFalseAndEmptyCompanyDb() throws Exception{
		setUpPrivateField("mongoRecreateDb",false);
		when(mongoTemplate.count(null, Company.class)).thenReturn(0L);
		when(mongoTemplate.count(null, User.class)).thenReturn(100L);
		when(mongoTemplate.count(null, Video.class)).thenReturn(100L);
		testRecreateData();
	}

	@Test
	public final void testCreateTestDataIfNecessaryWithRecreateFalseAndEmptyUserDb() throws Exception{
		setUpPrivateField("mongoRecreateDb",false);
		when(mongoTemplate.count(null, Company.class)).thenReturn(100L);
		when(mongoTemplate.count(null, User.class)).thenReturn(0L);
		when(mongoTemplate.count(null, Video.class)).thenReturn(100L);
		testRecreateData();
	}

	@Test
	public final void testCreateTestDataIfNecessaryWithRecreateFalseAndEmptyVideoDb() throws Exception{
		setUpPrivateField("mongoRecreateDb",false);
		when(mongoTemplate.count(null, Company.class)).thenReturn(100L);
		when(mongoTemplate.count(null, User.class)).thenReturn(100L);
		when(mongoTemplate.count(null, Video.class)).thenReturn(0L);
		testRecreateData();
	}

	private void testRecreateData() {
		createTestDataService.createTestDataIfNecessary();
		verify(videoStatisticsRepository).deleteAll();;
		verify(passwordEncoder,times(6)).encode(any(CharSequence.class));
		verify(mongoTemplate,times(4)).remove(any(Query.class),any(Class.class));
		verify(mongoTemplate).insert(anyCollectionOf(Company.class),eq(Company.class));
		verify(mongoTemplate).insert(anyCollectionOf(User.class),eq(User.class));
		verify(mongoTemplate).insert(anyCollectionOf(Video.class),eq(Video.class));
	}

}
