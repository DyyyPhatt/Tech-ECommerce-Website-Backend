package hcmute.tech_ecommerce_website.service;

import hcmute.tech_ecommerce_website.model.Brand;
import hcmute.tech_ecommerce_website.model.Product;
import hcmute.tech_ecommerce_website.model.Tag;
import hcmute.tech_ecommerce_website.repository.ProductRepository;
import hcmute.tech_ecommerce_website.repository.TagRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        return tagRepository.findByIsDeletedFalse(sort);
    }

    public Tag getTagById(String id) {
        return tagRepository.findByIdAndIsDeletedFalse(id)
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

    public String checkProductsBeforeDeletingTag(String tagsId) {
        if (!tagRepository.existsById(tagsId)) {
            throw new IllegalArgumentException("Nhãn với id: " + tagsId + " không tìm thấy");
        }

        ObjectId tagObjectId = new ObjectId(tagsId);

        List<Product> relatedProducts = productRepository.findByTags(tagObjectId);
        if (!relatedProducts.isEmpty()) {
            return "Có sản phẩm liên quan đến nhãn. Bạn có chắc muốn xóa nhãn này không?";
        }

        return null;
    }

    public void deleteTagWithConfirmation(String tagsId, boolean forceDelete) {
        if (!ObjectId.isValid(tagsId)) {
            throw new IllegalArgumentException("Định dạng ID tình trạng không hợp lệ.");
        }

        String confirmationMessage = checkProductsBeforeDeletingTag(tagsId);
        if (confirmationMessage != null && !forceDelete) {
            throw new IllegalArgumentException(confirmationMessage);
        }

        Tag tagToDelete = tagRepository.findById(tagsId)
                .orElseThrow(() -> new IllegalArgumentException("Nhãn có id: " + tagsId + " không tìm thấy"));

        tagToDelete.setDeleted(true);
        tagToDelete.setUpdatedAt(new Date());
        tagRepository.save(tagToDelete);
    }

    public List<Tag> searchTags(String searchTerm) {
        return tagRepository.findByNameContainingIgnoreCase(searchTerm);
    }
}
