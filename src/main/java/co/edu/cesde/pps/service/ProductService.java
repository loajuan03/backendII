package co.edu.cesde.pps.service;

import co.edu.cesde.pps.dto.ProductDTO;
import co.edu.cesde.pps.exception.DuplicateEntityException;
import co.edu.cesde.pps.exception.EntityNotFoundException;
import co.edu.cesde.pps.exception.InsufficientStockException;
import co.edu.cesde.pps.mapper.ProductMapper;
import co.edu.cesde.pps.model.Category;
import co.edu.cesde.pps.model.Product;
import co.edu.cesde.pps.repository.CategoryRepository;
import co.edu.cesde.pps.repository.ProductRepository;
import co.edu.cesde.pps.util.CalculationUtils;
import co.edu.cesde.pps.util.ValidationUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductMapper productMapper;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productMapper = new ProductMapper();
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO) {
        validateProductData(productDTO);

        if (existsBySku(productDTO.getSku())) {
            throw new DuplicateEntityException("Product", "sku", productDTO.getSku());
        }

        Category category = findCategoryOrThrow(productDTO.getCategoryId());
        Product product = productMapper.toEntity(productDTO);
        product.setProductId(null);
        product.setCategory(category);
        product.setIsActive(productDTO.getIsActive() != null ? productDTO.getIsActive() : true);
        product.setCreatedAt(LocalDateTime.now());

        return productMapper.toDTO(productRepository.save(product));
    }

    @Transactional
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        Product product = findProductEntityOrThrow(productId);
        validateProductData(productDTO);

        if (!product.getSku().equalsIgnoreCase(productDTO.getSku()) && existsBySku(productDTO.getSku())) {
            throw new DuplicateEntityException("Product", "sku", productDTO.getSku());
        }

        product.setSku(productDTO.getSku().trim());
        product.setName(productDTO.getName().trim());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setStockQty(productDTO.getStockQty());
        product.setIsActive(productDTO.getIsActive() != null ? productDTO.getIsActive() : product.getIsActive());

        if (!productDTO.getCategoryId().equals(product.getCategory().getCategoryId())) {
            product.setCategory(findCategoryOrThrow(productDTO.getCategoryId()));
        }

        return productMapper.toDTO(productRepository.save(product));
    }

    @Transactional
    public void deleteProduct(Long productId) {
        Product product = findProductEntityOrThrow(productId);
        product.setIsActive(false);
        productRepository.save(product);
    }

    public ProductDTO findById(Long productId) {
        return productMapper.toDTO(findProductEntityOrThrow(productId));
    }

    public ProductDTO findBySku(String sku) {
        Product product = productRepository.findBySkuIgnoreCase(sku)
                .orElseThrow(() -> new EntityNotFoundException("Product with SKU: " + sku));
        return productMapper.toDTO(product);
    }

    public List<ProductDTO> findAllProducts() {
        return productMapper.toDTOList(productRepository.findAll());
    }

    public List<ProductDTO> findActiveProducts() {
        return productMapper.toDTOList(productRepository.findByIsActiveTrue());
    }

    public List<ProductDTO> findByCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new EntityNotFoundException("Category", categoryId);
        }
        return productMapper.toDTOList(productRepository.findByCategory_CategoryId(categoryId));
    }

    public List<ProductDTO> searchByName(String name) {
        return productMapper.toDTOList(productRepository.findByNameContainingIgnoreCase(name));
    }

    public boolean checkAvailability(Long productId, Integer quantity) {
        Product product = findProductEntityOrThrow(productId);
        return Boolean.TRUE.equals(product.getIsActive())
                && CalculationUtils.hasEnoughStock(product.getStockQty(), quantity);
    }

    public boolean hasEnoughStock(Long productId, Integer quantity) {
        Product product = findProductEntityOrThrow(productId);
        return CalculationUtils.hasEnoughStock(product.getStockQty(), quantity);
    }

    @Transactional
    public void updateStock(Long productId, Integer newStock) {
        ValidationUtils.validateNotNull(newStock, "stock");
        ValidationUtils.validateNonNegative(newStock, "stock");

        Product product = findProductEntityOrThrow(productId);
        product.setStockQty(newStock);
        productRepository.save(product);
    }

    @Transactional
    public void decreaseStock(Long productId, Integer quantity) {
        Product product = findProductEntityOrThrow(productId);

        if (!CalculationUtils.hasEnoughStock(product.getStockQty(), quantity)) {
            throw new InsufficientStockException(productId, product.getSku(), quantity, product.getStockQty());
        }

        product.setStockQty(CalculationUtils.calculateNewStock(product.getStockQty(), quantity));
        productRepository.save(product);
    }

    @Transactional
    public void increaseStock(Long productId, Integer quantity) {
        ValidationUtils.validatePositive(quantity, "quantity");

        Product product = findProductEntityOrThrow(productId);
        product.setStockQty(product.getStockQty() + quantity);
        productRepository.save(product);
    }

    public boolean existsBySku(String sku) {
        return productRepository.existsBySkuIgnoreCase(sku);
    }

    public Product findProductEntityOrThrow(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product", productId));
    }

    private Category findCategoryOrThrow(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category", categoryId));
    }

    private void validateProductData(ProductDTO productDTO) {
        ValidationUtils.validateNotNull(productDTO, "product");
        ValidationUtils.validateNotNull(productDTO.getCategoryId(), "categoryId");
        ValidationUtils.validateNotBlank(productDTO.getSku(), "sku");
        ValidationUtils.validateNotBlank(productDTO.getName(), "name");
        ValidationUtils.validateNonNegative(productDTO.getPrice(), "price");
        ValidationUtils.validateNotNull(productDTO.getStockQty(), "stockQty");
        ValidationUtils.validateNonNegative(productDTO.getStockQty(), "stockQty");
    }
}
