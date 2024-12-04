package hcmute.tech_ecommerce_website.repository;

import hcmute.tech_ecommerce_website.model.ProductCondition;
import hcmute.tech_ecommerce_website.model.Tag;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductConditionRepository extends MongoRepository<ProductCondition, String> {
    List<ProductCondition> findByConditionNameContainingIgnoreCase(String searchTerm);
}
