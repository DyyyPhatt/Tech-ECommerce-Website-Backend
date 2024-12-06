package hcmute.tech_ecommerce_website.repository;

import hcmute.tech_ecommerce_website.model.Brand;
import hcmute.tech_ecommerce_website.model.ProductCondition;
import hcmute.tech_ecommerce_website.model.Tag;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductConditionRepository extends MongoRepository<ProductCondition, String> {
    List<ProductCondition> findByConditionNameContainingIgnoreCase(String searchTerm);

    List<ProductCondition> findByIsDeletedFalse(Sort sort);
    Optional<ProductCondition> findByIdAndIsDeletedFalse(String id);
}
