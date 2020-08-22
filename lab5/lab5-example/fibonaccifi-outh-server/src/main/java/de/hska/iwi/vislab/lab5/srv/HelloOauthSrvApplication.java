
package de.hska.iwi.vislab.lab5.srv;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableResourceServer
@RestController
public class HelloOauthSrvApplication {

	public static void main(String[] args) {
		SpringApplication.run(HelloOauthSrvApplication.class, args);
	}

	private int fibonacciIndexNumber = 0;
	private int fibonacciNumber = 0;

	@RequestMapping(value = "/increment", method = RequestMethod.GET)
	public String increment() {
		this.fibonacciIndexNumber++;
		this.fibonacciNumber = this.getFibonacciForNumber(this.fibonacciIndexNumber);
		return Integer.toString(this.fibonacciNumber);
	}

	@RequestMapping(value = "/reset", method = RequestMethod.DELETE)
	public String reset() {
		this.fibonacciNumber = 0;
		this.fibonacciIndexNumber = 0;
		return "success";
	}

	private int getFibonacciForNumber(int number) {
        if (number == 0) {
            return 0;
        } else if (number == 1) {
            return 1;
        } else {
            return  getFibonacciForNumber(number - 1) + getFibonacciForNumber(number - 2);
        }
	}
	@Configuration
	@EnableAuthorizationServer
	protected static class OAuth2Config extends AuthorizationServerConfigurerAdapter {

		@Autowired
		private AuthenticationManager authenticationManager;

		@Override
		public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
			endpoints.authenticationManager(authenticationManager);
		}

		@Override
		public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
			security.checkTokenAccess("isAuthenticated()");
		}

		@Override
		public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
			// @formatter:off
			clients.inMemory()
				.withClient("my-client-with-secret")
					.authorizedGrantTypes("client_credentials")
					.authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
					.scopes("read", "write", "trust")
					.resourceIds("oauth2-resource")
					.secret("secret");
			// @formatter:on
		}

	}
}