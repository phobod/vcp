package com.phobod.study.vcp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

import com.phobod.study.vcp.Constants.Role;
import com.phobod.study.vcp.security.CsrfHeaderFilter;
import com.phobod.study.vcp.security.RestAccessDeniedHandler;
import com.phobod.study.vcp.security.RestAuthenticationFailureHandler;
import com.phobod.study.vcp.security.RestAuthenticationSuccessHandler;
import com.phobod.study.vcp.service.impl.AuthentificationService;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private AuthentificationService authentificationService;
	
	@Autowired
    PersistentTokenRepository repository;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
			.antMatchers("/video/popular", "/video/all", "/login", "/user").permitAll()
			.antMatchers("/admin/**").hasAuthority(Role.ADMIN.name())
			.antMatchers("/my-account/**").hasAuthority(Role.USER.name())
			.antMatchers("/user/**", "/video/**").hasAnyAuthority(Role.USER.name(),Role.ADMIN.name());
		http.formLogin()
			.successHandler(new RestAuthenticationSuccessHandler())
			.failureHandler(new RestAuthenticationFailureHandler())
			.loginProcessingUrl("/login")
			.usernameParameter("username")
			.passwordParameter("password");
		http.logout()
			.logoutUrl("/logout")
			.logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())
			.invalidateHttpSession(true)
			.deleteCookies("JSESSIONID");
		http.exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
		http.exceptionHandling().accessDeniedHandler(new RestAccessDeniedHandler());
		http.rememberMe()
			.rememberMeParameter("remember-me")
			.tokenRepository(repository);
		http.csrf().csrfTokenRepository(csrfTokenRepository());
		http.addFilterAfter(new CsrfHeaderFilter(), CsrfFilter.class);
	}
	
	private CsrfTokenRepository csrfTokenRepository() {
		  HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
		  repository.setHeaderName("X-XSRF-TOKEN");
		  return repository;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(authentificationService).passwordEncoder(passwordEncoder());
	}
	

}
