package hcmute.tech_ecommerce_website.service;

import hcmute.tech_ecommerce_website.model.Product;
import hcmute.tech_ecommerce_website.model.ProductCondition;
import hcmute.tech_ecommerce_website.repository.ProductConditionRepository;
import hcmute.tech_ecommerce_website.repository.ProductRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


import java.util.Date;
import java.util.List;

@Service
public class ProductConditionService {
    @Autowired
    private ProductConditionRepository productConditionRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<ProductCondition> getAllProductConditions() {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        return productConditionRepository.findByIsDeletedFalse(sort);
    }

    public ProductCondition getProductConditionById(String id) {
        return productConditionRepository.findByIdAndIsDeletedFalse(id)
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
        if (!ObjectId.isValid(conditionId)) {
            throw new IllegalArgumentException("Định dạng ID tình trạng không hợp lệ.");
        }

        String confirmationMessage = checkProductsBeforeDeletingCondition(conditionId);
        if (confirmationMessage != null && !forceDelete) {
            throw new IllegalArgumentException(confirmationMessage);
        }

        ProductCondition conditionToDelete = productConditionRepository.findById(conditionId)
                .orElseThrow(() -> new IllegalArgumentException("Tình trạng có id: " + conditionId + " không tìm thấy"));

        conditionToDelete.setDeleted(true);
        conditionToDelete.setUpdatedAt(new Date());
        productConditionRepository.save(conditionToDelete);
    }

    public List<ProductCondition> searchConditions(String searchTerm) {
        return productConditionRepository.findByConditionNameContainingIgnoreCase(searchTerm);
    }

}
