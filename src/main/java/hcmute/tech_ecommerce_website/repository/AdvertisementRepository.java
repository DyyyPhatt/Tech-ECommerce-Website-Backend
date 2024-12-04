package hcmute.tech_ecommerce_website.repository;

import hcmute.tech_ecommerce_website.model.Advertisement;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AdvertisementRepository extends MongoRepository<Advertisement, String> {
}

