package hcmute.tech_ecommerce_website.repository;

import hcmute.tech_ecommerce_website.model.Brand;
import hcmute.tech_ecommerce_website.model.Coupon;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CouponRepository extends MongoRepository<Coupon, String> {
    Optional<Coupon> findByCode(String code);
    List<Coupon> findByCodeContainingIgnoreCase(String code);
    List<Coupon> findAll(Sort sort);
}
