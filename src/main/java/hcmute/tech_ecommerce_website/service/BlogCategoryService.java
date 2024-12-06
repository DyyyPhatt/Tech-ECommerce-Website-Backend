package hcmute.tech_ecommerce_website.service;

import hcmute.tech_ecommerce_website.model.Blog;
import hcmute.tech_ecommerce_website.model.BlogCategory;
import hcmute.tech_ecommerce_website.repository.BlogCategoryRepository;
import hcmute.tech_ecommerce_website.repository.BlogRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class BlogCategoryService {
    @Autowired
    private BlogCategoryRepository blogCategoryRepository;

    @Autowired
    private BlogRepository blogRepository;

    public List<BlogCategory> getAllCategories() {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        return blogCategoryRepository.findByIsDeletedFalse(sort);
    }

    public Optional<BlogCategory> getCategoryById(String id) {
        return blogCategoryRepository.findByIdAndIsDeletedFalse(id);
    }

    public BlogCategory addCategory(BlogCategory newCategory) {
        newCategory.setCreatedAt(new Date());
        newCategory.setUpdatedAt(new Date());
        return blogCategoryRepository.save(newCategory);
    }

    public BlogCategory updateCategory(String id, BlogCategory updatedCategory) {
        Optional<BlogCategory> existingCategory = blogCategoryRepository.findById(id);
        if (existingCategory.isPresent()) {
            if (blogCategoryRepository.existsByCateBlogNameAndIdNot(updatedCategory.getCateBlogName(), id)) {
                throw new RuntimeException("Tên danh mục đã tồn tại");
            }

            BlogCategory category = existingCategory.get();
            category.setCateBlogName(updatedCategory.getCateBlogName());
            category.setCateBlogDesc(updatedCategory.getCateBlogDesc());
            category.setUpdatedAt(new Date());
            return blogCategoryRepository.save(category);
        } else {
            throw new RuntimeException("Không tìm thấy danh mục");
        }
    }

    public void deleteCategory(String id) {
        BlogCategory category = blogCategoryRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Danh mục có id: " + id + " không tìm thấy"));

        category.setDeleted(true);
        blogCategoryRepository.save(category);
    }
}