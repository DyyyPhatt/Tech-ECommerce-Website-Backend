package hcmute.tech_ecommerce_website.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import hcmute.tech_ecommerce_website.util.ObjectIdSerializer;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "Cart")
public class Cart {
    @Id
    private String id;

    @JsonSerialize(using = ObjectIdSerializer.class)
    private ObjectId user;

    private List<Item> items;

    @CreatedDate
    private Date createdAt;

    public static class Item {
        @JsonSerialize(using = ObjectIdSerializer.class)
        private ObjectId product;
        private int quantity;
        private String color;

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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
