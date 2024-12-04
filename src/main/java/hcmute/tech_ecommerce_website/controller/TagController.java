package hcmute.tech_ecommerce_website.controller;

import hcmute.tech_ecommerce_website.model.Brand;
import hcmute.tech_ecommerce_website.model.Tag;
import hcmute.tech_ecommerce_website.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    @Autowired
    private TagService tagService;

    @GetMapping("/all")
    public List<Tag> getAllTags() {
        return tagService.getAllTags();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tag> getTagById(@PathVariable String id) {
        try {
            Tag tag = tagService.getTagById(id);
            return ResponseEntity.ok(tag);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/add")
    public Tag createTag(@RequestBody Tag tag) {
        return tagService.addTag(tag);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Tag> updateTag(@PathVariable String id,
                                         @RequestBody Tag tagDetails) {
        try {
            Tag updatedTag = tagService.updateTag(id, tagDetails);
            return ResponseEntity.ok(updatedTag);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteTag(@PathVariable String id, @RequestParam(defaultValue = "false") boolean force) {
        try {
            String confirmationMessage = tagService.checkProductsBeforeDeletingTag(id);

            if (confirmationMessage != null && !force) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(confirmationMessage);
            }
            tagService.deleteTagWithConfirmation(id, force);
            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Tag>> searchTags(@RequestParam("q") String searchTerm) {
        List<Tag> tags = tagService.searchTags(searchTerm);
        return ResponseEntity.ok(tags);
    }

}
