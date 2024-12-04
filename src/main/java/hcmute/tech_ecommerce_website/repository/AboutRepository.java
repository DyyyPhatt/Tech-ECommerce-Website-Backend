package hcmute.tech_ecommerce_website.repository;

import hcmute.tech_ecommerce_website.model.About;
import hcmute.tech_ecommerce_website.model.Brand;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AboutRepository extends MongoRepository<About, String> {
    List<About> findByTitleContainingIgnoreCase(String title);
}
