package hcmute.tech_ecommerce_website.service;

import hcmute.tech_ecommerce_website.model.Blog;
import hcmute.tech_ecommerce_website.model.Employee;
import hcmute.tech_ecommerce_website.model.Product;
import hcmute.tech_ecommerce_website.repository.BlogRepository;
import hcmute.tech_ecommerce_website.repository.EmployeeRepository;
import hcmute.tech_ecommerce_website.repository.ProductRepository;
import hcmute.tech_ecommerce_website.util.JwtUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.*;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private BlogRepository blogRepository;


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;


    @Autowired
    private JwtUtil jwtUtil;

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Optional<Employee> getEmployeeById(String id) {
        return employeeRepository.findById(id);
    }

    public Employee addEmployee(Employee employee) {
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        employee.setCreatedAt(new Date());
        employee.setUpdatedAt(new Date());
        return employeeRepository.save(employee);
    }

    public boolean checkUsernameExists(String username) {
        return employeeRepository.existsByUsername(username);
    }

    public Employee updateEmployee(String id, Employee updatedEmployee) {
        return employeeRepository.findById(id).map(employee -> {
            if (updatedEmployee.getPassword() != null && !updatedEmployee.getPassword().isEmpty()) {
                employee.setPassword(passwordEncoder.encode(updatedEmployee.getPassword()));
            }

            if (updatedEmployee.getUsername() != null) {
                employee.setUsername(updatedEmployee.getUsername());
            }
            if (updatedEmployee.getEmail() != null) {
                employee.setEmail(updatedEmployee.getEmail());
            }
            if (updatedEmployee.getPhoneNumber() != null) {
                employee.setPhoneNumber(updatedEmployee.getPhoneNumber());
            }
            if (updatedEmployee.getRole() != null) {
                employee.setRole(updatedEmployee.getRole());
            }

            employee.setUpdatedAt(new Date());
            return employeeRepository.save(employee);
        }).orElse(null);
    }



    public void deleteEmployee(String id) {
        if (!employeeRepository.existsById(id)) {
            throw new IllegalArgumentException("Nhân viên có id: " + id + " không tìm thấy");
        }

        ObjectId employeeObjectId = new ObjectId(id);
        List<Blog> blogsToDelete = blogRepository.findByAuthor(employeeObjectId);

        if (!blogsToDelete.isEmpty()) {
            blogRepository.deleteAll(blogsToDelete);
        }

        employeeRepository.deleteById(id);
    }

    public String checkBlogsBeforeDeletingEmployee(String employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new IllegalArgumentException("Nhân viên có id: " + employeeId + " không tìm thấy");
        }

        ObjectId employeeObjectId = new ObjectId(employeeId);

        List<Blog> relatedBlogs = blogRepository.findByAuthor(employeeObjectId);
        if (!relatedBlogs.isEmpty()) {
            return "Có blog liên quan đến nhân viên này. Bạn có chắc muốn xóa nhân viên này không?";
        }

        return null;
    }


    public void deleteEmployeeWithConfirmation(String employeeId, boolean forceDelete) {
        String confirmationMessage = checkBlogsBeforeDeletingEmployee(employeeId);
        if (confirmationMessage != null && !forceDelete) {
            throw new IllegalArgumentException(confirmationMessage);
        }

        ObjectId employeeObjectId = new ObjectId(employeeId);

        List<Blog> blogsToDelete = blogRepository.findByAuthor(employeeObjectId);
        if (!blogsToDelete.isEmpty()) {
            blogRepository.deleteAll(blogsToDelete);
        }

        employeeRepository.deleteById(employeeId);
    }

    public boolean checkUsernameExistsExceptId(String username, String id) {
        Optional<Employee> employee = employeeRepository.findByUsername(username);
        return employee.isPresent() && !employee.get().getId().equals(id);
    }

    public ResponseEntity<?> loginEmployee(String email, String password) {
        Employee employee = employeeRepository.findByEmail(email);

        if (employee == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email không tồn tại.");
        }

        if (!passwordEncoder.matches(password, employee.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Mật khẩu không hợp lệ.");
        }

        String jwtToken = generateEmployeeToken(employee);

        return ResponseEntity.ok(Map.of(
                "token", jwtToken,
                "message", "Login successful",
                "employeeId", employee.getId(),
                "username", employee.getUsername(),
                "role", employee.getRole()
        ));
    }


    private String generateEmployeeToken(Employee employee) {
        return jwtUtil.generateEmployeeToken(employee.getId(), employee.getUsername());
    }


    public ResponseEntity<?> forgotPassword(String email) {
        Employee employee = employeeRepository.findByEmail(email);
        if (employee == null) {
            return ResponseEntity.badRequest().body("Email không tồn tại.");
        }

        String resetToken = UUID.randomUUID().toString();
        employee.setResetToken(resetToken);
        employee.setResetTokenExpiration(new Date(System.currentTimeMillis() + 3600 * 1000));
        employeeRepository.save(employee);

        try {
            sendResetPasswordEmail(employee);
        } catch (MessagingException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi gửi email.");
        }

        return ResponseEntity.ok("Email reset password đã được gửi tới bạn.");
    }

    private void sendResetPasswordEmail(Employee employee) throws MessagingException {
        String resetUrl = "http://localhost:8080/reset-password/employee/" + employee.getResetToken();
        System.out.println("Reset Password URL: " + resetUrl);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(employee.getEmail());
        helper.setSubject("Yêu cầu reset mật khẩu - Tech E-commerce Website");

        String emailContent = "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px; }" +
                ".container { background-color: #ffffff; border-radius: 5px; padding: 20px; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); }" +
                "h1 { color: #333333; }" +
                "p { color: #555555; }" +
                ".button { display: inline-block; background-color: #007bff; color: white; padding: 10px 20px; border-radius: 5px; text-decoration: none; }" +
                ".footer { margin-top: 20px; font-size: 12px; color: #888888; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<h1>Xin chào,</h1>" +
                "<p>Bạn đã yêu cầu reset mật khẩu cho tài khoản của mình.</p>" +
                "<p>Vui lòng nhấn vào nút dưới đây để đặt lại mật khẩu:</p>" +
                "<a href=\"" + resetUrl + "\" class='button'>Đặt lại mật khẩu</a>" +
                "<p>Nếu bạn không yêu cầu reset mật khẩu, hãy bỏ qua email này.</p>" +
                "<p>Cảm ơn bạn,<br>Đội ngũ <strong>Tech E-commerce Website</strong></p>" +
                "</div>" +
                "<div class='footer'>Bạn nhận được email này vì bạn đã yêu cầu reset mật khẩu.</div>" +
                "</body>" +
                "</html>";

        helper.setText(emailContent, true);
        mailSender.send(message);
    }

    public ResponseEntity<?> resetPassword(String token, String newPassword) {
        Optional<Employee> optionalEmployee = employeeRepository.findByResetToken(token);
        if (optionalEmployee.isEmpty() || optionalEmployee.get().getResetTokenExpiration() == null
                || optionalEmployee.get().getResetTokenExpiration().before(new Date())) {
            return ResponseEntity.badRequest().body("Token không hợp lệ hoặc đã hết hạn.");
        }


        Employee employee = optionalEmployee.get();

        employee.setPassword(new BCryptPasswordEncoder().encode(newPassword));
        employee.setResetToken(null);
        employee.setResetTokenExpiration(null);
        employeeRepository.save(employee);

        return ResponseEntity.ok("Mật khẩu đã được reset thành công.");
    }

}