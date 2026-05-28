package co.edu.cesde.pps.web.mapper;

import co.edu.cesde.pps.dto.AddressDTO;
import co.edu.cesde.pps.dto.CartDTO;
import co.edu.cesde.pps.dto.CartItemDTO;
import co.edu.cesde.pps.dto.CategoryDTO;
import co.edu.cesde.pps.dto.OrderDTO;
import co.edu.cesde.pps.dto.OrderItemDTO;
import co.edu.cesde.pps.dto.ProductDTO;
import co.edu.cesde.pps.dto.UserDTO;
import co.edu.cesde.pps.model.UserSession;
import co.edu.cesde.pps.web.dto.response.AddressResponse;
import co.edu.cesde.pps.web.dto.response.CartItemResponse;
import co.edu.cesde.pps.web.dto.response.CartResponse;
import co.edu.cesde.pps.web.dto.response.CategoryResponse;
import co.edu.cesde.pps.web.dto.response.OrderItemResponse;
import co.edu.cesde.pps.web.dto.response.OrderResponse;
import co.edu.cesde.pps.web.dto.response.ProductResponse;
import co.edu.cesde.pps.web.dto.response.SessionResponse;
import co.edu.cesde.pps.web.dto.response.UserResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WebResponseMapper {

    public UserResponse toUserResponse(UserDTO dto) {
        if (dto == null) {
            return null;
        }
        return new UserResponse(
                dto.getUserId(),
                dto.getEmail(),
                dto.getFirstName(),
                dto.getLastName(),
                dto.getFullName(),
                dto.getRoleName(),
                dto.getStatus() != null ? dto.getStatus().name() : null);
    }

    public List<UserResponse> toUserResponseList(List<UserDTO> dtos) {
        return dtos.stream().map(this::toUserResponse).toList();
    }

    public ProductResponse toProductResponse(ProductDTO dto) {
        if (dto == null) {
            return null;
        }
        return new ProductResponse(
                dto.getProductId(),
                dto.getCategoryId(),
                dto.getCategoryName(),
                dto.getSku(),
                dto.getName(),
                dto.getDescription(),
                dto.getPrice(),
                dto.getStockQty(),
                dto.getIsActive(),
                dto.getIsAvailable(),
                dto.getCreatedAt());
    }

    public List<ProductResponse> toProductResponses(List<ProductDTO> dtos) {
        return dtos.stream().map(this::toProductResponse).toList();
    }

    public CategoryResponse toCategoryResponse(CategoryDTO dto) {
        if (dto == null) {
            return null;
        }
        List<CategoryResponse> subcategories = dto.getSubcategories() == null
                ? List.of()
                : dto.getSubcategories().stream().map(this::toCategoryResponse).toList();
        return new CategoryResponse(
                dto.getCategoryId(),
                dto.getParentId(),
                dto.getParentName(),
                dto.getName(),
                dto.getSlug(),
                dto.getIsRoot(),
                dto.getSubcategoriesCount(),
                dto.getProductsCount(),
                subcategories);
    }

    public List<CategoryResponse> toCategoryResponses(List<CategoryDTO> dtos) {
        return dtos.stream().map(this::toCategoryResponse).toList();
    }

    public AddressResponse toAddressResponse(AddressDTO dto) {
        if (dto == null) {
            return null;
        }
        return new AddressResponse(
                dto.getAddressId(),
                dto.getUserId(),
                dto.getType(),
                dto.getLine1(),
                dto.getLine2(),
                dto.getCity(),
                dto.getState(),
                dto.getCountry(),
                dto.getPostalCode(),
                dto.getIsDefault(),
                dto.getFullAddress());
    }

    public List<AddressResponse> toAddressResponses(List<AddressDTO> dtos) {
        return dtos.stream().map(this::toAddressResponse).toList();
    }

    public CartResponse toCartResponse(CartDTO dto) {
        if (dto == null) {
            return null;
        }
        List<CartItemResponse> items = dto.getItems() == null
                ? List.of()
                : dto.getItems().stream().map(this::toCartItemResponse).toList();
        return new CartResponse(
                dto.getCartId(),
                dto.getUserId(),
                dto.getUserEmail(),
                dto.getStatus() != null ? dto.getStatus().name() : null,
                dto.getIsGuest(),
                dto.getCreatedAt(),
                dto.getUpdatedAt(),
                items,
                dto.getItemsCount(),
                dto.getTotal());
    }

    public CartItemResponse toCartItemResponse(CartItemDTO dto) {
        if (dto == null) {
            return null;
        }
        return new CartItemResponse(
                dto.getCartItemId(),
                dto.getCartId(),
                dto.getProductId(),
                dto.getProductName(),
                dto.getProductSku(),
                dto.getProductImageUrl(),
                dto.getQuantity(),
                dto.getUnitPrice(),
                dto.getSubtotal(),
                dto.getAddedAt(),
                dto.getProductAvailable(),
                dto.getProductStock());
    }

    public OrderResponse toOrderResponse(OrderDTO dto) {
        if (dto == null) {
            return null;
        }
        List<OrderItemResponse> items = dto.getItems() == null
                ? List.of()
                : dto.getItems().stream().map(this::toOrderItemResponse).toList();
        return new OrderResponse(
                dto.getOrderId(),
                dto.getOrderNumber(),
                dto.getUserId(),
                dto.getUserEmail(),
                dto.getUserFullName(),
                dto.getOrderStatusName(),
                toAddressResponse(dto.getShippingAddress()),
                toAddressResponse(dto.getBillingAddress()),
                items,
                dto.getItemsCount(),
                dto.getSubtotal(),
                dto.getTax(),
                dto.getShippingCost(),
                dto.getTotal(),
                dto.getCreatedAt());
    }

    public List<OrderResponse> toOrderResponses(List<OrderDTO> dtos) {
        return dtos.stream().map(this::toOrderResponse).toList();
    }

    public OrderItemResponse toOrderItemResponse(OrderItemDTO dto) {
        if (dto == null) {
            return null;
        }
        return new OrderItemResponse(
                dto.getOrderItemId(),
                dto.getOrderId(),
                dto.getProductId(),
                dto.getProductName(),
                dto.getProductSku(),
                dto.getProductImageUrl(),
                dto.getQuantity(),
                dto.getUnitPrice(),
                dto.getLineTotal());
    }

    public SessionResponse toSessionResponse(UserSession session) {
        if (session == null) {
            return null;
        }
        return new SessionResponse(
                session.getSessionId(),
                session.getSessionToken(),
                session.getUser() != null ? session.getUser().getUserId() : null,
                session.isGuestSession(),
                session.getCreatedAt(),
                session.getExpiresAt());
    }
}
