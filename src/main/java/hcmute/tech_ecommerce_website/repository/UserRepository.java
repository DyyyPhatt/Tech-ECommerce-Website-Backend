package hcmute.tech_ecommerce_website.repository;

import hcmute.tech_ecommerce_website.model.Employee;
import hcmute.tech_ecommerce_website.model.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
    Optional<User> findById(String id);
    User findByVerificationToken(String verificationToken);
    User findByEmail(String email);
    List<User> findByIsDeletedFalse(Sort sort);
    Optional<User> findByIdAndIsDeletedFalse(String id);
}
