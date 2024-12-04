package hcmute.tech_ecommerce_website.service;

import hcmute.tech_ecommerce_website.model.Contact;
import hcmute.tech_ecommerce_website.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ContactService {
    @Autowired
    private ContactRepository contactRepository;

    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    public Contact getContactById(String id) {
        return contactRepository.findById(id).orElse(null);
    }
    public Contact addContact(Contact contact) {
        return contactRepository.save(contact);
    }

    public Contact updateContact(String id, Contact updatedContact) {
        Optional<Contact> existingContact = contactRepository.findById(id);

        if (existingContact.isPresent()) {
            Contact contact = existingContact.get();
            contact.setAddress(updatedContact.getAddress());
            contact.setPhone_number(updatedContact.getPhone_number());
            contact.setEmail(updatedContact.getEmail());
            contact.setTimeServing(updatedContact.getTimeServing());
            contact.setGoogleMap(updatedContact.getGoogleMap());
            contact.setUpdatedAt(new Date());
            contact.setUpdatedAt(new Date());
            return contactRepository.save(contact);
        } else {
            throw new RuntimeException("Không tìm thấy liên hệ");
        }
    }

    public void deleteContact(String id) {
        contactRepository.deleteById(id);
    }
}

