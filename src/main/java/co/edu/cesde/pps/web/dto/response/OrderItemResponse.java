package co.edu.cesde.pps.web.dto.response;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long id,
        Long orderId,
        Long productId,
        String productName,
        String productSku,
        String productImageUrl,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal
) {
}
