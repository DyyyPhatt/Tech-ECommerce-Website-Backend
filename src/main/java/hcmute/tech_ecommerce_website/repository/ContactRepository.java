package hcmute.tech_ecommerce_website.repository;

import hcmute.tech_ecommerce_website.model.Contact;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ContactRepository extends MongoRepository<Contact, String> {
}

