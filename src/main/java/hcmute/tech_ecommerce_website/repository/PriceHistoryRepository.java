package hcmute.tech_ecommerce_website.repository;

import hcmute.tech_ecommerce_website.model.PriceHistory;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriceHistoryRepository extends MongoRepository<PriceHistory, String> {
    List<PriceHistory> findByProduct(ObjectId product);


}