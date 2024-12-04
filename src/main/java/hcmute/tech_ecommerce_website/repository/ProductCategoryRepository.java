package hcmute.tech_ecommerce_website.repository;


import hcmute.tech_ecommerce_website.model.Brand;
import hcmute.tech_ecommerce_website.model.ProductCategory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ProductCategoryRepository extends MongoRepository<ProductCategory, String> {
    public List<ProductCategory> findByCateNameContainingIgnoreCase(String cateName);

    boolean existsById(String id);

}
