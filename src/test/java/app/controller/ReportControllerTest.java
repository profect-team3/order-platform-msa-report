package app.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import app.commonSecurity.TokenPrincipalParser;
import app.global.apiPayload.exception.GeneralException;
import app.report.ReportController;
import app.report.ReportService;
import app.report.model.dto.request.ReportRequest;
import app.report.model.dto.response.ReportResponse;
import app.report.status.ReportErrorStatus;

@WebMvcTest(ReportController.class)
@DisplayName("ReportController Test")
public class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReportService reportService;

    @MockitoBean
    private TokenPrincipalParser tokenPrincipalParser;

    private final Long USER_ID = 1L;
    private final String TEST_JOB_ID = "job-123";

    @BeforeEach
    void setUp() {
        when(tokenPrincipalParser.getUserId(any())).thenReturn(String.valueOf(USER_ID));
    }

    @Nested
    @DisplayName("Report Generation Tests")
    class ReportGenerationTests {

        @Test
        @DisplayName("POST /report - 리포트 생성 요청 성공")
        @WithMockUser(roles = "OWNER")
        void createReport_Success() throws Exception {
            // given
            final String TEST_STORE_ID = "store-123";
            ReportRequest request = new ReportRequest(TEST_STORE_ID);
            ReportResponse mockResponse = ReportResponse.builder().jobId(TEST_JOB_ID).build();
            when(reportService.requestGenerationAndGetStatus(anyString(), anyLong())).thenReturn(mockResponse);

            // when & then
            mockMvc.perform(post("/report")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result").value(TEST_JOB_ID));
        }
    }

    @Nested
    @DisplayName("Report Download Tests")
    class ReportDownloadTests {

        @Test
        @DisplayName("GET /report/{jobId}/download - 리포트 다운로드 URL 조회 성공")
        @WithMockUser(roles = "OWNER")
        void downloadReport_Success() throws Exception {
            // given
            final String TEST_S3_URL = "https://s3.amazonaws.com/bucket/report.pdf";
            when(reportService.getReportUrl(anyString(), anyLong())).thenReturn(TEST_S3_URL);

            // when & then
            mockMvc.perform(get("/report/{jobId}/download", TEST_JOB_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result").value(TEST_S3_URL));
        }

        @Test
        @DisplayName("GET /report/{jobId}/download - 실패 (Job Not Found)")
        @WithMockUser(roles = "OWNER")
        void downloadReport_JobNotFound() throws Exception {
            // given
            when(reportService.getReportUrl(anyString(), anyLong())).thenThrow(new GeneralException(ReportErrorStatus.REPORT_JOB_NOT_FOUND));

            // when & then
            mockMvc.perform(get("/report/{jobId}/download", TEST_JOB_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("REPORT001"));
        }

        @Test
        @DisplayName("GET /report/{jobId}/download - 실패 (Access Denied)")
        @WithMockUser(roles = "OWNER")
        void downloadReport_AccessDenied() throws Exception {
            // given
            when(reportService.getReportUrl(anyString(), anyLong())).thenThrow(new GeneralException(ReportErrorStatus.REPORT_ACCESS_DENIED));

            // when & then
            mockMvc.perform(get("/report/{jobId}/download", TEST_JOB_ID))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("REPORT003"));
        }

        @Test
        @DisplayName("GET /report/{jobId}/download - 실패 (Pending)")
        @WithMockUser(roles = "OWNER")
        void downloadReport_Pending() throws Exception {
            // given
            when(reportService.getReportUrl(anyString(), anyLong())).thenThrow(new GeneralException(ReportErrorStatus.REPORT_GENERATION_PENDING));

            // when & then
            mockMvc.perform(get("/report/{jobId}/download", TEST_JOB_ID))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("REPORT002"));
        }
    }
}
