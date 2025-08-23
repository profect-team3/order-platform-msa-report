package app.report.model.dto.request;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
		LocalDateTime createdAt,
		String storeName,
		String usersex,           // nullable
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
		LocalDateTime createdAt
	) {}
}
