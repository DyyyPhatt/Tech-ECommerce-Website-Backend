package hcmute.tech_ecommerce_website.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hcmute.tech_ecommerce_website.model.Product;
import hcmute.tech_ecommerce_website.service.ProductService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("")
    public List<Product> getProducts(
            @RequestParam(required = false, defaultValue = "newest") String sortBy,
            @RequestParam(required = false) List<String> brandIds,
            @RequestParam(required = false) List<String> categoryIds,
            @RequestParam(required = false) List<String> conditionIds,
            @RequestParam(required = false) List<String> tagIds,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {

        List<ObjectId> brandObjectIds = brandIds != null ? brandIds.stream().map(ObjectId::new).collect(Collectors.toList()) : null;
        List<ObjectId> categoryObjectIds = categoryIds != null ? categoryIds.stream().map(ObjectId::new).collect(Collectors.toList()) : null;
        List<ObjectId> conditionObjectIds = conditionIds != null ? conditionIds.stream().map(ObjectId::new).collect(Collectors.toList()) : null;
        List<ObjectId> tagObjectIds = tagIds != null ? tagIds.stream().map(ObjectId::new).collect(Collectors.toList()) : null;

        return productService.getAllProductsSorted(sortBy, brandObjectIds, categoryObjectIds, conditionObjectIds, tagObjectIds, minPrice, maxPrice);
    }

    @GetMapping("/highest-discount")
    public ResponseEntity<List<Product>> getHighestDiscountedProducts() {
        List<Product> discountedProducts = productService.getProductsWithHighestDiscount();
        return ResponseEntity.ok(discountedProducts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam("q") String searchTerm) {
        List<Product> products = productService.searchProducts(searchTerm);
        return ResponseEntity.ok(products);
    }
    
    @PostMapping("/add")
    public ResponseEntity<Product> createProduct(
            @RequestParam("product") String productJson,
            @RequestParam(value = "mainImage", required = false) MultipartFile mainImageFile,
            @RequestParam(value = "thumbnails", required = false) List<MultipartFile> thumbnailFiles) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Product product = objectMapper.readValue(productJson, Product.class);

            Product createdProduct = productService.createProduct(product, mainImageFile, thumbnailFiles);

            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable String id, @RequestBody Product product) {
        Product updatedProduct = productService.updateProduct(id, product);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        productService.deleteProductImagesAndProduct(id);
        return ResponseEntity.noContent().build();
    }
}