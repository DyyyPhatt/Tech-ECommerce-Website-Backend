package hcmute.tech_ecommerce_website.service;

import hcmute.tech_ecommerce_website.model.Blog;
import hcmute.tech_ecommerce_website.repository.BlogRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BlogService {
    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    public List<Blog> getAllBlogs() {
        Sort sort = Sort.by(Sort.Direction.DESC, "publishedDate");
        return blogRepository.findByIsDeletedFalse(sort);
    }

    public Optional<Blog> getBlogById(String id) {
        return blogRepository.findByIdAndIsDeletedFalse(id);
    }

    public List<Blog> searchBlogsByTitle(String title) {
        return blogRepository.findByTitleContainingIgnoreCaseAndIsDeletedFalse(title);
    }

    public List<Blog> getAllBlogsSorted(String sortOrder, List<String> categoryIds) {
        List<Blog> blogs;

        if (categoryIds != null && !categoryIds.isEmpty()) {
            List<ObjectId> objectIds = categoryIds.stream()
                    .map(ObjectId::new)
                    .collect(Collectors.toList());
            blogs = blogRepository.findByCategoryInAndIsDeletedFalse(objectIds);
        } else {
            Sort sort = Sort.by(Sort.Direction.DESC, "publishedDate");
            blogs = blogRepository.findByIsDeletedFalse(sort);
        }

        if ("oldest".equals(sortOrder)) {
            return blogs.stream()
                    .sorted(Comparator.comparing(Blog::getPublishedDate))
                    .collect(Collectors.toList());
        } else {
            return blogs.stream()
                    .sorted(Comparator.comparing(Blog::getPublishedDate).reversed())
                    .collect(Collectors.toList());
        }
    }

    public Blog createBlog(Blog blog, List<MultipartFile> images) {
        blog.setPublishedDate(new Date());
        blog.setUpdatedAt(new Date());

        if (images != null && !images.isEmpty()) {
            try {
                List<String> imageUrls = cloudinaryService.uploadImagesForBlog(images, blog);
                System.out.println("URL hình ảnh đã tải lên: " + imageUrls);

                blog.setBlogImage(imageUrls);

                List<String> publicIds = blog.getBlogImagePublicId();
                blog.setBlogImagePublicId(publicIds);

            } catch (IOException e) {
                throw new RuntimeException("Lỗi tải hình ảnh lên", e);
            }
        }

        return blogRepository.save(blog);
    }

    private String extractPublicIdFromUrl(String url) {
        String[] urlParts = url.split("/upload/");
        String[] publicIdParts = urlParts[1].split("\\.");
        return publicIdParts[0];
    }

    public Blog updateBlog(String id, Blog updatedBlog) {
        Optional<Blog> existingBlogOptional = blogRepository.findById(id);

        if (existingBlogOptional.isPresent()) {
            Blog existingBlog = existingBlogOptional.get();

            if (updatedBlog.getTitle() != null) {
                existingBlog.setTitle(updatedBlog.getTitle());
            }
            if (updatedBlog.getContent() != null) {
                existingBlog.setContent(updatedBlog.getContent());
            }
            if (updatedBlog.getAuthor() != null) {
                existingBlog.setAuthor(updatedBlog.getAuthor());
            }
            if (updatedBlog.getCategory() != null) {
                existingBlog.setCategory(updatedBlog.getCategory());
            }
            if (updatedBlog.getBlogImage() != null) {
                existingBlog.setBlogImage(updatedBlog.getBlogImage());
            }
            if (updatedBlog.getComments() != null) {
                existingBlog.setComments(updatedBlog.getComments());
            }

            existingBlog.setUpdatedAt(new Date());

            return blogRepository.save(existingBlog);
        } else {
            throw new RuntimeException("Không tìm thấy Blog với ID: " + id);
        }
    }

    private String getPublicIdFromUrl(String url) {
        String[] urlParts = url.split("/upload/");
        String[] publicIdParts = urlParts[1].split("\\.");
        return publicIdParts[0]; // Trả về publicId
    }

    public Blog addCommentToBlog(String blogId, Blog.Comment comment) {
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new IllegalArgumentException("Blog có id: " + blogId + " không tìm thấy"));

        comment.setCreatedAt(new Date());

        if (blog.getComments() == null) {
            blog.setComments(new ArrayList<>());
        }
        blog.getComments().add(comment);

        return blogRepository.save(blog);
    }

    public void deleteBlog(String id) {
        Blog blog = blogRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Blog có id: " + id + " không tìm thấy"));

        List<String> publicIds = blog.getBlogImagePublicId();
        if (publicIds != null && !publicIds.isEmpty()) {
            cloudinaryService.deleteImagesBlog(publicIds);
        } else {
            System.out.println("Không tìm thấy publicIds nào cho blog có id: " + id);
        }
        blog.setDeleted(true);
        blog.setUpdatedAt(new Date());
        blogRepository.save(blog);
    }
}
