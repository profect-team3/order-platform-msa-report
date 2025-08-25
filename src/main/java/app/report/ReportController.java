package app.report;

import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;

import app.commonSecurity.TokenPrincipalParser;
import app.global.apiPayload.ApiResponse;
import app.global.apiPayload.exception.GeneralException;
import app.report.model.dto.request.ReportRequest;
import app.report.model.dto.response.ReportResponse;
import app.report.status.ReportErrorStatus;
import app.report.status.ReportSuccessStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {
	private final ReportService service;
	private final TokenPrincipalParser tokenPrincipalParser;

	@PostMapping
	public ApiResponse<String> create(@Valid @RequestBody ReportRequest req, Authentication authentication) {

		Long userId = Long.parseLong(tokenPrincipalParser.getUserId(authentication));
		ReportResponse jobId = service.requestGenerationAndGetStatus(req.storeId(), userId);

		return ApiResponse.onSuccess(ReportSuccessStatus.REPORT_REQUESTED, jobId.jobId());
	}

	@GetMapping("/{jobId}/download")
	public ApiResponse<Resource> download(@PathVariable String jobId, Authentication authentication) {

		Long userId = Long.parseLong(tokenPrincipalParser.getUserId(authentication));
		var file = service.getFile(jobId, userId);

		if (!file.exists())
			throw new GeneralException(ReportErrorStatus.REPORT_GENERATION_FAILED);
		return ApiResponse.onSuccess(ReportSuccessStatus.REPORT_GENERATED, file);
			// .contentType(MediaType.APPLICATION_PDF)
			// .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getFilename())
			// .body(file);
	}
}
