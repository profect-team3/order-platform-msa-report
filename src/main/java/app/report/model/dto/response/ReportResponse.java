package app.report.model.dto.response;

public record ReportResponse(
	String jobId
	// String status          // PENDING | DONE | FAILED
	// String url,
	// String localPath,       // 프록시용 내부 경로
	// String createdAt,
	// String error
) {}
