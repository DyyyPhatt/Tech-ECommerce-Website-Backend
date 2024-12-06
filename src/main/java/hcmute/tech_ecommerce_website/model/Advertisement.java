package hcmute.tech_ecommerce_website.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "Advertisement")
public class Advertisement {
    @Id
    private String id;
    private List<MainAdv> mainAdv;
    private List<SubAdv> subAdv;
    @CreatedDate
    private Date createdAt;
    @LastModifiedDate
    private Date updatedAt;

    public static class MainAdv {
        private String image;
        private String description;

        public MainAdv() {}

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

    }

    public static class SubAdv {
        private String image;
        private String description;


        public SubAdv() {}

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<MainAdv> getMainAdv() {
        return mainAdv;
    }

    public void setMainAdv(List<MainAdv> mainAdv) {
        this.mainAdv = mainAdv;
    }

    public List<SubAdv> getSubAdv() {
        return subAdv;
    }

    public void setSubAdv(List<SubAdv> subAdv) {
        this.subAdv = subAdv;
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

