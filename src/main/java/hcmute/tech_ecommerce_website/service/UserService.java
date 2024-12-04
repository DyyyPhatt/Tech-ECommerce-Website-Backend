package hcmute.tech_ecommerce_website.service;

import com.cloudinary.utils.ObjectUtils;
import hcmute.tech_ecommerce_website.model.Brand;
import hcmute.tech_ecommerce_website.model.User;
import hcmute.tech_ecommerce_website.repository.UserRepository;
import hcmute.tech_ecommerce_website.util.JwtUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private CartService cartService;


    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Scheduled(fixedRate = 3600000)
    public void scheduledCleanupUnverifiedUsers() {
        deleteUnverifiedUsers();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(String id) {
        return userRepository.findById(id).orElse(null);
    }

    public User addUser(User user) {
        user.setCreatedAt(new Date());
        return userRepository.save(user);
    }


    public User updateUser(String id, User updatedUser, MultipartFile newImage) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Thương hiệu có id: " + id + " không tìm thấy"));

        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPhone_number(updatedUser.getPhone_number());
        existingUser.setAddress(updatedUser.getAddress());

        if (newImage != null && !newImage.isEmpty()) {
            try {
                if (existingUser.getUserImagePublicId() != null) {
                    cloudinaryService.deleteImage(existingUser.getUserImagePublicId());
                }

                String imageUrl = cloudinaryService.uploadImageForUser(newImage, existingUser);
                existingUser.setAvatar(imageUrl);

                String publicId = existingUser.getUserImagePublicId();
                existingUser.setUserImagePublicId(publicId);
            } catch (IOException e) {
                throw new RuntimeException("Lỗi tải lên hoặc cập nhật hình ảnh: " + e.getMessage(), e);
            }
        }

        existingUser.setUpdatedAt(new Date());
        return userRepository.save(existingUser);
    }


    public boolean isUsernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public void deleteUser(String id) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (user.getUserImagePublicId() != null && !user.getUserImagePublicId().isEmpty()) {
                try {
                    cloudinaryService.deleteImage(user.getUserImagePublicId());
                } catch (IOException e) {
                    throw new RuntimeException("Lỗi xóa ảnh người dùng trên Cloudinary: " + e.getMessage(), e);
                }
            }

            cartService.deleteCartByUserId(new ObjectId(user.getId()));

            userRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Người dùng có ID " + id + " không tìm thấy.");
        }
    }

    private String generateVerificationToken() {
        return UUID.randomUUID().toString();
    }

    public boolean isEmailExists(String email) {
        return userRepository.findByEmail(email) != null; // Kiểm tra email
    }

    public ResponseEntity<?> registerUser(User user) {
        if (isEmailExists(user.getEmail())) {
            return ResponseEntity.badRequest().body("Email đã tồn tại. Vui lòng sử dụng email khác.");
        }

        if (user.getAvatar() == null || user.getAvatar().isEmpty()) {
            user.setAvatar("https://i.postimg.cc/rp7L1xMh/user.jpg");
        }

        user.setPhone_number("");
        user.setAddress(new User.Address());
        user.getAddress().setStreet("");
        user.getAddress().setCommunes("");
        user.getAddress().setDistrict("");
        user.getAddress().setCity("");
        user.getAddress().setCountry("");

        user.setCreatedAt(new Date());

        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));

        String verificationToken = generateVerificationToken();
        user.setVerificationToken(verificationToken);
        user.setVerificationExpiry(new Date(System.currentTimeMillis() + 3600 * 1000)); // 1 giờ

        userRepository.save(user);

        try {
            sendVerificationEmail(user);
        } catch (MessagingException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Có lỗi xảy ra khi gửi email xác thực.");
        }

        String jwtToken = jwtUtil.generateToken(user.getId(), user.getUsername());

        return ResponseEntity.ok(Map.of(
                "message", "Đăng ký thành công! Vui lòng kiểm tra email của bạn để xác thực tài khoản.",
                "token", jwtToken
        ));
    }

    private void sendVerificationEmail(User user) throws MessagingException {
        String verificationUrl = "http://localhost:8081/api/users/verify/" + user.getVerificationToken();
        System.out.println("Verification URL: " + verificationUrl);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(user.getEmail());
        helper.setSubject("Xác thực tài khoản - Tech E-commerce Website");

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
                "<h1>Chào " + user.getUsername() + ",</h1>" +
                "<p>Cảm ơn bạn đã đăng ký tài khoản tại <strong>Tech E-commerce Website</strong>!</p>" +
                "<p>Để hoàn tất quá trình đăng ký, vui lòng xác thực tài khoản của bạn bằng cách nhấn vào nút dưới đây:</p>" +
                "<a href=\"" + verificationUrl + "\" class='button'>Xác thực tài khoản</a>" +
                "<p>Nếu bạn không phải là người đăng ký, hãy bỏ qua email này.</p>" +
                "<p>Cảm ơn bạn,<br>Đội ngũ <strong>Tech E-commerce Website</strong></p>" +
                "</div>" +
                "<div class='footer'>Bạn nhận được email này vì bạn đã đăng ký tài khoản trên website của chúng tôi.</div>" +
                "</body>" +
                "</html>";

        helper.setText(emailContent, true);
        mailSender.send(message);
    }

    public boolean verifyUser(String token) {
        User user = userRepository.findByVerificationToken(token);
        if (user != null) {
            if (user.getVerificationExpiry() != null && user.getVerificationExpiry().after(new Date())) {
                user.setVerified(true);
                user.setVerificationToken(null);
                user.setVerificationExpiry(null);
                cartService.createCartForUser(new ObjectId(user.getId()));
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    public ResponseEntity<?> forgotPassword(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body("Email không tồn tại.");
        }

        String resetToken = UUID.randomUUID().toString();
        user.setVerificationToken(resetToken);
        user.setVerificationExpiry(new Date(System.currentTimeMillis() + 3600 * 1000));
        userRepository.save(user);

        try {
            sendResetPasswordEmail(user);
        } catch (MessagingException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi gửi email.");
        }

        return ResponseEntity.ok("Email reset password đã được gửi tới bạn.");
    }

    private void sendResetPasswordEmail(User user) throws MessagingException {
        String resetUrl = "http://localhost:8080/reset-password/" + user.getVerificationToken();
        System.out.println("Reset Password URL: " + resetUrl);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(user.getEmail());
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
        User user = userRepository.findByVerificationToken(token);
        if (user == null || user.getVerificationExpiry() == null || user.getVerificationExpiry().before(new Date())) {
            return ResponseEntity.badRequest().body("Token không hợp lệ hoặc đã hết hạn.");
        }

        user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
        user.setVerificationToken(null);
        user.setVerificationExpiry(null);
        userRepository.save(user);

        return ResponseEntity.ok("Mật khẩu đã được reset thành công.");
    }

    public ResponseEntity<?> loginUser(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email không tồn tại.");
        }

        if (!user.isVerified()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Tài khoản của bạn chưa được xác thực. Vui lòng kiểm tra email để xác thực tài khoản.");
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Mật khẩu không chính xác.");
        }

        String jwtToken = jwtUtil.generateToken(user.getId(), user.getUsername());

        return ResponseEntity.ok(Map.of(
                "token", jwtToken,
                "message", "Đăng nhập thành công",
                "userId", user.getId(),
                "username", user.getUsername()
        ));
    }

    public User findOrCreateGoogleUser(String email, String name) {
        User existingUser = userRepository.findByEmail(email);
        if (existingUser != null) {
            if (existingUser.getUsername() == null || existingUser.getUsername().isEmpty()) {
                existingUser.setUsername(name);
            }
            return existingUser;
        }

        User newUser = new User();
        newUser.setAvatar("https://i.postimg.cc/rp7L1xMh/user.jpg");
        newUser.setEmail(email);
        newUser.setUsername(name);
        newUser.setPassword("");
        newUser.setPhone_number("");
        newUser.setAddress(new User.Address());
        newUser.getAddress().setStreet("");
        newUser.getAddress().setCommunes("");
        newUser.getAddress().setDistrict("");
        newUser.getAddress().setCity("");
        newUser.getAddress().setCountry("");
        newUser.setVerified(true);
        userRepository.save(newUser);
        return newUser;
    }

    public void deleteUnverifiedUsers() {
        List<User> users = userRepository.findAll();
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.HOUR, -1);

        for (User user : users) {
            if (!user.isVerified() && user.getCreatedAt() != null && user.getCreatedAt().before(cal.getTime())) {
                userRepository.delete(user);
            }
        }
    }
}
