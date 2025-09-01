// app/report/service/ReportService.java
package app.report;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import app.commonUtil.apiPayload.exception.GeneralException;
import app.report.client.PythonReportClient;
import app.report.model.dto.response.ReportResponse;
import app.report.model.repository.ReportJobMeta;
import app.report.model.repository.ReportJobStore;
import app.report.model.entity.ReportStatus;
import app.report.status.ReportErrorStatus;
import lombok.RequiredArgsConstructor;

import app.report.model.dto.request.PythonReportRequest;
import app.report.model.repository.ReportDatasetRepository;

@Service
@RequiredArgsConstructor
public class ReportService {
	private final PythonReportClient client;
	private final ReportJobStore jobs;
	private final ReportDatasetRepository datasetRepository;

	public ReportResponse requestGenerationAndGetStatus(String storeId, Long userId) {
		var jobId = UUID.randomUUID().toString();
		jobs.createPending(jobId, userId, storeId);

		CompletableFuture.runAsync(() -> {
			try {
				// 최근 한 달 범위 [start, end) — KST 기준
				var zone = ZoneId.of("Asia/Seoul");
				var nowKst = ZonedDateTime.now(zone).toLocalDateTime();
				var start = nowKst.minusMonths(1);
				var end = nowKst;

				var orders  = datasetRepository.findStoreOrdersWithPeriod(storeId, start, end);
				var reviews = datasetRepository.findStoreReviewsWithPeriod(storeId, start, end);
				var payload = new PythonReportRequest(storeId, orders, reviews);

				var res = client.generate(payload);
				jobs.markDone(jobId, res.url(), res.localPath(), res.createdAt());
			} catch (Exception ex) {
				jobs.markFailed(jobId, ex.getClass().getName() + ": " + ex.getMessage());
			}
		});


		var meta = jobs.get(jobId);
		return new ReportResponse(
			meta.jobId()
			// meta.status().name(),   // "PENDING"
			// meta.url(),             // 아직 null
			// meta.localPath(),       // 아직 null
			// meta.createdAt(),       // 아직 null
			// meta.error()            // 아직 null
		);
	}

	public String getReportUrl(String jobId, Long userId) {
		ReportJobMeta meta = jobs.get(jobId);
		if (meta == null) throw new GeneralException(ReportErrorStatus.REPORT_JOB_NOT_FOUND);
		if (!meta.ownerUserId().equals(userId)) throw new GeneralException(ReportErrorStatus.REPORT_ACCESS_DENIED);
		if (meta.status() != ReportStatus.DONE) throw new GeneralException(ReportErrorStatus.REPORT_GENERATION_PENDING);
		if (meta.url() == null || meta.url().isBlank()) throw new GeneralException(ReportErrorStatus.REPORT_GENERATION_FAILED);

		return meta.url();
	}
}
