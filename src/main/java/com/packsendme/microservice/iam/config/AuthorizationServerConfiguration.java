package com.packsendme.microservice.iam.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {
    

	@Autowired
	private TokenStore tokenStore;
    
    @Autowired
	private AuthenticationManager authenticationManager;
    
    @Value("${security.oauth2.client.client-id}")
    private String clientId;

    @Value("${security.oauth2.client.authorized-grant-types}")
    private String[] authorizedGrantTypes;

    @Value("${security.oauth2.client.resource-ids}")
    private String resourceIds;

    @Value("${security.oauth2.client.scope}")
    private String[] scopes;

   // @Value("${security.oauth2.client.client-secret}")
    private String secret = "$2a$10$jJFbSMGC2xsOqIlYYLqRouBDGRsvnP.egfdw3MutRXzHdhNoNnBA6";

    @Value("${security.oauth2.client.access-token-validity-seconds}")
    private Integer accessTokenValiditySeconds;
    
	static final int ACCESS_TOKEN_VALIDITY_SECONDS = 1*60*60;
    static final int FREFRESH_TOKEN_VALIDITY_SECONDS = 6*60*60;
	
	@Override
	public void configure(ClientDetailsServiceConfigurer configurer) throws Exception {
		configurer
	              .inMemory()
	              .withClient(clientId)
	              .secret(secret)
	              .authorizedGrantTypes(authorizedGrantTypes)
	              .scopes(scopes)
	              .resourceIds(resourceIds);
	}
	
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
    	endpoints.tokenStore(tokenStore).authenticationManager(authenticationManager);
    }
   

}
