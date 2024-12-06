package hcmute.tech_ecommerce_website.repository;

import hcmute.tech_ecommerce_website.model.Brand;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface BrandRepository extends MongoRepository<Brand, String> {
    Optional<Brand> findByBrandName(String brandName);
    List<Brand> findByBrandNameContainingIgnoreCase(String brandName);
    List<Brand> findByIsDeletedFalse(Sort sort);
    Optional<Brand> findByIdAndIsDeletedFalse(String id);
    boolean existsById(String id);
}