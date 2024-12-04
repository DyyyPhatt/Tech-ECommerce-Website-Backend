package hcmute.tech_ecommerce_website.controller;

import hcmute.tech_ecommerce_website.model.Employee;
import hcmute.tech_ecommerce_website.repository.EmployeeRepository;
import hcmute.tech_ecommerce_website.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping("/all")
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable String id) {
        Optional<Employee> employee = employeeService.getEmployeeById(id);
        return employee.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/add")
    public Employee addEmployee(@RequestBody Employee employee) {
        return employeeService.addEmployee(employee);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable String id, @RequestBody Employee updatedEmployee) {
        Employee employee = employeeService.updateEmployee(id, updatedEmployee);
        return employee != null ? ResponseEntity.ok(employee) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable String id, @RequestParam(defaultValue = "false") boolean force) {
        try {
            String confirmationMessage = employeeService.checkBlogsBeforeDeletingEmployee(id);

            if (confirmationMessage != null && !force) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(confirmationMessage);
            }

            employeeService.deleteEmployeeWithConfirmation(id, force);
            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<Boolean> checkUsernameExists(@PathVariable String username) {
        boolean exists = employeeService.checkUsernameExists(username);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/username/{username}/exclude/{id}")
    public ResponseEntity<Boolean> checkUsernameExistsExceptId(@PathVariable String username, @PathVariable String id) {
        boolean exists = employeeService.checkUsernameExistsExceptId(username, id);
        return ResponseEntity.ok(exists);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");
        return employeeService.loginEmployee(email, password);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        return employeeService.forgotPassword(email);
    }

    @PostMapping("/reset-password/{token}")
    public ResponseEntity<?> resetPassword(@PathVariable String token, @RequestBody Map<String, String> requestBody) {
        String newPassword = requestBody.get("newPassword");
        return employeeService.resetPassword(token, newPassword);
    }

}