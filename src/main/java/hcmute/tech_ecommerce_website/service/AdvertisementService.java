package hcmute.tech_ecommerce_website.service;

import hcmute.tech_ecommerce_website.model.Advertisement;
import hcmute.tech_ecommerce_website.repository.AdvertisementRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class AdvertisementService {

    @Autowired
    private AdvertisementRepository advertisementRepository;

    public Advertisement addAdvertisement(Advertisement advertisement) {
        return advertisementRepository.save(advertisement);
    }

    public Advertisement updateAdvertisement(String id, Advertisement advertisement) {
        Optional<Advertisement> existingAdvertisement = advertisementRepository.findById(id);
        if (existingAdvertisement.isPresent()) {
            Advertisement existingAdv = existingAdvertisement.get();

            advertisement.setCreatedAt(existingAdv.getCreatedAt());

            advertisement.setId(id);
            return advertisementRepository.save(advertisement);
        } else {
            throw new RuntimeException("Không tìm thấy quảng cáo có id: " + id);
        }
    }

    public void deleteAdvertisement(String id) {
        advertisementRepository.deleteById(id);
    }

    public List<Advertisement> getAllAdvertisements() {
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        return advertisementRepository.findAll(sort);
    }

    public Advertisement getAdvertisementById(String id) {
        return advertisementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Quảng cáo có id: " + id + " không tìm thấy"));
    }
}
