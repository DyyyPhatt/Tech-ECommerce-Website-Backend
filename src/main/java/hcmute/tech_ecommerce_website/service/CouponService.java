package hcmute.tech_ecommerce_website.service;

import hcmute.tech_ecommerce_website.model.Brand;
import hcmute.tech_ecommerce_website.model.Coupon;
import hcmute.tech_ecommerce_website.repository.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CouponService {
    @Autowired
    private CouponRepository couponRepository;

    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }
    public Optional<Coupon> getCouponById(String id) {
        return couponRepository.findById(id);
    }

    public List<Coupon> getActiveCoupons() {
        List<Coupon> allCoupons = couponRepository.findAll();

        return allCoupons.stream()
                .filter(coupon -> coupon.isActive() &&
                        (coupon.getStartDate() == null || coupon.getStartDate().before(new Date())) &&
                        (coupon.getEndDate() == null || coupon.getEndDate().after(new Date())) &&
                        (coupon.getUsageLimit() == null || coupon.getUsageCount() < coupon.getUsageLimit()))
                .collect(Collectors.toList());
    }

    public Coupon createCoupon(Coupon coupon) {
        System.out.println("Tạo phiếu giảm giá với isActive: " + coupon.isActive());

        return couponRepository.save(coupon);
    }


    public Coupon updateCoupon(String id, Coupon updatedCoupon) throws Exception {
        Optional<Coupon> existingCoupon = couponRepository.findById(id);
        if (existingCoupon.isEmpty()) {
            throw new Exception("Không tìm thấy phiếu giảm giá với id: " + id);
        }

        Coupon coupon = existingCoupon.get();
        coupon.setCode(updatedCoupon.getCode());
        coupon.setDescription(updatedCoupon.getDescription());
        coupon.setDiscountType(updatedCoupon.getDiscountType());
        coupon.setDiscountValue(updatedCoupon.getDiscountValue());
        coupon.setMinimumOrderAmount(updatedCoupon.getMinimumOrderAmount());
        coupon.setMaxDiscountAmount(updatedCoupon.getMaxDiscountAmount());
        coupon.setStartDate(updatedCoupon.getStartDate());
        coupon.setEndDate(updatedCoupon.getEndDate());
        coupon.setUsageLimit(updatedCoupon.getUsageLimit());
        coupon.setActive(updatedCoupon.isActive());

        return couponRepository.save(coupon);
    }

    public void deleteCoupon(String id) {
        couponRepository.deleteById(id);
    }

    public Coupon applyCoupon(String code, double orderAmount) throws Exception {
        Optional<Coupon> existingCoupon = couponRepository.findByCode(code);
        if (existingCoupon.isEmpty()) {
            throw new Exception("Mã phiếu giảm giá không hợp lệ: " + code);
        }

        Coupon coupon = existingCoupon.get();

        if (!coupon.isActive()) {
            throw new Exception("Phiếu giảm giá không hoạt động.");
        }
        if (coupon.getStartDate() != null && coupon.getStartDate().after(new java.util.Date())) {
            throw new Exception("Phiếu giảm giá chưa hợp lệ.");
        }
        if (coupon.getEndDate() != null && coupon.getEndDate().before(new java.util.Date())) {
            throw new Exception("Phiếu giảm giá đã hết hạn.");
        }
        if (coupon.getMinimumOrderAmount() != null && orderAmount < coupon.getMinimumOrderAmount()) {
            throw new Exception("Số lượng đặt hàng không đáp ứng yêu cầu tối thiểu cho phiếu giảm giá này.");
        }
        if (coupon.getUsageLimit() != null && coupon.getUsageCount() >= coupon.getUsageLimit()) {
            throw new Exception("Đã đạt đến giới hạn sử dụng phiếu giảm giá.");
        }

        coupon.setUsageCount(coupon.getUsageCount() + 1);
        couponRepository.save(coupon);

        return coupon;
    }

    public List<Coupon> searchCoupons(String searchTerm) {
        return couponRepository.findByCodeContainingIgnoreCase(searchTerm);
    }

}
