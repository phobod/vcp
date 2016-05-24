package com.phobod.study.vcp.service.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

import com.phobod.study.vcp.domain.Video;
import com.phobod.study.vcp.repository.search.VideoSearchRepository;
import com.phobod.study.vcp.repository.storage.VideoRepository;

@RunWith(MockitoJUnitRunner.class)
public class ElasticSearchIndexingServiceTest {
	@InjectMocks
	private ElasticSearchIndexingService elasticSearchIndexingService = new ElasticSearchIndexingService();

	@Mock
	private ElasticsearchOperations elasticsearchOperations;

	@Mock
	private VideoRepository videoRepository;

	@Mock
	private VideoSearchRepository videoSearchRepository;

	private List<Video> testVideos;

	@Before
	public void setUp() throws Exception {
		testVideos = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			testVideos.add(new Video());
		}
	}

	private void setUpPrivateField(String name, Object value) throws NoSuchFieldException, IllegalAccessException {
		Field fromEmailField = elasticSearchIndexingService.getClass().getDeclaredField(name);
		fromEmailField.setAccessible(true);
		fromEmailField.set(elasticSearchIndexingService, value);
	}

	@Test
	public final void testPostConstructWithIndexAllDuringStartupTrue() throws Exception {
		setUpPrivateField("indexAllDuringStartup", true);
		when(videoRepository.findAll()).thenReturn(testVideos);
		elasticSearchIndexingService.postConstruct();
		verify(elasticsearchOperations).deleteIndex(Video.class);
		verify(videoRepository).findAll();
		verify(videoSearchRepository, times(testVideos.size())).save(any(Video.class));
	}

	@Test
	public final void testPostConstructWithIndexAllDuringStartupFalse() throws Exception {
		setUpPrivateField("indexAllDuringStartup", false);
		elasticSearchIndexingService.postConstruct();
		verifyZeroInteractions(elasticsearchOperations);
		verifyZeroInteractions(videoRepository);
		verifyZeroInteractions(videoSearchRepository);
	}

}
