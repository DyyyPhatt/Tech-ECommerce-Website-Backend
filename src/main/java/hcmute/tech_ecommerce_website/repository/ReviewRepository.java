package hcmute.tech_ecommerce_website.repository;

import hcmute.tech_ecommerce_website.model.Review;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReviewRepository extends MongoRepository<Review, ObjectId> {
    List<Review> findByUser(ObjectId userId);
    List<Review> findByProduct(ObjectId productId);
}
