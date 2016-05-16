package com.phobod.study.vcp.service.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.phobod.study.vcp.exception.CantProcessUserException;
import com.phobod.study.vcp.service.AvatarService;

@RunWith(MockitoJUnitRunner.class)
public class GravatarAvatarServiceTest {
	@InjectMocks
	private AvatarService avatarService = new GravatarAvatarService();
	
	private String firstTestEmailCorrect;
	private String secondTestEmailCorrect;
	private String firstTestEmailWithSpaces;
	private String firstTestEmailWithMixedCase;
	private String firstTestEmailWithSpacesAndMixedCase;
	private String firstRightUrl;
	private String secondRightUrl;
	
	@Before
	public void setUp() throws Exception {
		firstTestEmailCorrect = "first.test@email.com";
		secondTestEmailCorrect = "second.test@email.com";
		firstTestEmailWithSpaces = "  first.test@email.com  ";
		firstTestEmailWithMixedCase = "First.Test@Email.com";
		firstTestEmailWithSpacesAndMixedCase = "  First.Test@Email.com  ";
		firstRightUrl = "http://www.gravatar.com/avatar/b2d4bb4815184fb872c557190320286a?d=mm";
		secondRightUrl = "http://www.gravatar.com/avatar/52ab06502d8e588ec24a549b95dc61ea?d=mm";
	}

	@Test(expected = CantProcessUserException.class)
	public final void testGenerateAvatarUrlWithNull() {
		avatarService.generateAvatarUrl(null);
	}

	@Test
	public final void testGenerateAvatarUrl() {
		String firstUrl = avatarService.generateAvatarUrl(firstTestEmailCorrect);
		String secondUrl = avatarService.generateAvatarUrl(secondTestEmailCorrect);
		String firstUrlWithSpaces = avatarService.generateAvatarUrl(firstTestEmailWithSpaces);
		String firstUrlWithMixedCase = avatarService.generateAvatarUrl(firstTestEmailWithMixedCase);
		String firstUrlWithSpacesAndMixedCase = avatarService.generateAvatarUrl(firstTestEmailWithSpacesAndMixedCase);
		assertEquals(firstRightUrl, firstUrl);
		assertEquals(secondRightUrl, secondUrl);
		assertEquals(firstRightUrl, firstUrlWithSpaces);
		assertEquals(firstRightUrl, firstUrlWithMixedCase);
		assertEquals(firstRightUrl, firstUrlWithSpacesAndMixedCase);
	}

}
