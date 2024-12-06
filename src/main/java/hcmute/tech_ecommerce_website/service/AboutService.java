package hcmute.tech_ecommerce_website.service;

import hcmute.tech_ecommerce_website.model.About;
import hcmute.tech_ecommerce_website.repository.AboutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AboutService {

    @Autowired
    private AboutRepository aboutRepository;

    public List<About> getAllAboutInfo() {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        return aboutRepository.findByIsDeletedFalse(sort);
    }

    public About getAboutById(String id) {
        About about = aboutRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Về thông tin không tìm thấy"));
        if (about.isDeleted()) {
            throw new RuntimeException("Về thông tin đã bị xóa");
        }
        return about;
    }

    public About addAbout(About about) {
        about.setCreatedAt(new Date());
        about.setUpdatedAt(new Date());
        about.setDeleted(false);
        return aboutRepository.save(about);
    }

    public About updateAbout(String id, About updatedAbout) {
        Optional<About> existingAbout = aboutRepository.findById(id);

        if (existingAbout.isPresent()) {
            About about = existingAbout.get();
            if (about.isDeleted()) {
                throw new RuntimeException("Không thể cập nhật vì tài liệu đã bị xóa");
            }
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
        Optional<About> about = aboutRepository.findById(id);
        if (about.isPresent()) {
            About existingAbout = about.get();
            existingAbout.setDeleted(true);
            aboutRepository.save(existingAbout);
        } else {
            throw new IllegalArgumentException("Về thông tin có id: " + id + " không tìm thấy");
        }
    }

    public List<About> searchAbouts(String searchTerm) {
        return aboutRepository.findByTitleContainingIgnoreCaseAndIsDeletedFalse(searchTerm);
    }
}