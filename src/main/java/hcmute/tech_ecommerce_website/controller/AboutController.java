package hcmute.tech_ecommerce_website.controller;

import hcmute.tech_ecommerce_website.model.About;
import hcmute.tech_ecommerce_website.service.AboutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/about")
public class AboutController {

    @Autowired
    private AboutService aboutService;

    @GetMapping("/all")
    public ResponseEntity<List<About>> getAllAboutInfo() {
        List<About> aboutList = aboutService.getAllAboutInfo();
        return ResponseEntity.ok(aboutList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<About> getAboutById(@PathVariable String id) {
        try {
            About about = aboutService.getAboutById(id);
            return ResponseEntity.ok(about);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/add")
    public ResponseEntity<About> addAbout(@RequestBody About newAbout) {
        About createdAbout = aboutService.addAbout(newAbout);
        return ResponseEntity.ok(createdAbout);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<About> updateAbout(@PathVariable String id, @RequestBody About updatedAbout) {
        try {
            About updated = aboutService.updateAbout(id, updatedAbout);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteAbout(@PathVariable String id) {
        try {
            aboutService.deleteAbout(id);
            return ResponseEntity.ok("Xóa thành công");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<About>> searchAbouts(@RequestParam("q") String searchTerm) {
        List<About> abouts = aboutService.searchAbouts(searchTerm);
        return ResponseEntity.ok(abouts);
    }
}