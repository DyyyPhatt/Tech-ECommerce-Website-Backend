package hcmute.tech_ecommerce_website.service;

import hcmute.tech_ecommerce_website.model.Product;
import hcmute.tech_ecommerce_website.model.ProductCondition;
import hcmute.tech_ecommerce_website.model.Tag;
import hcmute.tech_ecommerce_website.repository.ProductConditionRepository;
import hcmute.tech_ecommerce_website.repository.ProductRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ProductConditionService {
    @Autowired
    private ProductConditionRepository productConditionRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<ProductCondition> getAllProductConditions() {
        return productConditionRepository.findAll();
    }

    public ProductCondition getProductConditionById(String id) {
        return productConditionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tình trạng sản phẩm có id: " + id + " không tìm thấy"));
    }

    public ProductCondition addProductCondition(ProductCondition productCondition) {
        return productConditionRepository.save(productCondition);
    }

    public ProductCondition updateProductCondition(String id, ProductCondition productConditionDetails) {
        ProductCondition productCondition = productConditionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tình trạng sản phẩm"));
        productCondition.setConditionName(productConditionDetails.getConditionName());
        productCondition.setDescription(productConditionDetails.getDescription());
        productCondition.setUpdatedAt(new Date());

        return productConditionRepository.save(productCondition);
    }

    public void deleteCondtion(String id) {
        if (!productConditionRepository.existsById(id)) {
            throw new IllegalArgumentException("Điều kiện có id: " + id + " không tìm thấy");
        }

        List<Product> productsToDelete = productRepository.findByCondition(new ObjectId(id));
        if (!productsToDelete.isEmpty()) {
            productRepository.deleteAll(productsToDelete);
        }

        productConditionRepository.deleteById(id);
    }

    public String checkProductsBeforeDeletingCondition(String conditionId) {
        if (!productConditionRepository.existsById(conditionId)) {
            throw new IllegalArgumentException("Tình trạng có id: " + conditionId + " không tìm thấy");
        }

        ObjectId ConditionObjectId = new ObjectId(conditionId);

        List<Product> relatedProducts = productRepository.findByCondition(ConditionObjectId);
        if (!relatedProducts.isEmpty()) {
            return "Có sản phẩm liên quan đến tình trạng. Bạn có chắc muốn xóa tình trạng này không?";
        }

        return null;
    }

    public void deleteConditionWithConfirmation(String conditionId, boolean forceDelete) {
        String confirmationMessage = checkProductsBeforeDeletingCondition(conditionId);
        if (confirmationMessage != null && !forceDelete) {
            throw new IllegalArgumentException(confirmationMessage);
        }

        ObjectId conditionObjectId = new ObjectId(conditionId);

        List<Product> productsToDelete = productRepository.findByCondition(conditionObjectId);
        if (!productsToDelete.isEmpty()) {
            productRepository.deleteAll(productsToDelete);
        }

        productConditionRepository.deleteById(conditionId);
    }

    public List<ProductCondition> searchConditions(String searchTerm) {
        return productConditionRepository.findByConditionNameContainingIgnoreCase(searchTerm);
    }

}
