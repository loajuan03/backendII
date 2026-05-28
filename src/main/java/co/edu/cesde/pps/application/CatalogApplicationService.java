package co.edu.cesde.pps.application;

import co.edu.cesde.pps.dto.CategoryDTO;
import co.edu.cesde.pps.dto.ProductDTO;
import co.edu.cesde.pps.service.CategoryService;
import co.edu.cesde.pps.service.ProductService;
import co.edu.cesde.pps.web.dto.request.CategoryUpsertRequest;
import co.edu.cesde.pps.web.dto.request.ProductUpsertRequest;
import co.edu.cesde.pps.web.dto.response.CategoryResponse;
import co.edu.cesde.pps.web.dto.response.ProductResponse;
import co.edu.cesde.pps.web.mapper.WebRequestMapper;
import co.edu.cesde.pps.web.mapper.WebResponseMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CatalogApplicationService {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final WebRequestMapper requestMapper;
    private final WebResponseMapper responseMapper;

    public CatalogApplicationService(ProductService productService,
                                     CategoryService categoryService,
                                     WebRequestMapper requestMapper,
                                     WebResponseMapper responseMapper) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.requestMapper = requestMapper;
        this.responseMapper = responseMapper;
    }

    public List<ProductResponse> listProducts() {
        return responseMapper.toProductResponses(productService.findActiveProducts());
    }

    public ProductResponse getProduct(Long id) {
        return responseMapper.toProductResponse(productService.findById(id));
    }

    public List<CategoryResponse> listCategories() {
        return responseMapper.toCategoryResponses(categoryService.findAllCategories());
    }

    public CategoryResponse getCategory(Long id) {
        return responseMapper.toCategoryResponse(categoryService.findById(id));
    }

    @Transactional
    public CategoryResponse createCategory(CategoryUpsertRequest request) {
        CategoryDTO dto = categoryService.createCategory(requestMapper.toCategoryDTO(request));
        return responseMapper.toCategoryResponse(dto);
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryUpsertRequest request) {
        CategoryDTO dto = categoryService.updateCategory(id, requestMapper.toCategoryDTO(request));
        return responseMapper.toCategoryResponse(dto);
    }

    @Transactional
    public void deleteCategory(Long id) {
        categoryService.deleteCategory(id);
    }

    @Transactional
    public ProductResponse createProduct(ProductUpsertRequest request) {
        ProductDTO dto = productService.createProduct(requestMapper.toProductDTO(request));
        return responseMapper.toProductResponse(dto);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductUpsertRequest request) {
        ProductDTO dto = productService.updateProduct(id, requestMapper.toProductDTO(request));
        return responseMapper.toProductResponse(dto);
    }

    @Transactional
    public void deleteProduct(Long id) {
        productService.deleteProduct(id);
    }
}
