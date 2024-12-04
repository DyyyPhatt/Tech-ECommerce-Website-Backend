package hcmute.tech_ecommerce_website.service;

import hcmute.tech_ecommerce_website.model.Coupon;
import hcmute.tech_ecommerce_website.model.Order;
import hcmute.tech_ecommerce_website.model.Product;
import hcmute.tech_ecommerce_website.model.User;
import hcmute.tech_ecommerce_website.repository.CouponRepository;
import hcmute.tech_ecommerce_website.repository.OrderRepository;
import hcmute.tech_ecommerce_website.repository.ProductRepository;
import hcmute.tech_ecommerce_website.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CouponRepository couponRepository;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(String id) {
        return orderRepository.findById(id).orElse(null);
    }

    public List<Order> getOrdersByUser(String userId) {
        ObjectId userObjectId;
        try {
            userObjectId = new ObjectId(userId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("ID không hợp lệ: " + userId);
        }

        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User không tồn tại với ID: " + userId);
        }
        return orderRepository.findByUser(userObjectId);
    }

    public Order createOrder(Order order) {
        if (order.getUser() == null) {
            throw new IllegalArgumentException("ID của user không được để trống.");
        }
        Optional<User> user = userRepository.findById(order.getUser().toHexString());
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User không tồn tại với ID: " + order.getUser());
        }
        return orderRepository.save(order);
    }

    private double applyCoupon(Order order) {
        if (order.getCoupon() == null || order.getCoupon() == "" || order.getCoupon().isEmpty()) {
            return 0;
        }

        Optional<Coupon> couponOptional = couponRepository.findByCode(order.getCoupon());
        if (couponOptional.isPresent()) {
            Coupon coupon = couponOptional.get();

            if (!coupon.isActive()) {
                throw new IllegalArgumentException("Coupon không hợp lệ hoặc đã hết hạn.");
            }

            if (coupon.getUsageLimit() != null && coupon.getUsageCount() >= coupon.getUsageLimit()) {
                throw new IllegalArgumentException("Coupon đã hết số lần sử dụng cho phép.");
            }

            if (coupon.getMinimumOrderAmount() != null && order.getTotalAmount() < coupon.getMinimumOrderAmount()) {
                throw new IllegalArgumentException("Đơn hàng không đủ điều kiện áp dụng coupon (min: " + coupon.getMinimumOrderAmount() + ").");
            }

            double discount = 0;

            if ("PERCENTAGE".equalsIgnoreCase(coupon.getDiscountType())) {
                discount = order.getTotalAmount() * coupon.getDiscountValue() / 100;
            } else if ("FIXED".equalsIgnoreCase(coupon.getDiscountType())) {
                discount = coupon.getDiscountValue();
            }

            if (coupon.getMaxDiscountAmount() != null && discount > coupon.getMaxDiscountAmount()) {
                discount = coupon.getMaxDiscountAmount();
            }

            coupon.setUsageCount(coupon.getUsageCount() + 1);

            couponRepository.save(coupon);

            return discount;
        } else {
            throw new IllegalArgumentException("Coupon không tồn tại.");
        }
    }

    public Order createOrderForCustomer(Order order) {
        if (order.getUser() == null) {
            throw new IllegalArgumentException("ID của user không được để trống.");
        }

        Optional<User> user = userRepository.findById(order.getUser().toHexString());
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User không tồn tại với ID: " + order.getUser());
        }

        double totalItemPrice = 0.0;
        for (Order.Item item : order.getItems()) {
            if (item.getProduct() == null || item.getQuantity() <= 0) {
                throw new IllegalArgumentException("Sản phẩm hoặc số lượng không hợp lệ.");
            }

            Optional<Product> productOptional = productRepository.findById(item.getProduct().toHexString());
            if (productOptional.isEmpty()) {
                throw new IllegalArgumentException("Sản phẩm không tồn tại với ID: " + item.getProduct());
            }
            Product product = productOptional.get();

            boolean colorFound = false;
            for (Product.Color color : product.getColors()) {
                if (color.getName().equals(item.getColor())) {
                    if (color.getQuantity() < item.getQuantity()) {
                        throw new IllegalArgumentException("Số lượng sản phẩm không đủ trong kho cho màu sắc: " + item.getColor());
                    }
                    color.setQuantity(color.getQuantity() - item.getQuantity());
                    colorFound = true;
                    break;
                }
            }

            if (!colorFound) {
                throw new IllegalArgumentException("Không tìm thấy màu sắc: " + item.getColor() + " cho sản phẩm ID: " + product.getId());
            }

            double productDiscountPrice = product.getDiscountPrice();
            double priceTotal = productDiscountPrice * item.getQuantity();
            item.setPriceTotal(priceTotal);
            item.setHasReviewed(false);

            productRepository.save(product);

            totalItemPrice += priceTotal;
        }

        order.setCoupon(order.getCoupon() == null ? "" : order.getCoupon());
        order.setNotes(order.getNotes() == null ? "" : order.getNotes());
        order.setShippingCost(20000.0);
        order.setTotalAmount(totalItemPrice);
        order.setStatus("Processing");

        double couponDiscount = applyCoupon(order);

        order.setTotalAmount(totalItemPrice + order.getShippingCost() - couponDiscount);

        return orderRepository.save(order);
    }

    public Order updateOrder(String id, Order order) {
        Optional<Order> existingOrder = orderRepository.findById(id);
        if (existingOrder.isPresent()) {
            order.setId(id);
            return orderRepository.save(order);
        }
        throw new IllegalArgumentException("Order không tồn tại với ID: " + id);
    }

    public void deleteOrder(String id) {
        if (!orderRepository.existsById(id)) {
            throw new IllegalArgumentException("Order không tồn tại với ID: " + id);
        }
        orderRepository.deleteById(id);
    }

    public Order updateHasReviewed(String orderId, String productId) {
        Optional<Order> existingOrder = orderRepository.findById(orderId);
        if (!existingOrder.isPresent()) {
            throw new IllegalArgumentException("Order không tồn tại với ID: " + orderId);
        }

        Order order = existingOrder.get();

        for (Order.Item item : order.getItems()) {
            if (item.getProduct().toHexString().equals(productId)) {
                item.setHasReviewed(true);
                orderRepository.save(order);
                return order;
            }
        }

        throw new IllegalArgumentException("Sản phẩm không tồn tại trong đơn hàng với ID: " + productId);
    }

    public Order updateOrderStatus(String id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng"));
        order.setStatus(status);
        return orderRepository.save(order);
    }

}