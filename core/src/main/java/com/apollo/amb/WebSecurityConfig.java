package com.apollo.amb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

/*
@Configuration
@EnableWebSecurity
*/
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
        	.csrf().disable()
            .authorizeRequests()
            	.antMatchers("/", "/login", "/logout").permitAll()
                .anyRequest().authenticated()
                .and()
            .formLogin()
            	.permitAll()
            	.loginPage(AppContext.getLoginPageUrl())
            	.loginProcessingUrl("/login")
            	.successHandler(successHandler())
            	.and()
            .logout()
            	.logoutUrl("/logout")
            	.logoutSuccessUrl(AppContext.getLoginPageUrl())
				.permitAll();
    }
    
    @Bean
    public AuthenticationSuccessHandler successHandler() {
        AmbSuccessHandler handler = new AmbSuccessHandler();
//        handler.setUseReferer(true);
        return handler;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .inMemoryAuthentication()
                .withUser("admin").password("secret").roles("ADMIN");
        auth
        	.inMemoryAuthentication()
                .withUser("user").password("secret").roles("USER");
    }
        
}
