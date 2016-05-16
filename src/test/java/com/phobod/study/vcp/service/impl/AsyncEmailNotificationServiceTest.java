package com.phobod.study.vcp.service.impl;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

import com.phobod.study.vcp.component.TestUtils;
import com.phobod.study.vcp.domain.User;
import com.phobod.study.vcp.service.NotificationService;

@RunWith(MockitoJUnitRunner.class)
public class AsyncEmailNotificationServiceTest {
	@InjectMocks
	private NotificationService notificationService = new AsyncEmailNotificationService();

	@Mock
	private JavaMailSender javaMailSender;
	
	private List<User> usersWithBlankEmail; 
	private String userId;
	private String restoreLink;
	private int tryCount;
	private String fromName;
	private String fromEmail;
	private MimeMessage mimeMessage;
	
	@Before
	public void setUp() throws Exception {
		userId = "userId";
		usersWithBlankEmail = Arrays.asList(new User(),new User(null,null,null,null,"",null,null,null),new User(null,null,null,null,"   ",null,null,null));
		restoreLink = "test link";
		mimeMessage = new MimeMessage(Session.getDefaultInstance(System.getProperties(), null));
		setUpNotificationServiceFields();
	}

	private void setUpNotificationServiceFields() throws NoSuchFieldException, IllegalAccessException {
		tryCount = 3;
		setUpPrivateField("tryCount",tryCount);
		fromName = "TestSenderName";
		setUpPrivateField("fromName",fromName);
		fromEmail = "test@email.com";
		setUpPrivateField("fromEmail",fromEmail);
	}

	private void setUpPrivateField(String name, Object value) throws NoSuchFieldException, IllegalAccessException {
		Field fromEmailField = notificationService.getClass().getDeclaredField(name);
		fromEmailField.setAccessible(true);
		fromEmailField.set(notificationService,value);
	}

	@Test(timeout = 200)
	public final void testSendRestoreAccessLinkWithFailure() throws Exception {
		doThrow(new MailSendException("")).when(javaMailSender).send((MimeMessage) anyObject());
		when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
		notificationService.sendRestoreAccessLink(TestUtils.getTestUserWithId(userId), restoreLink);
		Thread.sleep(100);
		verify(javaMailSender,times(tryCount)).createMimeMessage();
		verify(javaMailSender,times(tryCount)).send((MimeMessage) anyObject());
	}

	@Test(timeout = 200)
	public final void testSendRestoreAccessLinkWithBlankEmail() throws Exception {
		when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
		for (User user : usersWithBlankEmail) {
			notificationService.sendRestoreAccessLink(user, restoreLink);
			Thread.sleep(50);
		}
		verifyZeroInteractions(javaMailSender);
	}

	@Test(timeout = 100)
	public final void testSendRestoreAccessLinkSuccess() throws Exception {
		when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
		notificationService.sendRestoreAccessLink(TestUtils.getTestUserWithId(userId), restoreLink);
		Thread.sleep(50);
		verify(javaMailSender,times(1)).createMimeMessage();
		verify(javaMailSender,times(1)).send(argThat(new MimeMessageArgumentMatcher(TestUtils.getTestUserWithId(userId))));
	}

	private class MimeMessageArgumentMatcher extends ArgumentMatcher<MimeMessage>{
		private User user;

		public MimeMessageArgumentMatcher(User user) {
			super();
			this.user = user;
		}

		@Override
		public boolean matches(Object argument) {
			if (argument instanceof MimeMessage) {
				MimeMessage message = (MimeMessage)argument;
				try {
					if ("Restore access".equals(message.getSubject()) && message.getContent().toString().endsWith(restoreLink) && message.getRecipients(Message.RecipientType.TO).length == 1 && message.getFrom().length == 1) {
						Address recipientAddress = message.getRecipients(Message.RecipientType.TO)[0];
						Address senderAddress = message.getFrom()[0];
						if (recipientAddress instanceof InternetAddress && senderAddress instanceof InternetAddress) {
							InternetAddress recipientInternetAddress = (InternetAddress)recipientAddress;
							InternetAddress senderInternetAddress = (InternetAddress)senderAddress;
 							if (user.getEmail().equals(recipientInternetAddress.getAddress()) && user.getName().equals(recipientInternetAddress.getPersonal()) && fromEmail.equals(senderInternetAddress.getAddress()) && fromName.equals(senderInternetAddress.getPersonal())) {
 								return true;
							}
						}
					}
				} catch (MessagingException | IOException e) {
					return false;
				}
			}
			return false;
		}
	}

}
