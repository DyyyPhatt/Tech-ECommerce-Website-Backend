package hcmute.tech_ecommerce_website.controller;

import hcmute.tech_ecommerce_website.model.BlogCategory;
import hcmute.tech_ecommerce_website.service.BlogCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/blogcategories")
public class BlogCategoryController {
    @Autowired
    private BlogCategoryService blogCategoryService;

    @GetMapping("/all")
    public ResponseEntity<List<BlogCategory>> getAllCategories() {
        List<BlogCategory> categories = blogCategoryService.getAllCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BlogCategory> getCategoryById(@PathVariable String id) {
        Optional<BlogCategory> category = blogCategoryService.getCategoryById(id);
        return category.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/add")
    public ResponseEntity<BlogCategory> addCategory(@RequestBody BlogCategory newCategory) {
        BlogCategory category = blogCategoryService.addCategory(newCategory);
        return new ResponseEntity<>(category, HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<BlogCategory> updateCategory(@PathVariable String id, @RequestBody BlogCategory updatedCategory) {
        BlogCategory category = blogCategoryService.updateCategory(id, updatedCategory);
        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable String id) {
        try {
            blogCategoryService.deleteCategory(id);
            return new ResponseEntity<>("Danh mục đã được xóa mềm thành công.", HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Đã xảy ra lỗi không mong muốn", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
