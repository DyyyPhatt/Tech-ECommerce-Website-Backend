package hcmute.tech_ecommerce_website.repository;

import hcmute.tech_ecommerce_website.model.Blog;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface BlogRepository extends MongoRepository<Blog, String> {
    List<Blog> findByCategoryInAndIsDeletedFalse(List<ObjectId> categories);
    List<Blog> findByTitleContainingIgnoreCaseAndIsDeletedFalse(String title);
    List<Blog> findByAuthor(ObjectId author);
    List<Blog> findByIsDeletedFalse(Sort sort);
    Optional<Blog> findByIdAndIsDeletedFalse(String id);
    boolean existsById(String id);
}