package hcmute.tech_ecommerce_website.repository;

import hcmute.tech_ecommerce_website.model.Brand;
import hcmute.tech_ecommerce_website.model.Product;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByIsDeletedFalse(Sort sort);
    @Query("{ 'brand': ?0, 'category': ?1, 'condition': ?2, 'tags': { $in: ?3 }, 'price': { $gte: ?4, $lte: ?5 } }")
    List<Product> findFilteredProducts(ObjectId brand, ObjectId category, ObjectId condition, List<ObjectId> tags, BigDecimal minPrice, BigDecimal maxPrice);
    List<Product> findByProductNameContainingIgnoreCase(String productName);
    List<Product> findByBrand(ObjectId brand);
    List<Product> findByCategory(ObjectId category);
    List<Product> findByTags(ObjectId tags);
    List<Product> findByCondition(ObjectId condition);
    List<Product> findByIsDeletedFalse();
    Optional<Product> findByIdAndIsDeletedFalse(String id);
}
