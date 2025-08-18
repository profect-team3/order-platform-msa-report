package app.report.model.repository;

import app.report.model.entity.enums.ReportStatus;

public record ReportJobMeta(
	String jobId,
	String ownerUserId,
	String storeId,
	ReportStatus status,
	String url,
	String localPath,
	String createdAt,
	String error
) {
	public ReportJobMeta withStatus(ReportStatus s) {
		return new ReportJobMeta(jobId, ownerUserId, storeId, s, url, localPath, createdAt, error);
	}
	public ReportJobMeta done(String url, String localPath, String createdAt) {
		return new ReportJobMeta(jobId, ownerUserId, storeId, ReportStatus.DONE, url, localPath, createdAt, null);
	}
	public ReportJobMeta failed(String err) {
		return new ReportJobMeta(jobId, ownerUserId, storeId, ReportStatus.FAILED, url, localPath, createdAt, err);
	}
}
