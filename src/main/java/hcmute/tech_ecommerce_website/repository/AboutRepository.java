package hcmute.tech_ecommerce_website.repository;

import hcmute.tech_ecommerce_website.model.About;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AboutRepository extends MongoRepository<About, String> {
    List<About> findByTitleContainingIgnoreCaseAndIsDeletedFalse(String title);
    List<About> findByIsDeletedFalse(Sort sort);
}
