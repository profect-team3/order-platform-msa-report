package app.report.model.repository;

import java.util.Map;

import org.springframework.stereotype.Component;

import app.report.model.entity.ReportStatus;

@Component
public class ReportJobStore {
	private final Map<String, ReportJobMeta> jobs = new java.util.concurrent.ConcurrentHashMap<>();

	public void createPending(String jobId, Long ownerUserId, String storeId) {
		jobs.put(jobId, new ReportJobMeta(
			jobId, ownerUserId, storeId, ReportStatus.PENDING, null, null, null, null
		));
	}

	public void markDone(String jobId, String url, String localPath, String createdAt) {
		jobs.computeIfPresent(jobId, (k, v) -> v.done(url, localPath, createdAt));
	}

	public void markFailed(String jobId, String error) {
		jobs.computeIfPresent(jobId, (k, v) -> v.failed(error));
	}

	public ReportJobMeta get(String jobId) {
		return jobs.get(jobId);
	}

	// 추후 스케줄러로 호출
	// public void remove(String jobId) {
	// 	jobs.remove(jobId);
	// }
}