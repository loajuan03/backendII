package co.edu.cesde.pps.web.controller;

import co.edu.cesde.pps.application.CartApplicationService;
import co.edu.cesde.pps.web.dto.request.AddCartItemRequest;
import co.edu.cesde.pps.web.dto.request.UpdateCartItemQuantityRequest;
import co.edu.cesde.pps.web.dto.response.CartResponse;
import co.edu.cesde.pps.web.security.CurrentSessionResolver;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiRoutes.CART)
public class CartController {

    private final CartApplicationService cartApplicationService;
    private final CurrentSessionResolver currentSessionResolver;

    public CartController(CartApplicationService cartApplicationService,
                          CurrentSessionResolver currentSessionResolver) {
        this.cartApplicationService = cartApplicationService;
        this.currentSessionResolver = currentSessionResolver;
    }

    @GetMapping("/me")
    public CartResponse getCurrentCart(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth) {
        return cartApplicationService.getCurrentCart(currentSessionResolver.resolveCurrentToken(auth));
    }

    @PostMapping("/items")
    public CartResponse addItem(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth,
            @Valid @RequestBody AddCartItemRequest request) {
        return cartApplicationService.addItem(currentSessionResolver.resolveCurrentToken(auth), request);
    }

    @PutMapping("/items/{productId}")
    public CartResponse updateItem(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth,
            @PathVariable Long productId,
            @Valid @RequestBody UpdateCartItemQuantityRequest request) {
        return cartApplicationService.updateItem(
                currentSessionResolver.resolveCurrentToken(auth),
                productId,
                request);
    }

    @DeleteMapping("/items/{productId}")
    public CartResponse removeItem(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth,
            @PathVariable Long productId) {
        return cartApplicationService.removeItem(currentSessionResolver.resolveCurrentToken(auth), productId);
    }

    @DeleteMapping("/items")
    public ResponseEntity<Void> clear(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth) {
        cartApplicationService.clear(currentSessionResolver.resolveCurrentToken(auth));
        return ResponseEntity.noContent().build();
    }
}
