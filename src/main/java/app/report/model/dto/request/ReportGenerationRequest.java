package app.report.model.dto.request;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public record ReportGenerationRequest(
	String storeId,
	List<OrderRow> orders,
	List<ReviewRow> reviews
) {
	public record OrderRow(
		String orderId,
		int totalPrice,
		String orderChannel,
		String receiptMethod,
		String paymentMethod,
		String orderStatus,
		String requestMessage,   // nullable
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
		LocalDateTime createdAt,
		String storeName,
		String usersex,           // nullable
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
		LocalDate birthdate,     // nullable
		boolean isFirstOrderInStore,
		int orderSeqInStore,
		List<OrderItemRow> items
	) {}

	public record OrderItemRow(
		String orderItemId,
		String menuName,
		int price,
		int quantity
	) {}

	public record ReviewRow(
		int rating,
		String content,
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
		LocalDateTime createdAt
	) {}
}
