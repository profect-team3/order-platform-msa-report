package app.report.client;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReportClient {
	private final WebClient reportWebClient;

	public record GenerateJsonRes(String url, String localPath, String createdAt) {}

	public GenerateJsonRes generate(String storeId, String period) {
		return reportWebClient.post().uri("/report/generate-json")
			.bodyValue(Map.of("storeId", storeId, "period", period))
			.retrieve()
			.bodyToMono(GenerateJsonRes.class)
			.block();
	}
}

