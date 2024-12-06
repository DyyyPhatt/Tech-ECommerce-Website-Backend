package hcmute.tech_ecommerce_website.repository;

import hcmute.tech_ecommerce_website.model.Contact;
import hcmute.tech_ecommerce_website.model.Coupon;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ContactRepository extends MongoRepository<Contact, String> {
    List<Contact> findAll(Sort sort);
}

