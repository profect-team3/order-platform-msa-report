// app/report/client/PythonReportClient.java
package app.report.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;

import app.report.model.dto.request.PythonReportRequest;

@Component
@RequiredArgsConstructor
public class PythonReportClient {
	private final WebClient reportWebClient;

	public record GenerateJsonRes(String url, String localPath, String createdAt) {}

	public GenerateJsonRes generate(PythonReportRequest payload) {
		return reportWebClient.post().uri("/report/generate-json")
			.bodyValue(payload)
			.retrieve()
			.bodyToMono(GenerateJsonRes.class)
			.block();
	}
}
