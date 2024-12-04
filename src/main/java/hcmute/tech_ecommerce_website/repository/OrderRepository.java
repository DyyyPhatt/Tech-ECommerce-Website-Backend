package hcmute.tech_ecommerce_website.repository;

import hcmute.tech_ecommerce_website.model.Order;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OrderRepository extends MongoRepository<Order, String> {
    List<Order> findByUser(ObjectId userId);
}
