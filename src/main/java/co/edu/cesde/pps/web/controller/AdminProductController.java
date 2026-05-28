package co.edu.cesde.pps.web.controller;

import co.edu.cesde.pps.application.CatalogApplicationService;
import co.edu.cesde.pps.web.dto.request.ProductUpsertRequest;
import co.edu.cesde.pps.web.dto.response.ProductResponse;
import co.edu.cesde.pps.web.security.CurrentSessionResolver;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiRoutes.ADMIN_PRODUCTS)
public class AdminProductController {

    private final CatalogApplicationService catalogApplicationService;
    private final CurrentSessionResolver currentSessionResolver;

    public AdminProductController(CatalogApplicationService catalogApplicationService,
                                  CurrentSessionResolver currentSessionResolver) {
        this.catalogApplicationService = catalogApplicationService;
        this.currentSessionResolver = currentSessionResolver;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth,
            @Valid @RequestBody ProductUpsertRequest request) {
        currentSessionResolver.resolveAuthenticatedUser(auth);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(catalogApplicationService.createProduct(request));
    }

    @PutMapping("/{id}")
    public ProductResponse updateProduct(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth,
            @PathVariable Long id,
            @Valid @RequestBody ProductUpsertRequest request) {
        currentSessionResolver.resolveAuthenticatedUser(auth);
        return catalogApplicationService.updateProduct(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth,
            @PathVariable Long id) {
        currentSessionResolver.resolveAuthenticatedUser(auth);
        catalogApplicationService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
