package co.edu.cesde.pps.web.controller;

import co.edu.cesde.pps.application.OrderApplicationService;
import co.edu.cesde.pps.web.dto.request.CheckoutRequest;
import co.edu.cesde.pps.web.dto.response.OrderResponse;
import co.edu.cesde.pps.web.security.CurrentSessionResolver;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(ApiRoutes.ORDERS)
public class OrderController {

    private final OrderApplicationService orderApplicationService;
    private final CurrentSessionResolver currentSessionResolver;

    public OrderController(OrderApplicationService orderApplicationService,
                           CurrentSessionResolver currentSessionResolver) {
        this.orderApplicationService = orderApplicationService;
        this.currentSessionResolver = currentSessionResolver;
    }

    @PostMapping("/checkout")
    public OrderResponse checkout(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth,
            @Valid @RequestBody CheckoutRequest request) {
        return orderApplicationService.checkout(currentSessionResolver.resolveCurrentToken(auth), request);
    }

    @GetMapping("/me")
    public List<OrderResponse> listCurrentUserOrders(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth) {
        return orderApplicationService.listCurrentUserOrders(currentSessionResolver.resolveCurrentToken(auth));
    }

    @GetMapping("/{id}")
    public OrderResponse getOrder(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth,
            @PathVariable Long id) {
        return orderApplicationService.getOrder(currentSessionResolver.resolveCurrentToken(auth), id);
    }
}
