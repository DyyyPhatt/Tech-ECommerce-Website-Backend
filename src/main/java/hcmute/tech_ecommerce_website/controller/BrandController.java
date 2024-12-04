package hcmute.tech_ecommerce_website.controller;

import hcmute.tech_ecommerce_website.model.Brand;
import hcmute.tech_ecommerce_website.model.ProductCategory;
import hcmute.tech_ecommerce_website.model.ProductCondition;
import hcmute.tech_ecommerce_website.service.BrandService;
import hcmute.tech_ecommerce_website.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
public class BrandController {

    @Autowired
    private BrandService brandService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @GetMapping("/all")
    public List<Brand> getAllBrands() {
        return brandService.getAllBrands();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Brand> getBrandById(@PathVariable String id) {
        try {
            Brand brand = brandService.getBrandById(id);
            return ResponseEntity.ok(brand);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/add")
    public ResponseEntity<Brand> addBrand(
            @ModelAttribute Brand newBrand,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        if (brandService.isBrandNameExists(newBrand.getBrandName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }

        try {
            Brand createdBrand = brandService.addBrand(newBrand, image);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBrand);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateBrand(
            @PathVariable String id,
            @ModelAttribute Brand updatedBrand,
            @RequestParam(value = "image", required = false) MultipartFile newImage) {

        try {
            Brand brand = brandService.updateBrand(id, updatedBrand, newImage);
            return ResponseEntity.ok(brand);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Thương hiệu có id: " + id + " không tìm thấy.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi cập nhật dữ liệu thương hiệu: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteBrand(@PathVariable String id,
                                         @RequestParam(value = "forceDelete", defaultValue = "false") boolean forceDelete) {
        try {
            brandService.deleteBrandWithConfirmation(id, forceDelete);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("sản phẩm liên quan")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi xóa thương hiệu: " + e.getMessage());
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile image,
                                         @RequestParam("brandId") String brandId) {
        try {
            Brand brand = brandService.getBrandById(brandId);
            String imageUrl = cloudinaryService.uploadImageForBrand(image, brand);
            return ResponseEntity.ok(new ImageResponse(imageUrl));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi tải hình ảnh lên");
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

    @GetMapping("/exists")
    public ResponseEntity<Boolean> checkBrandExists(@RequestParam String name) {
        boolean exists = brandService.isBrandNameExists(name);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Brand>> searchBrands(@RequestParam("q") String searchTerm) {
        List<Brand> brands = brandService.searchBrands(searchTerm);
        return ResponseEntity.ok(brands);
    }
}