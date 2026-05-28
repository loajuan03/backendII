package co.edu.cesde.pps.web.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String orderNumber,
        Long userId,
        String userEmail,
        String userFullName,
        String status,
        AddressResponse shippingAddress,
        AddressResponse billingAddress,
        List<OrderItemResponse> items,
        Integer itemsCount,
        BigDecimal subtotal,
        BigDecimal tax,
        BigDecimal shippingCost,
        BigDecimal total,
        LocalDateTime createdAt
) {
}
