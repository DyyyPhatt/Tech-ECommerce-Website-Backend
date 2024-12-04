package hcmute.tech_ecommerce_website.service;

import hcmute.tech_ecommerce_website.model.Blog;
import hcmute.tech_ecommerce_website.model.BlogCategory;
import hcmute.tech_ecommerce_website.repository.BlogCategoryRepository;
import hcmute.tech_ecommerce_website.repository.BlogRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
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
        return blogCategoryRepository.findAll();
    }

    public Optional<BlogCategory> getCategoryById(String id) {
        return blogCategoryRepository.findById(id);
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
        if (!blogCategoryRepository.existsById(id)) {
            throw new IllegalArgumentException("Danh mục có id: " + id + " không tìm thấy");
        }

        List<Blog> blogsToDelete = blogRepository.findByCategoryIn(Collections.singletonList(new ObjectId(id)));
        if (!blogsToDelete.isEmpty()) {
            blogRepository.deleteAll(blogsToDelete);
        }

        blogCategoryRepository.deleteById(id);
    }
}
