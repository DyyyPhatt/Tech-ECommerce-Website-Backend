package hcmute.tech_ecommerce_website.repository;

import hcmute.tech_ecommerce_website.model.BlogCategory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlogCategoryRepository extends MongoRepository<BlogCategory, String> {
    boolean existsByCateBlogNameAndIdNot(String cateBlogName, String id);
    List<BlogCategory> findByIsDeletedFalse(Sort sort);
    Optional<BlogCategory> findByIdAndIsDeletedFalse(String id);
}

