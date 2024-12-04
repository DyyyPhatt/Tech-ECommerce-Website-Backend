package hcmute.tech_ecommerce_website.repository;

import hcmute.tech_ecommerce_website.model.BlogCategory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogCategoryRepository extends MongoRepository<BlogCategory, String> {
    boolean existsByCateBlogNameAndIdNot(String cateBlogName, String id);
}
