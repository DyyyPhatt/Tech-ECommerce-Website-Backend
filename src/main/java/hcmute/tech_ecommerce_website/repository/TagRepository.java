package hcmute.tech_ecommerce_website.repository;

import hcmute.tech_ecommerce_website.model.Brand;
import hcmute.tech_ecommerce_website.model.ProductCategory;
import hcmute.tech_ecommerce_website.model.Tag;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends MongoRepository<Tag, String> {
    List<Tag> findByNameContainingIgnoreCase(String name);
    List<Tag> findByIsDeletedFalse(Sort sort);
    Optional<Tag> findByIdAndIsDeletedFalse(String id);
}
