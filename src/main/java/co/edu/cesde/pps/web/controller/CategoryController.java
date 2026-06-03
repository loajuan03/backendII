package co.edu.cesde.pps.web.controller;

import co.edu.cesde.pps.application.CatalogApplicationService;
import co.edu.cesde.pps.web.dto.response.CategoryResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiRoutes.CATEGORIES)
@CrossOrigin(origins = "http://localhost:5173")
public class CategoryController {

    private final CatalogApplicationService catalogApplicationService;

    public CategoryController(CatalogApplicationService catalogApplicationService) {
        this.catalogApplicationService = catalogApplicationService;
    }

    @GetMapping
    public List<CategoryResponse> listCategories() {
        return catalogApplicationService.listCategories();
    }

    @GetMapping("/{id}")
    public CategoryResponse getCategory(@PathVariable Long id) {
        return catalogApplicationService.getCategory(id);
    }
}
