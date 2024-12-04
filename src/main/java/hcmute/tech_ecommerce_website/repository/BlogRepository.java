package hcmute.tech_ecommerce_website.repository;

import hcmute.tech_ecommerce_website.model.Blog;
import hcmute.tech_ecommerce_website.model.Product;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BlogRepository extends MongoRepository<Blog, String> {
    List<Blog> findByCategoryIn(List<ObjectId> categories);
    List<Blog> findAllByOrderByPublishedDateDesc();
    List<Blog> findAllByOrderByPublishedDateAsc();
    List<Blog> findByTitleContainingIgnoreCase(String title);
    List<Blog> findByAuthor(ObjectId author);

    boolean existsById(String id);

}
