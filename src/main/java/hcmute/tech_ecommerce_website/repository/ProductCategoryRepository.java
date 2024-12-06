package hcmute.tech_ecommerce_website.repository;

import hcmute.tech_ecommerce_website.model.ProductCategory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ProductCategoryRepository extends MongoRepository<ProductCategory, String> {
    public List<ProductCategory> findByCateNameContainingIgnoreCase(String cateName);
    List<ProductCategory> findByIsDeletedFalse(Sort sort);
    Optional<ProductCategory> findByIdAndIsDeletedFalse(String id);
    boolean existsById(String id);

}
