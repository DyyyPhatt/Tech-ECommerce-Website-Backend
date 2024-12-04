package hcmute.tech_ecommerce_website.controller;

import hcmute.tech_ecommerce_website.model.Policy;
import hcmute.tech_ecommerce_website.service.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/policies")
public class PolicyController {
    @Autowired
    private PolicyService policyService;

    @GetMapping("/all")
    public List<Policy> getAllPolicies() {
        return policyService.getAllPolicies();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Policy> getPolicyById(@PathVariable String id) {
        try {
            Policy policy = policyService.getPolicyById(id);
            return ResponseEntity.ok(policy);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/add")
    public ResponseEntity<Policy> addPolicy(@RequestBody Policy policy) {
        Policy newPolicy = policyService.addPolicy(policy);
        return ResponseEntity.ok(newPolicy);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Policy> updatePolicy(
            @PathVariable String id,
            @RequestBody Policy updatedPolicy) {
        Policy policy = policyService.updatePolicy(id, updatedPolicy);
        return ResponseEntity.ok(policy);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletePolicy(@PathVariable String id) {
        policyService.deletePolicy(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<Policy> getPolicyByTitle(@PathVariable String title) {
        Policy policy = policyService.getPolicyByTitle(title);
        if (policy != null) {
            return ResponseEntity.ok(policy);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/check-title")
    public ResponseEntity<Boolean> checkTitleExists(@RequestParam String title) {
        boolean exists = policyService.titleExists(title);
        return ResponseEntity.ok(exists);
    }
}