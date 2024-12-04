package hcmute.tech_ecommerce_website.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        Cloudinary c = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dlop7a2bw",
                "api_key", "712194776841939",
                "api_secret", "gnn0BxbifrQy8WpT9gRIaIFfGyI",
                "secure", true
        ));
        return c;
    }
}