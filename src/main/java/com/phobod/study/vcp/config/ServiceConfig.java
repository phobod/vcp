package com.phobod.study.vcp.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Configuration
@ComponentScan({"com.phobod.study.vcp.service.impl", "com.phobod.study.vcp.component"})
@EnableAspectJAutoProxy
public class ServiceConfig {
	@Bean
	public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() throws IOException{
		PropertySourcesPlaceholderConfigurer conf = new PropertySourcesPlaceholderConfigurer();
		conf.setLocations(new Resource[] {new ClassPathResource("application.properties")});
		return conf;
	} 
}
