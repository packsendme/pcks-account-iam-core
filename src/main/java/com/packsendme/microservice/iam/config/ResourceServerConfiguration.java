package com.packsendme.microservice.iam.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;

@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter{

	
	@Value("${security.oauth2.client.resource-ids}")
    private String RESOURCE_ID;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
		System.out.println(" B configure ResourceServerSecurityConfigurer  ");
        resources.resourceId(RESOURCE_ID).stateless(false);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
		System.out.println("B configure HttpSecurity  ");
		http.
        anonymous().disable()
        .authorizeRequests()
        .antMatchers("/users/**").authenticated()
        .and().exceptionHandling().accessDeniedHandler(new OAuth2AccessDeniedHandler());
    }
}
