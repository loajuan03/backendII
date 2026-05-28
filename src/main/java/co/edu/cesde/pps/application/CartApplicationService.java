package co.edu.cesde.pps.application;

import co.edu.cesde.pps.dto.CartDTO;
import co.edu.cesde.pps.model.UserSession;
import co.edu.cesde.pps.service.CartService;
import co.edu.cesde.pps.service.UserSessionService;
import co.edu.cesde.pps.web.dto.request.AddCartItemRequest;
import co.edu.cesde.pps.web.dto.request.UpdateCartItemQuantityRequest;
import co.edu.cesde.pps.web.dto.response.CartResponse;
import co.edu.cesde.pps.web.mapper.WebResponseMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CartApplicationService {

    private final UserSessionService userSessionService;
    private final CartService cartService;
    private final WebResponseMapper responseMapper;

    public CartApplicationService(UserSessionService userSessionService,
                                  CartService cartService,
                                  WebResponseMapper responseMapper) {
        this.userSessionService = userSessionService;
        this.cartService = cartService;
        this.responseMapper = responseMapper;
    }

    public CartResponse getCurrentCart(String token) {
        UserSession session = userSessionService.requireActiveSession(token);
        return responseMapper.toCartResponse(resolveOpenCart(session));
    }

    @Transactional
    public CartResponse addItem(String token, AddCartItemRequest request) {
        CartDTO cart = resolveOpenCart(userSessionService.requireActiveSession(token));
        return responseMapper.toCartResponse(
                cartService.addItem(cart.getCartId(), request.productId(), request.quantity()));
    }

    @Transactional
    public CartResponse updateItem(String token, Long productId, UpdateCartItemQuantityRequest request) {
        CartDTO cart = resolveOpenCart(userSessionService.requireActiveSession(token));
        return responseMapper.toCartResponse(
                cartService.updateItemQuantity(cart.getCartId(), productId, request.quantity()));
    }

    @Transactional
    public CartResponse removeItem(String token, Long productId) {
        CartDTO cart = resolveOpenCart(userSessionService.requireActiveSession(token));
        return responseMapper.toCartResponse(cartService.removeItem(cart.getCartId(), productId));
    }

    @Transactional
    public void clear(String token) {
        CartDTO cart = resolveOpenCart(userSessionService.requireActiveSession(token));
        cartService.clearCart(cart.getCartId());
    }

    @Transactional
    public CartResponse mergeGuestCart(String token, Long guestCartId) {
        Long userId = userSessionService.requireAuthenticatedUser(token).getUserId();
        return responseMapper.toCartResponse(cartService.mergeGuestCartToUserCart(guestCartId, userId));
    }

    private CartDTO resolveOpenCart(UserSession session) {
        if (session.getUser() == null) {
            return cartService.createCartForGuest(session.getSessionId());
        }

        Long userId = session.getUser().getUserId();
        CartDTO cart = cartService.findOpenCartByUser(userId);
        return cart != null ? cart : cartService.createCartForUser(userId);
    }
}
