package hcmute.tech_ecommerce_website.controller;

import hcmute.tech_ecommerce_website.model.Brand;
import hcmute.tech_ecommerce_website.model.Coupon;
import hcmute.tech_ecommerce_website.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {
    @Autowired
    private CouponService couponService;

    @GetMapping("/all")
    public ResponseEntity<List<Coupon>> getAllCoupons() {
        return ResponseEntity.ok(couponService.getAllCoupons());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Coupon> getCouponById(@PathVariable String id) {
        Optional<Coupon> coupon = couponService.getCouponById(id);
        if (coupon.isPresent()) {
            return ResponseEntity.ok(coupon.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/active")
    public ResponseEntity<List<Coupon>> getActiveCoupons() {
        List<Coupon> activeCoupons = couponService.getActiveCoupons();
        return ResponseEntity.ok(activeCoupons);
    }

    @PostMapping("/add")
    public ResponseEntity<Coupon> createCoupon(@RequestBody Coupon coupon) {
        System.out.println("Đã nhận được dữ liệu phiếu giảm giá: " + coupon);

        Coupon createdCoupon = couponService.createCoupon(coupon);
        return ResponseEntity.ok(createdCoupon);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateCoupon(@PathVariable String id, @RequestBody Coupon updatedCoupon) {
        try {
            Coupon updated = couponService.updateCoupon(id, updatedCoupon);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCoupon(@PathVariable String id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/apply")
    public ResponseEntity<?> applyCoupon(@RequestParam String code, @RequestParam double orderAmount) {
        try {
            Coupon appliedCoupon = couponService.applyCoupon(code, orderAmount);
            return ResponseEntity.ok(appliedCoupon);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Coupon>> searchCoupons(@RequestParam("q") String searchTerm) {
        List<Coupon> coupons = couponService.searchCoupons(searchTerm);
        return ResponseEntity.ok(coupons);
    }
}