package app.report.status;

import org.springframework.http.HttpStatus;

import app.global.apiPayload.code.BaseCode;
import app.global.apiPayload.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReportErrorStatus implements BaseCode {
	REPORT_JOB_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "REPORT001", "리포트 생성 요청 작업을 찾을 수 없습니다."),
	REPORT_GENERATION_PENDING(HttpStatus.INTERNAL_SERVER_ERROR, "REPORT002", "리포트 생성 대기 중입니다."),
	REPORT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "REPORT003", "해당 가게 리포트에 대한 접근 권한이 없습니다."),
	REPORT_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "REPORT004", "리포트 생성에 실패했습니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

	@Override
	public ReasonDTO getReason() {
		return ReasonDTO.builder()
			.message(message)
			.code(code)
			.build();
	}

	@Override
	public ReasonDTO getReasonHttpStatus() {
		return ReasonDTO.builder()
			.isSuccess(false)
			.message(message)
			.code(code)
			.httpStatus(httpStatus)
			.build();
	}
}