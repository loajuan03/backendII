package co.edu.cesde.pps.application;

import co.edu.cesde.pps.dto.CartDTO;
import co.edu.cesde.pps.dto.OrderDTO;
import co.edu.cesde.pps.exception.ValidationException;
import co.edu.cesde.pps.model.User;
import co.edu.cesde.pps.service.CartService;
import co.edu.cesde.pps.service.OrderService;
import co.edu.cesde.pps.service.UserSessionService;
import co.edu.cesde.pps.web.dto.request.CheckoutRequest;
import co.edu.cesde.pps.web.dto.response.OrderResponse;
import co.edu.cesde.pps.web.mapper.WebResponseMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class OrderApplicationService {

    private final UserSessionService userSessionService;
    private final CartService cartService;
    private final OrderService orderService;
    private final WebResponseMapper responseMapper;

    public OrderApplicationService(UserSessionService userSessionService,
                                   CartService cartService,
                                   OrderService orderService,
                                   WebResponseMapper responseMapper) {
        this.userSessionService = userSessionService;
        this.cartService = cartService;
        this.orderService = orderService;
        this.responseMapper = responseMapper;
    }

    @Transactional
    public OrderResponse checkout(String token, CheckoutRequest request) {
        User user = userSessionService.requireAuthenticatedUser(token);
        CartDTO cart = cartService.findOpenCartByUser(user.getUserId());
        if (cart == null) {
            throw new ValidationException("Cannot checkout without an open cart");
        }

        OrderDTO order = orderService.checkout(
                user.getUserId(),
                cart.getCartId(),
                request.shippingAddressId(),
                request.billingAddressId());

        return responseMapper.toOrderResponse(order);
    }

    public List<OrderResponse> listCurrentUserOrders(String token) {
        User user = userSessionService.requireAuthenticatedUser(token);
        return responseMapper.toOrderResponses(orderService.findOrdersByUser(user.getUserId()));
    }

    public OrderResponse getOrder(String token, Long orderId) {
        userSessionService.requireAuthenticatedUser(token);
        return responseMapper.toOrderResponse(orderService.findById(orderId));
    }
}
