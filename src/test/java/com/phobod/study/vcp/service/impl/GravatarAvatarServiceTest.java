package com.phobod.study.vcp.service.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.phobod.study.vcp.exception.CantProcessUserException;
import com.phobod.study.vcp.service.AvatarService;

@RunWith(MockitoJUnitRunner.class)
public class GravatarAvatarServiceTest {
	private AvatarService avatarService = new GravatarAvatarService();

	@Test(expected = CantProcessUserException.class)
	public final void testGenerateAvatarUrlWithNull() {
		avatarService.generateAvatarUrl(null);
	}

	@Test
	public final void testGenerateAvatarUrl() {
		String firstUrl = avatarService.generateAvatarUrl("first.test@email.com");
		String firstUrlWithSpaces = avatarService.generateAvatarUrl("  first.test@email.com  ");
		String firstUrlWithMixedCase = avatarService.generateAvatarUrl("First.Test@Email.com");
		String firstUrlWithSpacesAndMixedCase = avatarService.generateAvatarUrl("  First.Test@Email.com  ");
		String secondUrl = avatarService.generateAvatarUrl("second.test@email.com");
		assertEquals("http://www.gravatar.com/avatar/b2d4bb4815184fb872c557190320286a?d=mm", firstUrl);
		assertEquals("http://www.gravatar.com/avatar/b2d4bb4815184fb872c557190320286a?d=mm", firstUrlWithSpaces);
		assertEquals("http://www.gravatar.com/avatar/b2d4bb4815184fb872c557190320286a?d=mm", firstUrlWithMixedCase);
		assertEquals("http://www.gravatar.com/avatar/b2d4bb4815184fb872c557190320286a?d=mm", firstUrlWithSpacesAndMixedCase);
		assertEquals("http://www.gravatar.com/avatar/52ab06502d8e588ec24a549b95dc61ea?d=mm", secondUrl);
	}

}
