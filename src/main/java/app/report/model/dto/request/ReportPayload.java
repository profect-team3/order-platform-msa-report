// app/report/model/dto/ReportPayload.java
package app.report.model.dto.request;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ReportPayload(
	String storeId,
	List<OrderRow> orders
) {
	public record OrderRow(
		UUID orderId,
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
		int orderSeqInStore,     // 1이면 첫 주문, 2부터 재주문
		List<OrderItemRow> items
	) {}

	public record OrderItemRow(
		UUID orderItemId,
		String menuName,
		int price,
		int quantity
	) {}
}
