package co.edu.cesde.pps.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ProductUpsertRequest(
        @NotNull Long categoryId,
        @NotBlank String sku,
        @NotBlank String name,
        String description,
        @NotNull @PositiveOrZero BigDecimal price,
        @NotNull @PositiveOrZero Integer stockQty,
        @NotNull Boolean isActive
) {
}
