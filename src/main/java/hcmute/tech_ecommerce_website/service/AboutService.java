package hcmute.tech_ecommerce_website.service;

import hcmute.tech_ecommerce_website.model.About;
import hcmute.tech_ecommerce_website.model.Brand;
import hcmute.tech_ecommerce_website.repository.AboutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AboutService {

    @Autowired
    private AboutRepository aboutRepository;

    public List<About> getAllAboutInfo() {
        return aboutRepository.findAll();
    }

    public About getAboutById(String id) {
        return aboutRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Về thông tin không tìm thấy"));
    }

    public About addAbout(About about) {
        about.setCreatedAt(new Date());
        about.setUpdatedAt(new Date());
        return aboutRepository.save(about);
    }

    public About updateAbout(String id, About updatedAbout) {
        Optional<About> existingAbout = aboutRepository.findById(id);

        if (existingAbout.isPresent()) {
            About about = existingAbout.get();
            about.setTitle(updatedAbout.getTitle());
            about.setContent(updatedAbout.getContent());
            about.setImage(updatedAbout.getImage());
            about.setCreator(updatedAbout.getCreator());
            about.setUpdatedAt(new Date());
            return aboutRepository.save(about);
        } else {
            throw new RuntimeException("Về thông tin không tìm thấy");
        }
    }

    public void deleteAbout(String id) {
        if (aboutRepository.existsById(id)) {
            aboutRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Về thông tin có id: " + id + " không tìm thấy");
        }
    }

    public List<About> searchAbouts(String searchTerm) {
        return aboutRepository.findByTitleContainingIgnoreCase(searchTerm);
    }

}
