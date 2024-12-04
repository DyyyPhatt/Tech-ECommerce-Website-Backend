package hcmute.tech_ecommerce_website.service;

import hcmute.tech_ecommerce_website.model.Brand;
import hcmute.tech_ecommerce_website.model.Product;
import hcmute.tech_ecommerce_website.model.ProductCategory;
import hcmute.tech_ecommerce_website.repository.ProductCategoryRepository;
import hcmute.tech_ecommerce_website.repository.ProductRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ProductCategoryService {
    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CloudinaryService cloudinaryService;


    public List<ProductCategory> getAllCategories() {
        return productCategoryRepository.findAll();
    }

    public ProductCategory getCategoryById(String id) {
        return productCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Danh mục có id: " + id + " không tìm thấy"));
    }


    public ProductCategory addCategory(ProductCategory newCategory, MultipartFile cateImage) {
        newCategory.setCreatedAt(new Date());
        newCategory.setUpdatedAt(new Date());

        if (cateImage != null && !cateImage.isEmpty()) {
            try {
                String imageUrl = cloudinaryService.uploadImageForProductCategory(cateImage, newCategory);
                newCategory.setCateImage(imageUrl);
            } catch (IOException e) {
                throw new RuntimeException("Lỗi tải hình ảnh lên", e);
            }
        }

        return productCategoryRepository.save(newCategory);
    }

    public ProductCategory updateCategory(String id, ProductCategory updateCategory, MultipartFile newImage) {
        ProductCategory existingCategory = productCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Thương hiệu có id: " + id + " không tìm thấy"));

        existingCategory.setCateName(updateCategory.getCateName());
        existingCategory.setCateDesc(updateCategory.getCateDesc());

        if (newImage != null && !newImage.isEmpty()) {
            try {
                if (existingCategory.getProductCategoryImagePublicId() != null) {
                    cloudinaryService.deleteImage(existingCategory.getProductCategoryImagePublicId());
                }

                String imageUrl = cloudinaryService.uploadImageForProductCategory(newImage, existingCategory);
                existingCategory.setCateImage(imageUrl);

                String publicId = existingCategory.getProductCategoryImagePublicId();
                existingCategory.setProductCategoryImagePublicId(publicId);
            } catch (IOException e) {
                throw new RuntimeException("Lỗi tải lên hoặc cập nhật hình ảnh: " + e.getMessage(), e);
            }
        }

        existingCategory.setUpdatedAt(new Date());
        return productCategoryRepository.save(existingCategory);
    }

    public String checkProductsBeforeDeletingCategory(String categoryId) {
        if (!ObjectId.isValid(categoryId)) {
            throw new IllegalArgumentException("Định dạng ID danh mục không hợp lệ.");
        }

        if (!productCategoryRepository.existsById(categoryId)) {
            throw new IllegalArgumentException("Danh mục có id: " + categoryId + " không tìm thấy.");
        }

        ObjectId categoryObjectId = new ObjectId(categoryId);
        List<Product> relatedProducts = productRepository.findByCategory(categoryObjectId);
        if (!relatedProducts.isEmpty()) {
            return "Có sản phẩm liên quan đến thương hiệu. Bạn có chắc muốn xóa thương hiệu này không?";
        }

        return null;
    }

    public void deleteCategoryWithConfirmation(String categoryId, boolean forceDelete) {
        if (!ObjectId.isValid(categoryId)) {
            throw new IllegalArgumentException("Định dạng ID danh mục không hợp lệ.");
        }

        String confirmationMessage = checkProductsBeforeDeletingCategory(categoryId);
        if (confirmationMessage != null && !forceDelete) {
            throw new IllegalArgumentException(confirmationMessage);
        }

        ProductCategory categoryToDelete = productCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Danh mục có id: " + categoryId + " không tìm thấy."));

        if (categoryToDelete.getProductCategoryImagePublicId() != null) {
            try {
                cloudinaryService.deleteImage(categoryToDelete.getProductCategoryImagePublicId());
            } catch (IOException e) {
                throw new RuntimeException("Lỗi xóa hình ảnh khỏi Cloudinary: " + e.getMessage());
            }
        }

        ObjectId categoryObjectId = new ObjectId(categoryId);
        List<Product> productsToDelete = productRepository.findByCategory(categoryObjectId);
        if (!productsToDelete.isEmpty()) {
            productRepository.deleteAll(productsToDelete);
        }

        productCategoryRepository.deleteById(categoryId);
    }

    public boolean isCategoryExists(String name) {
        return productCategoryRepository.findAll().stream()
                .anyMatch(category -> category.getCateName().equalsIgnoreCase(name));
    }

    public boolean isCategoryNameDuplicate(String id, String name) {
        return productCategoryRepository.findAll().stream()
                .filter(category -> !category.getId().equals(id))
                .anyMatch(category -> category.getCateName().equalsIgnoreCase(name));
    }

    public List<ProductCategory> searchCategories(String searchTerm) {
        return productCategoryRepository.findByCateNameContainingIgnoreCase(searchTerm);
    }

}
