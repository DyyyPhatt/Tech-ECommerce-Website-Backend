package hcmute.tech_ecommerce_website.controller;

import hcmute.tech_ecommerce_website.model.Advertisement;
import hcmute.tech_ecommerce_website.service.AdvertisementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/advertisement")
public class AdvertisementController {

    @Autowired
    private AdvertisementService advertisementService;

    @GetMapping("/all")
    public List<Advertisement> getAllAdvertisements() {
        return advertisementService.getAllAdvertisements();
    }

    @PostMapping("/add")
    public ResponseEntity<Advertisement> addAdvertisement(@RequestBody Advertisement advertisement) {
        Advertisement savedAdvertisement = advertisementService.addAdvertisement(advertisement);
        return new ResponseEntity<>(savedAdvertisement, HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Advertisement> updateAdvertisement(@PathVariable String id, @RequestBody Advertisement advertisement) {
        Advertisement updatedAdvertisement = advertisementService.updateAdvertisement(id, advertisement);
        return new ResponseEntity<>(updatedAdvertisement, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAdvertisement(@PathVariable String id) {
        advertisementService.deleteAdvertisement(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Advertisement> getAdvertisementById(@PathVariable String id) {
        Advertisement advertisement = advertisementService.getAdvertisementById(id);
        return new ResponseEntity<>(advertisement, HttpStatus.OK);
    }

}