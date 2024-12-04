package hcmute.tech_ecommerce_website.controller;

import hcmute.tech_ecommerce_website.model.ProductCondition;
import hcmute.tech_ecommerce_website.model.Tag;
import hcmute.tech_ecommerce_website.service.ProductConditionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/product-conditions")
public class ProductConditionController {

    @Autowired
    private ProductConditionService productConditionService;

    @GetMapping("/all")
    public List<ProductCondition> getAllProductConditions() {
        return productConditionService.getAllProductConditions();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductCondition> getProductConditionById(@PathVariable String id) {
        try {
            ProductCondition productCondition = productConditionService.getProductConditionById(id);
            return ResponseEntity.ok(productCondition);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/add")
    public ProductCondition createProductCondition(@RequestBody ProductCondition productCondition) {
        return productConditionService.addProductCondition(productCondition);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ProductCondition> updateProductCondition(@PathVariable String id,
                                                                   @RequestBody ProductCondition productConditionDetails) {
        try {
            ProductCondition updatedProductCondition = productConditionService.updateProductCondition(id, productConditionDetails);
            return ResponseEntity.ok(updatedProductCondition);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCondition(@PathVariable String id, @RequestParam(defaultValue = "false") boolean force) {
        try {
            String confirmationMessage = productConditionService.checkProductsBeforeDeletingCondition(id);

            if (confirmationMessage != null && !force) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(confirmationMessage);
            }

            productConditionService.deleteConditionWithConfirmation(id, force);
            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductCondition>> searchConditions(@RequestParam("q") String searchTerm) {
        try {
            String decodedSearchTerm = URLDecoder.decode(searchTerm, StandardCharsets.UTF_8.name());
            List<ProductCondition> conditions = productConditionService.searchConditions(decodedSearchTerm);
            return ResponseEntity.ok(conditions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }



}
