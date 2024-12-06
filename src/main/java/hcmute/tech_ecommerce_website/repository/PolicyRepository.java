package hcmute.tech_ecommerce_website.repository;

import hcmute.tech_ecommerce_website.model.Contact;
import hcmute.tech_ecommerce_website.model.Policy;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PolicyRepository extends MongoRepository<Policy, String> {
    Policy findByTitle(String title);
    List<Policy> findAll(Sort sort);

}
