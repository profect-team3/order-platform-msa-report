package app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import app.report.ReportService;
import app.report.client.PythonReportClient;
import app.report.model.dto.response.ReportResponse;
import app.report.model.repository.ReportDatasetRepository;
import app.report.model.repository.ReportJobMeta;
import app.report.model.repository.ReportJobStore;
import app.report.model.entity.ReportStatus;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReportService Test")
class ReportServiceTest {

    @InjectMocks
    private ReportService reportService;

    @Mock
    private PythonReportClient pythonReportClient;

    @Mock
    private ReportJobStore reportJobStore;

    @Mock
    private ReportDatasetRepository reportDatasetRepository;

    private String testStoreId;
    private Long testUserId;
    private String testJobId;

    @BeforeEach
    void setUp() {
        testStoreId = UUID.randomUUID().toString();
        testUserId = 1L;
        testJobId = UUID.randomUUID().toString();
    }

    @Test
    @DisplayName("리포트 생성 요청 및 상태 확인 - 성공")
    void requestGenerationAndGetStatus_Success() {
        // given
        ReportJobMeta mockMeta = new ReportJobMeta(testJobId, testUserId, testStoreId, ReportStatus.PENDING, null, null, null, null);

        when(reportJobStore.get(anyString())).thenReturn(mockMeta);

        // when
        ReportResponse response = reportService.requestGenerationAndGetStatus(testStoreId, testUserId);

        // then
        assertNotNull(response);
        assertNotNull(response.jobId());

        verify(reportJobStore).createPending(anyString(), eq(testUserId), eq(testStoreId));
    }
}
