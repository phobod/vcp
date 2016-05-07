package com.phobod.study.vcp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;

import com.phobod.study.vcp.Constants.Role;
import com.phobod.study.vcp.security.CsrfTokenGeneratorFilter;
import com.phobod.study.vcp.security.RestAccessDeniedHandler;
import com.phobod.study.vcp.security.RestAuthenticationFailureHandler;
import com.phobod.study.vcp.security.RestAuthenticationSuccessHandler;
import com.phobod.study.vcp.service.impl.AuthentificationService;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private AuthentificationService authentificationService;
	
	@Autowired
    PersistentTokenRepository tokenRepository;

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring()
			.antMatchers("/index.html", "/static/**", "/favicon.ico", "/media/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
			.antMatchers("/index.html", "/video/popular", "/video/all", "/login").permitAll()
			.antMatchers("/admin/**").hasAuthority(Role.ADMIN.name())
			.antMatchers("/my-account/**").hasAuthority(Role.USER.name())
			.anyRequest().authenticated();
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
			.rememberMeParameter("rememberMe")
			.tokenRepository(tokenRepository);
		http.addFilterAfter(new CsrfTokenGeneratorFilter(), CsrfFilter.class);
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
