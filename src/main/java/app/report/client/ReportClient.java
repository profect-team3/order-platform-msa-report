// app/report/client/ReportClient.java
package app.report.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;

import app.report.model.dto.request.ReportPayload;

@Component
@RequiredArgsConstructor
public class ReportClient {
	private final WebClient reportWebClient;

	public record GenerateJsonRes(String url, String localPath, String createdAt) {}

	public GenerateJsonRes generate(ReportPayload payload) {
		return reportWebClient.post().uri("/report/generate-json")
			.bodyValue(payload)
			.retrieve()
			.bodyToMono(GenerateJsonRes.class)
			.block();
	}
}
