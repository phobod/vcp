package com.phobod.study.vcp.config;

import java.io.IOException;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@ComponentScan({ "com.phobod.study.vcp.service.impl", "com.phobod.study.vcp.component" })
@EnableAspectJAutoProxy
public class ServiceConfig {

	@Value("${email.smtp.server}")
	private String emailSmtpServer;

	@Value("${email.smtp.port}")
	private int emailSmtpPort;

	@Value("${email.smtp.username}")
	private String emailSmtpUsername;

	@Value("${email.smtp.password}")
	private String emailSmtpPassword;

	@Bean
	public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() throws IOException {
		PropertySourcesPlaceholderConfigurer conf = new PropertySourcesPlaceholderConfigurer();
		conf.setLocations(new Resource[] { new ClassPathResource("application.properties") });
		return conf;
	}

	@Bean
	public JavaMailSender javaMailSender() {
		JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
		javaMailSender.setHost(emailSmtpServer);
		javaMailSender.setUsername(emailSmtpUsername);
		javaMailSender.setPassword(emailSmtpPassword);
		javaMailSender.setPort(emailSmtpPort);
		javaMailSender.setDefaultEncoding("UTF-8");
		javaMailSender.setJavaMailProperties(javaMailProperties());
		return javaMailSender;
	}

	private Properties javaMailProperties() {
		Properties p = new Properties();
		p.setProperty("mail.smtp.auth", "true");
		p.setProperty("mail.smtp.starttls.enable", "true");
		return p;
	}
}
