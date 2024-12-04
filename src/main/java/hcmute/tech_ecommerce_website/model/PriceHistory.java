package hcmute.tech_ecommerce_website.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import hcmute.tech_ecommerce_website.util.ObjectIdSerializer;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "PriceHistory")
public class PriceHistory {

    @Id
    private String id;

    @JsonSerialize(using = ObjectIdSerializer.class)
    private ObjectId product;

    private double oldPrice;
    private double newPrice;
    private double oldDiscountPrice;
    private double newDiscountPrice;

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ObjectId getProduct() {
        return product;
    }

    public void setProduct(ObjectId product) {
        this.product = product;
    }

    public double getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(double oldPrice) {
        this.oldPrice = oldPrice;
    }

    public double getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(double newPrice) {
        this.newPrice = newPrice;
    }

    public double getOldDiscountPrice() {
        return oldDiscountPrice;
    }

    public void setOldDiscountPrice(double oldDiscountPrice) {
        this.oldDiscountPrice = oldDiscountPrice;
    }

    public double getNewDiscountPrice() {
        return newDiscountPrice;
    }

    public void setNewDiscountPrice(double newDiscountPrice) {
        this.newDiscountPrice = newDiscountPrice;
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
}