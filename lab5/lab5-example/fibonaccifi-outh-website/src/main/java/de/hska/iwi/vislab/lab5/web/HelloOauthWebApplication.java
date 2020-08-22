package de.hska.iwi.vislab.lab5.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@SpringBootApplication
@EnableOAuth2Client
@Controller
public class HelloOauthWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(HelloOauthWebApplication.class, args);
	}

	@Value("${oauth.resource:http://localhost:8080}")
	private String baseUrl;

	@Value("${oauth.token:http://localhost:8080/oauth/token}")
	private String tokenUrl;

	@Autowired
	private OAuth2RestOperations restTemplate;

	@RequestMapping("/")
	public String home() {
		return "fibonacci";
	}

	@RequestMapping("/increment")
	public String increment(Model model) {
		String number = restTemplate.getForObject(baseUrl + "/increment", String.class);
		model.addAttribute("number", number);
		return "fibonacci";
	}

	@RequestMapping("/reset")
	public String reset(Model model) {
		restTemplate.delete(baseUrl + "/reset");
		model.addAttribute("number", 1);
		return "fibonacci";
	}

	@Bean
	public OAuth2RestOperations restTemplate(@Qualifier("oauth2ClientContext") OAuth2ClientContext oauth2ClientContext) {
		return new OAuth2RestTemplate(resource(), oauth2ClientContext);
	}

	@Bean
	protected OAuth2ProtectedResourceDetails resource() {
		ClientCredentialsResourceDetails resource = new ClientCredentialsResourceDetails();
		resource.setAccessTokenUri(tokenUrl);
		resource.setClientId("my-client-with-secret");
		resource.setClientSecret("secret");
		return resource;
	}

}