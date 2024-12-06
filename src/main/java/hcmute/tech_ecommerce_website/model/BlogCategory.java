package hcmute.tech_ecommerce_website.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "BlogCategories")
public class BlogCategory {
    @Id
    private String id;
    private String cateBlogName;
    private String cateBlogDesc;

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;

    private boolean isDeleted = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCateBlogName() {
        return cateBlogName;
    }

    public void setCateBlogName(String cateBlogName) {
        this.cateBlogName = cateBlogName;
    }

    public String getCateBlogDesc() {
        return cateBlogDesc;
    }

    public void setCateBlogDesc(String cateBlogDesc) {
        this.cateBlogDesc = cateBlogDesc;
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