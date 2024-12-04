package hcmute.tech_ecommerce_website;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
@EnableMongoAuditing
public class TechECommerceWebsiteApplication {

    public static void main(String[] args) {
        SpringApplication.run(TechECommerceWebsiteApplication.class, args);
    }



}
