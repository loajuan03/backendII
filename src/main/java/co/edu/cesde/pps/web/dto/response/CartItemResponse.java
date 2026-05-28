package co.edu.cesde.pps.web.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CartItemResponse(
        Long id,
        Long cartId,
        Long productId,
        String productName,
        String productSku,
        String productImageUrl,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal,
        LocalDateTime addedAt,
        Boolean productAvailable,
        Integer productStock
) {
}
