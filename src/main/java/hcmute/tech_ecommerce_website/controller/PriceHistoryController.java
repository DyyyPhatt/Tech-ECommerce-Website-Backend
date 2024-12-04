package hcmute.tech_ecommerce_website.controller;

import hcmute.tech_ecommerce_website.model.PriceHistory;
import hcmute.tech_ecommerce_website.service.PriceHistoryService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/price-history")
public class PriceHistoryController {

    @Autowired
    private PriceHistoryService priceHistoryService;

    @PostMapping("/add")
    public PriceHistory addPriceHistory(@RequestParam("product") String product,
                                        @RequestParam("oldPrice") Double oldPrice,
                                        @RequestParam("newPrice") Double newPrice,
                                        @RequestParam("oldDiscountPrice") Double oldDiscountPrice,
                                        @RequestParam("newDiscountPrice") Double newDiscountPrice) {
        ObjectId productObjectId = new ObjectId(product);
        return priceHistoryService.savePriceHistory(productObjectId, oldPrice, newPrice, oldDiscountPrice, newDiscountPrice);
    }

    @GetMapping("/product/{productId}")
    public List<PriceHistory> getPriceHistories(@PathVariable String productId) {
        ObjectId productObjectId = new ObjectId(productId);
        return priceHistoryService.getPriceHistoriesByProduct(productObjectId);
    }

    @PutMapping("/update/{id}")
    public PriceHistory updatePriceHistory(@PathVariable String id,
                                           @RequestParam("product") String product,
                                           @RequestParam("oldPrice") Double oldPrice,
                                           @RequestParam("newPrice") Double newPrice,
                                           @RequestParam("oldDiscountPrice") Double oldDiscountPrice,
                                           @RequestParam("newDiscountPrice") Double newDiscountPrice) {
        ObjectId productObjectId = new ObjectId(product);
        return priceHistoryService.updatePriceHistory(id, productObjectId, oldPrice, newPrice, oldDiscountPrice, newDiscountPrice);
    }
}