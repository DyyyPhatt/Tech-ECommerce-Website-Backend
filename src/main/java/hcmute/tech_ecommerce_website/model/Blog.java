package hcmute.tech_ecommerce_website.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import hcmute.tech_ecommerce_website.util.ObjectIdSerializer;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Document(collection = "Blogs")
public class Blog {
    @Id
    private String id;
    private String title;

    @JsonSerialize(using = ObjectIdSerializer.class)
    private ObjectId author;
    private String content;

    @JsonSerialize(using = ObjectIdSerializer.class)
    private ObjectId category;

    @CreatedDate
    private Date publishedDate;
    @LastModifiedDate
    private Date updatedAt;
    private List<String> blogImage;
    private List<Comment> comments;

    private String blogImagePublicId;

    private boolean isDeleted = false;

    public List<String> getBlogImagePublicId() {
        if (blogImagePublicId == null || blogImagePublicId.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(blogImagePublicId.split(","));
    }

    public void setBlogImagePublicId(List<String> publicIds) {
        if (publicIds == null || publicIds.isEmpty()) {
            this.blogImagePublicId = null;
        } else {
            this.blogImagePublicId = String.join(",", publicIds);
        }
    }

    public static class Comment {
        @JsonSerialize(using = ObjectIdSerializer.class)
        private ObjectId user;
        private String comment;
        private Date createdAt;

        public ObjectId getUser() {
            return user;
        }

        public void setUser(ObjectId user) {
            this.user = user;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public Date getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(Date createdAt) {
            this.createdAt = createdAt;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ObjectId getAuthor() {
        return author;
    }

    public void setAuthor(ObjectId author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ObjectId getCategory() {
        return category;
    }

    public void setCategory(ObjectId category) {
        this.category = category;
    }

    public Date getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<String> getBlogImage() {
        return blogImage;
    }

    public void setBlogImage(List<String> blogImage) {
        this.blogImage = blogImage;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public void setBlogImagePublicId(String blogImagePublicId) {
        this.blogImagePublicId = blogImagePublicId;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}