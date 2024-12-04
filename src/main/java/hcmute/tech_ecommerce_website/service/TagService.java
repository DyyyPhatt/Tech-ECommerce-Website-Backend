package hcmute.tech_ecommerce_website.service;

import hcmute.tech_ecommerce_website.model.Brand;
import hcmute.tech_ecommerce_website.model.Product;
import hcmute.tech_ecommerce_website.model.Tag;
import hcmute.tech_ecommerce_website.repository.ProductRepository;
import hcmute.tech_ecommerce_website.repository.TagRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ProductRepository productRepository;


    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    public Tag getTagById(String id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nhãn với id: " + id + " không tìm thấy"));
    }

    public Tag addTag(Tag tag) {
        tag.setCreatedAt(new Date());
        tag.setUpdatedAt(new Date());
        return tagRepository.save(tag);
    }

    public Tag updateTag(String id, Tag tagDetails) {
        Optional<Tag> tagOptional = tagRepository.findById(id);
        if (tagOptional.isPresent()) {
            Tag tag = tagOptional.get();
            tag.setName(tagDetails.getName());
            tag.setUpdatedAt(new Date());
            return tagRepository.save(tag);
        } else {
            throw new IllegalArgumentException("Không tìm thấy nhãn");
        }
    }


    public void deleteTag(String id) {
        if (!tagRepository.existsById(id)) {
            throw new IllegalArgumentException("Nhãn với id: " + id + " không tìm thấy");
        }

        List<Product> productsToDelete = productRepository.findByTags(new ObjectId(id));
        if (!productsToDelete.isEmpty()) {
            productRepository.deleteAll(productsToDelete);
        }

        tagRepository.deleteById(id);
    }

    public String checkProductsBeforeDeletingTag(String tagId) {
        if (!tagRepository.existsById(tagId)) {
            throw new IllegalArgumentException("Nhãn với id: " + tagId + " không tìm thấy");
        }

        ObjectId tagObjectId = new ObjectId(tagId);

        List<Product> relatedProducts = productRepository.findByTags(tagObjectId);
        if (!relatedProducts.isEmpty()) {
            return "Có sản phẩm liên quan đến nhãn. Bạn có chắc muốn xóa nhãn này không?";
        }

        return null;
    }

    public void deleteTagWithConfirmation(String tagId, boolean forceDelete) {
        String confirmationMessage = checkProductsBeforeDeletingTag(tagId);
        if (confirmationMessage != null && !forceDelete) {
            throw new IllegalArgumentException(confirmationMessage);
        }

        ObjectId tagObjectId = new ObjectId(tagId);

        List<Product> productsToDelete = productRepository.findByTags(tagObjectId);
        if (!productsToDelete.isEmpty()) {
            productRepository.deleteAll(productsToDelete);
        }

        tagRepository.deleteById(tagId);
    }


    public List<Tag> searchTags(String searchTerm) {
        return tagRepository.findByNameContainingIgnoreCase(searchTerm);
    }
}
