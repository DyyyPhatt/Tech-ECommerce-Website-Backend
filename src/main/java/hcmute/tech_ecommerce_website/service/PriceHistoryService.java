package hcmute.tech_ecommerce_website.service;

import hcmute.tech_ecommerce_website.model.PriceHistory;
import hcmute.tech_ecommerce_website.repository.PriceHistoryRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class PriceHistoryService {

    @Autowired
    private PriceHistoryRepository priceHistoryRepository;


    public PriceHistory savePriceHistory(ObjectId product, Double oldPrice, Double newPrice,
                                         Double oldDiscountPrice, Double newDiscountPrice) {
        PriceHistory priceHistory = new PriceHistory();

        priceHistory.setProduct(product);
        priceHistory.setOldPrice(oldPrice != null ? oldPrice : 0.0);
        priceHistory.setNewPrice(newPrice != null ? newPrice : 0.0);
        priceHistory.setOldDiscountPrice(oldDiscountPrice != null ? oldDiscountPrice : 0.0);
        priceHistory.setNewDiscountPrice(newDiscountPrice != null ? newDiscountPrice : 0.0);

        priceHistory.setCreatedAt(new Date());
        priceHistory.setUpdatedAt(new Date());

        return priceHistoryRepository.save(priceHistory);
    }




    public List<PriceHistory> getPriceHistoriesByProduct(ObjectId product) {
        return priceHistoryRepository.findByProduct(product);
    }



    public PriceHistory updatePriceHistory(String id, ObjectId product, Double oldPrice, Double newPrice,
                                           Double oldDiscountPrice, Double newDiscountPrice) {
        PriceHistory existingPriceHistory = priceHistoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PriceHistory not found"));

        boolean isPriceChanged = (newPrice != null && existingPriceHistory.getNewPrice() != newPrice);
        boolean isDiscountPriceChanged = (newDiscountPrice != null && existingPriceHistory.getNewDiscountPrice() != newDiscountPrice);

        if (isPriceChanged || isDiscountPriceChanged) {
            existingPriceHistory.setOldPrice(existingPriceHistory.getNewPrice());
            existingPriceHistory.setNewPrice(newPrice != null ? newPrice : 0.0);

            existingPriceHistory.setOldDiscountPrice(existingPriceHistory.getNewDiscountPrice());
            existingPriceHistory.setNewDiscountPrice(newDiscountPrice != null ? newDiscountPrice : 0.0);

            existingPriceHistory.setUpdatedAt(new Date());
            return priceHistoryRepository.save(existingPriceHistory);
        }

        return existingPriceHistory;
    }


}