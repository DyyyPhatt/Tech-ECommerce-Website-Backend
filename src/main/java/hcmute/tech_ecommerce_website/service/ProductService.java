package hcmute.tech_ecommerce_website.service;

import hcmute.tech_ecommerce_website.model.Brand;
import hcmute.tech_ecommerce_website.model.PriceHistory;
import hcmute.tech_ecommerce_website.model.Product;
import hcmute.tech_ecommerce_website.repository.*;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private ProductCategoryRepository categoryRepository;

    @Autowired
    private ProductConditionRepository conditionRepository;

    @Autowired
    private PriceHistoryRepository priceHistoryRepository;

    @Autowired
    private PriceHistoryService priceHistoryService;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    public List<Product> getAllProductsSorted(String sortBy, List<ObjectId> brandIds, List<ObjectId> categoryIds, List<ObjectId> conditionIds, List<ObjectId> tagIds, BigDecimal minPrice, BigDecimal maxPrice) {
        Sort sort;
        switch (sortBy.toLowerCase()) {
            case "oldest":
                sort = Sort.by(Direction.ASC, "createdAt");
                break;
            case "newest":
                sort = Sort.by(Direction.DESC, "createdAt");
                break;
            case "discountprice-low-to-high":
                sort = Sort.by(Direction.ASC, "discountPrice");
                break;
            case "discountprice-high-to-low":
                sort = Sort.by(Direction.DESC, "discountPrice");
                break;
            case "rating-high-to-low":
                sort = Sort.by(Direction.DESC, "ratings.average");
                break;
            case "rating-low-to-high":
                sort = Sort.by(Direction.ASC, "ratings.average");
                break;
            default:
                sort = Sort.by(Direction.DESC, "createdAt");
        }

        List<Product> products = productRepository.findAll(sort).stream()
                .filter(product -> {
                    boolean matches = true;
                    if (brandIds != null && !brandIds.isEmpty()) {
                        matches = matches && brandIds.contains(product.getBrand());
                    }
                    if (categoryIds != null && !categoryIds.isEmpty()) {
                        matches = matches && categoryIds.contains(product.getCategory());
                    }
                    if (conditionIds != null && !conditionIds.isEmpty()) {
                        matches = matches && conditionIds.contains(product.getCondition());
                    }
                    if (tagIds != null && !tagIds.isEmpty()) {
                        matches = matches && product.getTags().stream().anyMatch(tagIds::contains);
                    }
                    if (minPrice != null) {
                        matches = matches && product.getPrice() >= minPrice.doubleValue();
                    }
                    if (maxPrice != null) {
                        matches = matches && product.getPrice() <= maxPrice.doubleValue();
                    }
                    return matches;
                }).collect(Collectors.toList());

        return products;
    }

    public List<Product> getProductsWithHighestDiscount(int limit) {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .filter(product -> product.getDiscountPrice() > 0)
                .sorted(Comparator.comparingDouble(product -> -(product.getPrice() - product.getDiscountPrice())))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public Optional<Product> getProductById(String id) {
        return productRepository.findById(id);
    }

    public List<Product> searchProducts(String searchTerm) {
        return productRepository.findByProductNameContainingIgnoreCase(searchTerm);
    }

    public Product findProductById(String productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        return productOptional.orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
    }

    public void deleteProductImagesAndProduct(String productId) {
        Product product = findProductById(productId);

        if (product.getMainImagePublicId() != null) {
            cloudinaryService.deleteImages(List.of(product.getMainImagePublicId()));
        }

        if (product.getThumbnailsPublicId() != null && !product.getThumbnailsPublicId().isEmpty()) {
            List<String> thumbnailPublicIds = Arrays.asList(product.getThumbnailsPublicId().split(","));
            cloudinaryService.deleteImages(thumbnailPublicIds);
        }

        ObjectId productObjectId = new ObjectId(productId);
        List<PriceHistory> priceHistories = priceHistoryService.getPriceHistoriesByProduct(productObjectId);
        if (!priceHistories.isEmpty()) {
            priceHistoryRepository.deleteAll(priceHistories);
        }

        deleteProductById(productId);
    }


    public void deleteProductById(String productId) {
        productRepository.deleteById(productId);
    }

    public Product createProduct(Product product, MultipartFile mainImageFile, List<MultipartFile> thumbnailFiles) throws IOException {
        Map<String, Object> uploadResults = cloudinaryService.uploadImageForProduct(mainImageFile, thumbnailFiles);

        product.setMainImage((String) uploadResults.get("mainImageUrl"));
        product.setMainImagePublicId((String) uploadResults.get("mainImagePublicId"));
        product.setThumbnails((List<String>) uploadResults.get("thumbnailUrls"));
        product.setThumbnailsPublicId(String.join(",", (List<String>) uploadResults.get("thumbnailPublicIds")));

        product.setCreatedAt(new Date());
        product.setUpdatedAt(new Date());

        Product createdProduct = productRepository.save(product);

        double price = createdProduct.getPrice() != 0 ? createdProduct.getPrice() : 0.0;
        double discountPrice = createdProduct.getDiscountPrice() != 0 ? createdProduct.getDiscountPrice() : 0.0;

        priceHistoryService.savePriceHistory(
                new ObjectId(createdProduct.getId()),
                price,
                price,
                discountPrice,
                discountPrice
        );
        return createdProduct;
    }



    public Product updateProduct(String id, Product updatedProduct) {
        Optional<Product> existingProductOptional = productRepository.findById(id);

        if (existingProductOptional.isPresent()) {
            Product existingProduct = existingProductOptional.get();

            boolean isPriceChanged = existingProduct.getPrice() != updatedProduct.getPrice();
            boolean isDiscountPriceChanged = existingProduct.getDiscountPrice() != updatedProduct.getDiscountPrice();

            if (isPriceChanged || isDiscountPriceChanged) {
                priceHistoryService.savePriceHistory(
                        new ObjectId(existingProduct.getId()),
                        existingProduct.getPrice(),
                        updatedProduct.getPrice(),
                        existingProduct.getDiscountPrice(),
                        updatedProduct.getDiscountPrice()
                );
            }

            updatedProduct.setCreatedAt(existingProduct.getCreatedAt());
            updatedProduct.setId(id);
            updatedProduct.setUpdatedAt(new Date());

            return productRepository.save(updatedProduct);
        } else {
            throw new RuntimeException("Không tìm thấy sản phẩm có id: " + id);
        }
    }


}
