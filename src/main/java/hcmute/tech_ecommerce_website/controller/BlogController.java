package hcmute.tech_ecommerce_website.controller;

import hcmute.tech_ecommerce_website.model.About;
import hcmute.tech_ecommerce_website.model.Blog;
import hcmute.tech_ecommerce_website.model.Brand;
import hcmute.tech_ecommerce_website.service.BlogService;
import hcmute.tech_ecommerce_website.service.CloudinaryService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/blogs")
public class BlogController {
    @Autowired
    private BlogService blogService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @GetMapping("/all")
    public ResponseEntity<List<Blog>> getAllBlogs() {
        List<Blog> blogs = blogService.getAllBlogs();
        return ResponseEntity.ok(blogs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Blog> getBlogById(@PathVariable String id) {
        return blogService.getBlogById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Blog>> searchBlogs(@RequestParam String title) {
        List<Blog> blogs = blogService.searchBlogsByTitle(title);
        return ResponseEntity.ok(blogs);
    }

    @GetMapping("/sorted")
    public ResponseEntity<List<Blog>> getBlogsSorted(
            @RequestParam(defaultValue = "latest") String sortOrder,
            @RequestParam(required = false) List<String> categories) {

        List<Blog> blogs = blogService.getAllBlogsSorted(sortOrder, categories);
        return ResponseEntity.ok(blogs);
    }

    @PostMapping("/add")
    public ResponseEntity<Blog> addBlog(
            @ModelAttribute Blog newBlog,
            @RequestParam(value = "images", required = false) List<MultipartFile> images) {

        if (newBlog.getAuthor() == null || !ObjectId.isValid(newBlog.getAuthor().toHexString()) ||
                newBlog.getCategory() == null || !ObjectId.isValid(newBlog.getCategory().toHexString())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            Blog createdBlog = blogService.createBlog(newBlog, images);
            System.out.println("Tạo blog: " + createdBlog);  // Logging blog sau khi tạo
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBlog);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateBlog(@PathVariable String id, @RequestBody Blog updatedBlog) {
        try {
            Blog updated = blogService.updateBlog(id, updatedBlog);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy Blog với ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi khi cập nhật Blog");
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile image,
                                         @RequestParam("blogId") String blogId) {
        try {
            Optional<Blog> optionalBlog = blogService.getBlogById(blogId);  // Assuming this returns Optional<Blog>

            if (!optionalBlog.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Blog không tìm thấy");
            }

            Blog blog = optionalBlog.get();

            List<String> imageUrls = cloudinaryService.uploadImagesForBlog(Collections.singletonList(image), blog);

            if (imageUrls.isEmpty()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Tải hình ảnh lên thất bại");
            }

            return ResponseEntity.ok(new ImageResponse(imageUrls.get(0)));  // Return the first image URL

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi tải hình ảnh lên");
        }
    }

    public static class ImageResponse {
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

    @PostMapping("/{id}/comments")
    public ResponseEntity<?> addCommentToBlog(@PathVariable String id, @RequestBody Blog.Comment comment) {
        try {
            Blog updatedBlog = blogService.addCommentToBlog(id, comment);
            return ResponseEntity.ok(updatedBlog);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Blog with id: " + id + " not found.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding comment: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteBlog(@PathVariable String id) {
        try {
            blogService.deleteBlog(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy Blog: " + e.getMessage());
        }
    }
}