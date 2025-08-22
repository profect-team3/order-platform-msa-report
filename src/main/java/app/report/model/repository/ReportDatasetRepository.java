// app/report/model/repository/ReportDatasetRepository.java
package app.report.model.repository;

import app.report.model.dto.request.ReportPayload;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class ReportDatasetRepository {
	private final JdbcTemplate jdbc;
	private final ObjectMapper om = new ObjectMapper();

	 // 첫/재주문 여부는 전체 히스토리에서 판단
	 // 최종 SELECT에서 [start, end) 기간으로 표시만 제한.
	public List<ReportPayload.OrderRow> findStoreOrdersWithPeriod(
		String storeId, LocalDateTime startInclusive, LocalDateTime endExclusive
	) {
		String sql = """
			WITH ranked AS (
				SELECT
				o.orders_id,
				o.store_id,
				o.user_id,
				o.total_price,
				o.order_channel,
				o.receipt_method,
				o.payment_method,
				o.order_status,
				o.request_message,
				o.created_at,
				ROW_NUMBER() OVER (
					PARTITION BY o.store_id, o.user_id
					ORDER BY o.created_at
				) AS order_seq_in_store
				FROM p_orders o
				WHERE o.store_id = ?::uuid
				),
				items AS (
					SELECT
				i.orders_id,
					jsonb_agg(
						jsonb_build_object(
							'orderItemId', i.order_item_id,
							'menuName',    i.menu_name,
							'price',       i.price,
							'quantity',    i.quantity
						)
						ORDER BY i.order_item_id
					) AS items_json
				FROM p_b_order_item i
				GROUP BY i.orders_id
				)
				SELECT
				r.orders_id,
					r.total_price,
					r.order_channel,
					r.receipt_method,
					r.payment_method,
					r.order_status,
					r.request_message,
					r.created_at,
					s.store_name,
					u.usersex,
					u.birthdate,
					(r.order_seq_in_store = 1) AS is_first_order_in_store,
				r.order_seq_in_store,
					it.items_json
				FROM ranked r
				JOIN p_store s ON s.store_id = r.store_id
				JOIN p_user  u ON u.user_id  = r.user_id
				JOIN items it  ON it.orders_id = r.orders_id
				WHERE r.created_at >= ? AND r.created_at < ?
				ORDER BY r.created_at DESC, r.orders_id;
		""";

		return jdbc.query(sql, (rs, rowNum) -> {
			List<ReportPayload.OrderItemRow> items = null;
			try {
				items = om.readValue(
					rs.getString("items_json"),
					new TypeReference<List<ReportPayload.OrderItemRow>>() {}
				);
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}

			return new ReportPayload.OrderRow(
				(UUID) rs.getObject("order_id"),
				rs.getInt("total_price"),
				rs.getString("order_channel"),
				rs.getString("receipt_method"),
				rs.getString("payment_method"),
				rs.getString("order_status"),
				rs.getString("request_message"),
				rs.getTimestamp("created_at").toLocalDateTime(),
				rs.getString("store_name"),
				rs.getString("usersex"),
				rs.getObject("birthdate", LocalDate.class),
				rs.getBoolean("is_first_order_in_store"),
				rs.getInt("order_seq_in_store"),
				items
			);
		}, UUID.fromString(storeId), startInclusive, endExclusive);
	}
}