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
import java.util.Map;

@Document(collection = "Products")
public class Product {
    @Id
    private String id;
    private String productName;

    @JsonSerialize(using = ObjectIdSerializer.class)
    private ObjectId brand;

    @JsonSerialize(using = ObjectIdSerializer.class)
    private ObjectId category;

    @JsonSerialize(using = ObjectIdSerializer.class)
    private ObjectId condition;

    private String productDesc;
    private double price;
    private double discountPrice;
    private String mainImage;
    private List<String> thumbnails;
    private List<Color> colors;
    private Map<String, Object> specifications;

    @JsonSerialize(contentUsing = ObjectIdSerializer.class)
    private List<ObjectId> tags;

    private Ratings ratings;

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;

    private String mainImagePublicId;

    private String thumbnailsPublicId;

    private boolean isDeleted = false;

    public String getMainImagePublicId() {
        return mainImagePublicId;
    }

    public void setMainImagePublicId(String mainImagePublicId) {
        this.mainImagePublicId = mainImagePublicId;
    }

    public String getThumbnailsPublicId() {
        return thumbnailsPublicId;
    }

    public void setThumbnailsPublicId(String thumbnailsPublicId) {
        this.thumbnailsPublicId = thumbnailsPublicId;
    }

    public static class Color {
        private String name;
        private int quantity;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }

    public static class Ratings {
        private double average;
        private int totalReviews;

        public double getAverage() {
            return average;
        }

        public void setAverage(double average) {
            this.average = average;
        }

        public int getTotalReviews() {
            return totalReviews;
        }

        public void setTotalReviews(int totalReviews) {
            this.totalReviews = totalReviews;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public ObjectId getBrand() {
        return brand;
    }

    public void setBrand(ObjectId brand) {
        this.brand = brand;
    }

    public ObjectId getCategory() {
        return category;
    }

    public void setCategory(ObjectId category) {
        this.category = category;
    }

    public ObjectId getCondition() {
        return condition;
    }

    public void setCondition(ObjectId condition) {
        this.condition = condition;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(double discountPrice) {
        this.discountPrice = discountPrice;
    }

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public List<String> getThumbnails() {
        return thumbnails;
    }

    public void setThumbnails(List<String> thumbnails) {
        this.thumbnails = thumbnails;
    }

    public List<Color> getColors() {
        return colors;
    }

    public void setColors(List<Color> colors) {
        this.colors = colors;
    }

    public Map<String, Object> getSpecifications() {
        return specifications;
    }

    public void setSpecifications(Map<String, Object> specifications) {
        this.specifications = specifications;
    }

    public List<ObjectId> getTags() {
        return tags;
    }

    public void setTags(List<ObjectId> tags) {
        this.tags = tags;
    }

    public Ratings getRatings() {
        return ratings;
    }

    public void setRatings(Ratings ratings) {
        this.ratings = ratings;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
