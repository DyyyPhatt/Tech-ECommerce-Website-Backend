package hcmute.tech_ecommerce_website.service;

import com.cloudinary.utils.ObjectUtils;
import hcmute.tech_ecommerce_website.model.Brand;
import hcmute.tech_ecommerce_website.model.Product;
import hcmute.tech_ecommerce_website.repository.BrandRepository;
import hcmute.tech_ecommerce_website.repository.ProductRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
public class BrandService {

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    public Brand getBrandById(String id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Thương hiệu có id: " + id + " không tìm thấy"));
    }

    public Brand addBrand(Brand newBrand, MultipartFile brandImage) {
        newBrand.setCreatedAt(new Date());
        newBrand.setUpdatedAt(new Date());

        if (brandImage != null && !brandImage.isEmpty()) {
            try {
                String imageUrl = cloudinaryService.uploadImageForBrand(brandImage, newBrand);
                newBrand.setBrandImage(imageUrl);
                String publicId = newBrand.getBrandImagePublicId();
                newBrand.setBrandImagePublicId(publicId);
            } catch (IOException e) {
                throw new RuntimeException("Lỗi tải hình ảnh lên", e);
            }
        }

        return brandRepository.save(newBrand);
    }

    public Brand updateBrand(String id, Brand updatedBrand, MultipartFile newImage) {
        Brand existingBrand = brandRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Thương hiệu có id: " + id + " không tìm thấy"));

        existingBrand.setBrandName(updatedBrand.getBrandName());
        existingBrand.setBrandDesc(updatedBrand.getBrandDesc());

        if (newImage != null && !newImage.isEmpty()) {
            try {
                if (existingBrand.getBrandImagePublicId() != null) {
                    cloudinaryService.deleteImage(existingBrand.getBrandImagePublicId());
                }

                String imageUrl = cloudinaryService.uploadImageForBrand(newImage, existingBrand);
                existingBrand.setBrandImage(imageUrl);

                String publicId = existingBrand.getBrandImagePublicId();
                existingBrand.setBrandImagePublicId(publicId);
            } catch (IOException e) {
                throw new RuntimeException("Lỗi tải lên hoặc cập nhật hình ảnh: " + e.getMessage(), e);
            }
        }

        existingBrand.setUpdatedAt(new Date());
        return brandRepository.save(existingBrand);
    }

    public void deleteBrandWithConfirmation(String brandId, boolean forceDelete) {
        if (!ObjectId.isValid(brandId)) {
            throw new IllegalArgumentException("Định dạng ID thương hiệu không hợp lệ.");
        }

        String confirmationMessage = checkProductsBeforeDeletingBrand(brandId);
        if (confirmationMessage != null && !forceDelete) {
            throw new IllegalArgumentException(confirmationMessage);
        }

        Brand brandToDelete = brandRepository.findById(brandId)
                .orElseThrow(() -> new IllegalArgumentException("Thương hiệu có id: " + brandId + " không tìm thấy"));

        if (brandToDelete.getBrandImagePublicId() != null) {
            try {
                cloudinaryService.deleteImage(brandToDelete.getBrandImagePublicId());
            } catch (IOException e) {
                throw new RuntimeException("Lỗi xóa hình ảnh khỏi Cloudinary: " + e.getMessage());
            }
        }

        ObjectId brandObjectId = new ObjectId(brandId);
        List<Product> productsToDelete = productRepository.findByBrand(brandObjectId);
        if (!productsToDelete.isEmpty()) {
            productRepository.deleteAll(productsToDelete);
        }

        brandRepository.deleteById(brandId);
    }

    public String checkProductsBeforeDeletingBrand(String brandId) {
        if (!ObjectId.isValid(brandId)) {
            throw new IllegalArgumentException("Định dạng ID thương hiệu không hợp lệ.");
        }

        if (!brandRepository.existsById(brandId)) {
            throw new IllegalArgumentException("Thương hiệu có id: " + brandId + " không tìm thấy.");
        }

        ObjectId brandObjectId = new ObjectId(brandId);
        List<Product> relatedProducts = productRepository.findByBrand(brandObjectId);
        if (!relatedProducts.isEmpty()) {
            return "Có sản phẩm liên quan đến thương hiệu. Bạn có chắc muốn xóa thương hiệu này không?";
        }

        return null;
    }

    public boolean isBrandNameExists(String brandName) {
        return brandRepository.findByBrandName(brandName).isPresent();
    }

    public List<Brand> searchBrands(String searchTerm) {
        return brandRepository.findByBrandNameContainingIgnoreCase(searchTerm);
    }
}
