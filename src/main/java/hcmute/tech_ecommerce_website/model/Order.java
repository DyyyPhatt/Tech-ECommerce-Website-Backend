package hcmute.tech_ecommerce_website.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import hcmute.tech_ecommerce_website.util.ObjectIdSerializer;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "Orders")
public class Order {
    @Id
    private String id;

    @JsonSerialize(using = ObjectIdSerializer.class)
    private ObjectId user;

    private List<Item> items;

    private String fullName;

    private String phoneNumber;

    private Address shippingAddress;

    private String notes;

    private double shippingCost;

    private String coupon;

    private double couponDiscount;

    private double totalAmount;

    private String paymentMethod;

    private String status;

    @CreatedDate
    private Date orderCreatedAt;

    @LastModifiedDate
    private Date orderUpdatedAt;

    public static class Item {
        @JsonSerialize(using = ObjectIdSerializer.class)
        private ObjectId product;

        private int quantity;
        private String color;
        private double priceTotal;
        private boolean hasReviewed;

        public ObjectId getProduct() {
            return product;
        }

        public void setProduct(ObjectId product) {
            this.product = product;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public double getPriceTotal() {
            return priceTotal;
        }

        public void setPriceTotal(double priceTotal) {
            this.priceTotal = priceTotal;
        }

        public boolean isHasReviewed() {
            return hasReviewed;
        }

        public void setHasReviewed(boolean hasReviewed) {
            this.hasReviewed = hasReviewed;
        }
    }

    public static class Address {
        private String street;
        private String communes;
        private String district;
        private String city;
        private String country;

        // Getters and setters
        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getCommunes() {
            return communes;
        }

        public void setCommunes(String communes) {
            this.communes = communes;
        }

        public String getDistrict() {
            return district;
        }

        public void setDistrict(String district) {
            this.district = district;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ObjectId getUser() {
        return user;
    }

    public void setUser(ObjectId user) {
        this.user = user;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Address getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public double getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(double shippingCost) {
        this.shippingCost = shippingCost;
    }

    public String getCoupon() {
        return coupon;
    }

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }

    public double getCouponDiscount() {
        return couponDiscount;
    }

    public void setCouponDiscount(double couponDiscount) {
        this.couponDiscount = couponDiscount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getOrderCreatedAt() {
        return orderCreatedAt;
    }

    public void setOrderCreatedAt(Date orderCreatedAt) {
        this.orderCreatedAt = orderCreatedAt;
    }

    public Date getOrderUpdatedAt() {
        return orderUpdatedAt;
    }

    public void setOrderUpdatedAt(Date orderUpdatedAt) {
        this.orderUpdatedAt = orderUpdatedAt;
    }
}