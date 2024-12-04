package hcmute.tech_ecommerce_website.service;

import hcmute.tech_ecommerce_website.model.Policy;
import hcmute.tech_ecommerce_website.repository.PolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class PolicyService {
    @Autowired
    private PolicyRepository policyRepository;

    public List<Policy> getAllPolicies() {
        return policyRepository.findAll();
    }

    public Policy getPolicyById(String id) {
        return policyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Chính sách có id: " + id + " không tìm thấy"));
    }

    public Policy addPolicy(Policy policy) {
        policy.setCreatedAt(new Date());
        policy.setUpdatedAt(new Date());
        return policyRepository.save(policy);
    }

    public Policy updatePolicy(String id, Policy updatedPolicy) {
        updatedPolicy.setId(id);
        updatedPolicy.setUpdatedAt(new Date());
        return policyRepository.save(updatedPolicy);
    }

    public void deletePolicy(String id) {
        policyRepository.deleteById(id);
    }

    public boolean titleExists(String title) {
        return policyRepository.findByTitle(title) != null;
    }

    public Policy getPolicyByTitle(String title) {
        return policyRepository.findByTitle(title);
    }
}
