package hcmute.tech_ecommerce_website.repository;

import hcmute.tech_ecommerce_website.model.Brand;
import hcmute.tech_ecommerce_website.model.Employee;
import hcmute.tech_ecommerce_website.model.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends MongoRepository<Employee, String> {
    Optional<Employee> findByUsername(String username);
    boolean existsByUsername(String username);
    Employee findByEmail(String email);
    Optional<Employee> findByResetToken(String resetToken);
    Employee findByVerificationToken(String verificationToken);
    List<Employee> findByIsDeletedFalse(Sort sort);
    Optional<Employee> findByIdAndIsDeletedFalse(String id);
}