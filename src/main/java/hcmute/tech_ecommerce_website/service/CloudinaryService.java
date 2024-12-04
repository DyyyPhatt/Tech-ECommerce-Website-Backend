package hcmute.tech_ecommerce_website.service;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import hcmute.tech_ecommerce_website.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.*;


@Service
public class CloudinaryService {


    private final Cloudinary cloudinary;


    public CloudinaryService(@Value("${cloudinary.api.key}") String apiKey,
                             @Value("${cloudinary.api.secret}") String apiSecret,
                             @Value("${cloudinary.cloud.name}") String cloudName) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret));
    }


    public String uploadImageForBrand(MultipartFile file, Brand brand) throws IOException {
        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type", "auto"));

        String imageUrl = (String) uploadResult.get("secure_url");
        String publicId = (String) uploadResult.get("public_id");

        brand.setBrandImage(imageUrl);
        brand.setBrandImagePublicId(publicId);


        return imageUrl;
    }

    public String uploadImageForProductCategory(MultipartFile file, ProductCategory productCategory) throws IOException {
        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type", "auto"));

        String imageUrl = (String) uploadResult.get("secure_url");
        String publicId = (String) uploadResult.get("public_id");

        productCategory.setCateImage(imageUrl);
        productCategory.setProductCategoryImagePublicId(publicId);

        return imageUrl;
    }


    public List<String> uploadImagesForBlog(List<MultipartFile> images, Blog blog) throws IOException {
        List<String> imageUrls = new ArrayList<>();

        for (MultipartFile image : images) {
            if (image == null || image.isEmpty()) {
                throw new IllegalArgumentException("Hình ảnh không thể rỗng hoặc trống.");
            }
            Map<String, Object> uploadResult = cloudinary.uploader().upload(image.getBytes(),
                    ObjectUtils.asMap("resource_type", "auto"));

            String imageUrl = (String) uploadResult.get("secure_url");
            imageUrls.add(imageUrl);
        }

        return imageUrls;
    }

    public String uploadImageForUser(MultipartFile file, User user) throws IOException {
        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type", "auto"));

        String imageUrl = (String) uploadResult.get("secure_url");
        String publicId = (String) uploadResult.get("public_id");

        user.setAvatar(imageUrl);
        user.setUserImagePublicId(publicId);


        return imageUrl;
    }

    public Map<String, Object> uploadImageForProduct(MultipartFile mainImageFile,
                                                     List<MultipartFile> thumbnailFiles) throws IOException {Map<String, Object> uploadResults = new HashMap<>();

        if (mainImageFile != null && !mainImageFile.isEmpty()) {
            Map<String, Object> mainImageUploadResult = cloudinary.uploader().upload(mainImageFile.getBytes(),
                    ObjectUtils.asMap("resource_type", "auto"));

            String mainImageUrl = (String) mainImageUploadResult.get("secure_url");
            String mainImagePublicId = (String) mainImageUploadResult.get("public_id");

            uploadResults.put("mainImageUrl", mainImageUrl);
            uploadResults.put("mainImagePublicId", mainImagePublicId);
        }
        if (thumbnailFiles != null && !thumbnailFiles.isEmpty()) {
            List<String> thumbnailUrls = new ArrayList<>();
            List<String> thumbnailPublicIds = new ArrayList<>();

            for (MultipartFile thumbnailFile : thumbnailFiles) {
                if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
                    try {
                        Map<String, Object> thumbnailUploadResult = cloudinary.uploader().upload(thumbnailFile.getBytes(),
                                ObjectUtils.asMap("resource_type", "auto"));

                        String thumbnailUrl = (String) thumbnailUploadResult.get("secure_url");
                        String thumbnailPublicId = (String) thumbnailUploadResult.get("public_id");

                        thumbnailUrls.add(thumbnailUrl);
                        thumbnailPublicIds.add(thumbnailPublicId);
                    } catch (IOException e) {
                        throw new RuntimeException("Lỗi tải lên hình thu nhỏ: " + e.getMessage(), e);
                    }
                }
            }

            uploadResults.put("thumbnailUrls", thumbnailUrls);
            uploadResults.put("thumbnailPublicIds", thumbnailPublicIds);
        }

        return uploadResults;
    }

    public void deleteImages(List<String> publicIds) {
        if (publicIds != null && !publicIds.isEmpty()) {
            for (String publicId : publicIds) {
                try {
                    cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                } catch (IOException e) {
                    throw new RuntimeException("Lỗi xóa ảnh public_id: " + publicId + ", " + e.getMessage(), e);
                }
            }
        }
    }


    public Map<String, List<String>> deleteImagesBlog(List<String> publicIds) {
        List<String> successfullyDeleted = new ArrayList<>();
        List<String> failedToDelete = new ArrayList<>();

        for (String publicId : publicIds) {
            try {
                Map<String, Object> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                String deletionResult = (String) result.get("result");

                if ("ok".equals(deletionResult)) {
                    successfullyDeleted.add(publicId);
                    System.out.println("Đã xóa hình ảnh thành công: " + publicId);
                } else {
                    failedToDelete.add(publicId);
                    System.out.println("Không thể xóa hình ảnh: " + publicId + ". Lý do: " + deletionResult);
                }
            } catch (IOException e) {
                failedToDelete.add(publicId);
                System.err.println("Lỗi xóa hình ảnh với publicId: " + publicId + ". Ngoại lệ: " + e.getMessage());
            }
        }
        Map<String, List<String>> resultSummary = new HashMap<>();
        resultSummary.put("deleted", successfullyDeleted);
        resultSummary.put("failed", failedToDelete);

        return resultSummary;
    }

    public void deleteImage(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }
}

