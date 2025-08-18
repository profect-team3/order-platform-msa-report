package app.report.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import java.time.Duration;

@Configuration
public class WebClientConfig {
	@Bean
	public WebClient reportWebClient(@Value("${report.python.base-url}") String baseUrl,
		@Value("${report.python.timeout:60s}") Duration timeout) {
		var http = HttpClient.create().responseTimeout(timeout);
		return WebClient.builder()
			.baseUrl(baseUrl)
			.clientConnector(new ReactorClientHttpConnector(http))
			.build();
	}
}

