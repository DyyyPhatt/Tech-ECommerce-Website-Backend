package hcmute.tech_ecommerce_website.repository;

import hcmute.tech_ecommerce_website.model.Cart;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends MongoRepository<Cart, String> {
    Optional<Cart> findByUser(ObjectId userId);
    void deleteByUser(ObjectId userId);
}