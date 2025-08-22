// app/report/service/ReportService.java
package app.report.service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import app.report.client.ReportClient;
import app.report.model.dto.ReportStatusResponse;
import app.report.model.repository.ReportJobMeta;
import app.report.model.repository.ReportJobStore;
import app.report.model.entity.enums.ReportStatus;
import lombok.RequiredArgsConstructor;

import app.report.model.dto.request.ReportPayload;
import app.report.model.repository.ReportDatasetRepository;

@Service
@RequiredArgsConstructor
public class ReportService {
	private final ReportClient client;
	private final ReportJobStore jobs;
	private final ReportDatasetRepository datasetRepo;

	public String requestGeneration(String ownerUserId, String storeId) {
		var jobId = UUID.randomUUID().toString();
		jobs.createPending(jobId, ownerUserId, storeId);

		CompletableFuture.runAsync(() -> {
			try {
				// 최근 한 달 범위 [start, end) — KST 기준 롤링 1개월
				var zone = ZoneId.of("Asia/Seoul");
				var nowKst = ZonedDateTime.now(zone).toLocalDateTime();
				var start = nowKst.minusMonths(1); // 오늘 기준 1개월 전 (롤링)
				var end   = nowKst;

				var orders = datasetRepo.findStoreOrdersWithPeriod(storeId, start, end);

				var payload = new ReportPayload(storeId, orders);
				var res = client.generate(payload);

				jobs.markDone(jobId, res.url(), res.localPath(), res.createdAt());
			} catch (Exception ex) {
				jobs.markFailed(jobId, ex.getClass().getName() + ": " + ex.getMessage());
			}
		});

		return jobId;
	}

	public ReportStatusResponse getStatus(String jobId) {
		var meta = jobs.get(jobId);
		if (meta == null) return null;
		return new ReportStatusResponse(meta.jobId(), meta.status().name(), meta.url(), meta.localPath(), meta.createdAt(), meta.error());
	}

	public FileSystemResource getFile(String jobId, String requestUserId) {
		ReportJobMeta meta = jobs.get(jobId);
		if (meta == null) throw new IllegalStateException("job not found");
		// 시큐리티 적용 예정
		if (!meta.ownerUserId().equals(requestUserId)) throw new IllegalStateException("forbidden");
		if (meta.status() != ReportStatus.DONE) throw new IllegalStateException("not ready");
		return new FileSystemResource(meta.localPath());
	}
}
