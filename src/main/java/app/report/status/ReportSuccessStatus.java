package app.report.status;

import org.springframework.http.HttpStatus;

import app.commonUtil.apiPayload.code.BaseCode;
import app.commonUtil.apiPayload.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReportSuccessStatus implements BaseCode {
	REPORT_REQUESTED(HttpStatus.OK, "REPORT200", "리포트 생성 요청이 완료되었습니다."),
	REPORT_GENERATED(HttpStatus.OK, "REPORT201", "리포트가 생성되었습니다.");


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