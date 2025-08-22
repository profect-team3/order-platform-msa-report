package app.report.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;

import app.report.model.dto.GenerateRequest;
import app.report.model.dto.ReportStatusResponse;
import app.report.service.ReportService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {
	private final ReportService service;

	// 시큐리티 적용 예정
	private String requestUserId(HttpHeaders headers) {
		return headers.getFirst("X-USER-ID") != null ? headers.getFirst("X-USER-ID") : "demo-user";
	}

	@PostMapping
	// public ResponseEntity<?> create(@Valid @RequestBody GenerateRequest req, @RequestHeader HttpHeaders headers) {
	public ResponseEntity<?> create(@RequestBody GenerateRequest req, @RequestHeader HttpHeaders headers) {
		var jobId = service.requestGeneration(requestUserId(headers), req.storeId());
		return ResponseEntity.accepted().body(java.util.Map.of("jobId", jobId));
	}

	@GetMapping("/{jobId}")
	public ResponseEntity<?> status(@PathVariable String jobId) {
		ReportStatusResponse s = service.getStatus(jobId);
		if (s == null) return ResponseEntity.notFound().build();
		return ResponseEntity.ok(s);
	}

	@GetMapping("/{jobId}/download")
	public ResponseEntity<Resource> download(@PathVariable String jobId, @RequestHeader HttpHeaders headers) {
		var file = service.getFile(jobId, requestUserId(headers));
		if (!file.exists()) return ResponseEntity.notFound().build();
		return ResponseEntity.ok()
			.contentType(MediaType.APPLICATION_PDF)
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getFilename())
			.body(file);
	}
}
