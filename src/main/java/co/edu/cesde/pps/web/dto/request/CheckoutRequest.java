package co.edu.cesde.pps.web.dto.request;

public record CheckoutRequest(Long shippingAddressId, Long billingAddressId) {
}
