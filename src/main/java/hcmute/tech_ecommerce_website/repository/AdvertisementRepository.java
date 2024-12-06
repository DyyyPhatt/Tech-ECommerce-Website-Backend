package hcmute.tech_ecommerce_website.repository;

import hcmute.tech_ecommerce_website.model.Advertisement;
import hcmute.tech_ecommerce_website.model.Brand;
import hcmute.tech_ecommerce_website.model.Contact;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface AdvertisementRepository extends MongoRepository<Advertisement, String> {
    List<Advertisement> findAll(Sort sort);

}

