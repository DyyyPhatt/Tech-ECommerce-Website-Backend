package hcmute.tech_ecommerce_website.controller;

import hcmute.tech_ecommerce_website.model.Brand;
import hcmute.tech_ecommerce_website.model.ProductCategory;
import hcmute.tech_ecommerce_website.service.CloudinaryService;
import hcmute.tech_ecommerce_website.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
public class ProductCategoryController {
    @Autowired
    private ProductCategoryService productCategoryService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @GetMapping("/all")
    public List<ProductCategory> getAllCategories() {
        return productCategoryService.getAllCategories();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductCategory> getCategoryById(@PathVariable String id) {
        try {
            ProductCategory category = productCategoryService.getCategoryById(id);
            return ResponseEntity.ok(category);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/add")
    public ResponseEntity<ProductCategory> addCategory(
            @ModelAttribute ProductCategory newCategory,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        if (productCategoryService.isCategoryExists(newCategory.getCateName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }

        try {
            ProductCategory createdCategory = productCategoryService.addCategory(newCategory, image);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateCategory(
            @PathVariable String id,
            @ModelAttribute ProductCategory updatedCategory,
            @RequestParam(value = "image", required = false) MultipartFile newImage) {

        try {
            ProductCategory category = productCategoryService.updateCategory(id, updatedCategory, newImage);
            return ResponseEntity.ok(category);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Brand with id: " + id + " not found.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating brand data: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable String id,
                                            @RequestParam(value = "forceDelete", defaultValue = "false") boolean forceDelete) {
        try {
            productCategoryService.deleteCategoryWithConfirmation(id, forceDelete);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("sản phẩm liên quan")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting category: " + e.getMessage());
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile image,
                                         @RequestParam("categoryId") String categoryId) {
        try {
            ProductCategory category = productCategoryService.getCategoryById(categoryId);
            String imageUrl = cloudinaryService.uploadImageForProductCategory(image, category);  // Truyền cả image và brand vào
            return ResponseEntity.ok(new ImageResponse(imageUrl));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading image");
        }
    }


    class ImageResponse {
        private String secure_url;
        public ImageResponse(String secure_url) {
            this.secure_url = secure_url;
        }
        public String getSecure_url() {
            return secure_url;
        }
        public void setSecure_url(String secure_url) {
            this.secure_url = secure_url;
        }
    }

    @GetMapping
    public ResponseEntity<List<ProductCategory>> checkCategoryName(@RequestParam String name) {
        List<ProductCategory> categories = productCategoryService.getAllCategories();
        List<ProductCategory> existingCategories = categories.stream()
                .filter(category -> category.getCateName().equalsIgnoreCase(name))
                .collect(Collectors.toList());
        return ResponseEntity.ok(existingCategories);
    }
    @GetMapping("/search")
    public ResponseEntity<List<ProductCategory>> searchCategories(@RequestParam("q") String searchTerm) {
        List<ProductCategory> categories = productCategoryService.searchCategories(searchTerm);
        return ResponseEntity.ok(categories);
    }
}
