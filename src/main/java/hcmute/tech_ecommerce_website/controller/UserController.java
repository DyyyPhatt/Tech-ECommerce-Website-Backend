package hcmute.tech_ecommerce_website.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hcmute.tech_ecommerce_website.model.Brand;
import hcmute.tech_ecommerce_website.model.User;
import hcmute.tech_ecommerce_website.repository.UserRepository;
import hcmute.tech_ecommerce_website.service.CloudinaryService;
import hcmute.tech_ecommerce_website.service.GoogleAuthService;
import hcmute.tech_ecommerce_website.service.UserService;
import hcmute.tech_ecommerce_website.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GoogleAuthService googleAuthService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        User user = userService.getUserById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/add")
    public User addUser(@RequestBody User user) {
        return userService.addUser(user);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable String id,
            @RequestBody User updatedUser,
            @RequestParam(value = "image", required = false) MultipartFile newImage) {

        try {
            User user = userService.updateUser(id, updatedUser, newImage);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Người dùng có id: " + id + " không tìm thấy.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi cập nhật dữ liệu người dùng: " + e.getMessage());
        }
    }

    @PutMapping("/update/{id}/admin")
    public ResponseEntity<?> updateUserAdmin(
            @PathVariable String id,
            @ModelAttribute User updatedUser,
            @RequestParam(value = "image", required = false) MultipartFile newImage) {

        try {
            User user = userService.updateUser(id, updatedUser, newImage);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with id: " + id + " not found.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating user data: " + e.getMessage());
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile image,
                                         @RequestParam("userId") String userId) {
        try {
            User user = userService.getUserById(userId);
            String imageUrl = cloudinaryService.uploadImageForUser(image, user);
            return ResponseEntity.ok(new ImageResponse(imageUrl));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi tải hình ảnh lên");
        }
    }


    class ImageResponse {
        private String secure_url;

        public ImageResponse(String secure_url) {
            this.secure_url = secure_url;
        }

        public String getSecure_url() {
            return secure_url;
        }

        public void setSecure_url(String secure_url) {
            this.secure_url = secure_url;
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        return userService.registerUser(user);
    }

    @GetMapping("/verify/{token}")
    public ResponseEntity<?> verifyUser(@PathVariable String token) {
        if (userService.verifyUser(token)) {
            return ResponseEntity.ok("Tài khoản đã được xác thực thành công! Hãy trở về Website để tiến hành đăng nhập tài khoản!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mã xác thực không hợp lệ hoặc đã hết hạn.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginDetails) {
        String email = loginDetails.get("email");
        String password = loginDetails.get("password");
        return userService.loginUser(email, password);
    }

    @PostMapping("/google-login")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> request) {
        String tokenId = request.get("tokenId");

        try {
            Map<String, Object> userInfo = googleAuthService.getUserInfoFromToken(tokenId);

            if (userInfo != null) {
                String email = (String) userInfo.get("email");
                String name = (String) userInfo.get("name");

                User user = userService.findOrCreateGoogleUser(email, name);

                String jwtToken = jwtUtil.generateToken(user.getId(), user.getUsername());

                return ResponseEntity.ok(Map.of(
                        "token", jwtToken,
                        "message", "Đăng nhập thành công qua Google",
                        "userId", user.getId(),
                        "username", user.getUsername()
                ));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Đăng nhập không thành công.");
        } catch (Exception e) {
            System.err.println("Error during Google login: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Lỗi xác thực với Google: " + e.getMessage());
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        return userService.forgotPassword(email);
    }

    @PostMapping("/reset-password/{token}")
    public ResponseEntity<?> resetPassword(@PathVariable String token, @RequestBody Map<String, String> requestBody) {
        String newPassword = requestBody.get("newPassword");
        return userService.resetPassword(token, newPassword);
    }

    @DeleteMapping("/cleanup")
    public ResponseEntity<?> cleanupUnverifiedUsers() {
        userService.deleteUnverifiedUsers();
        return ResponseEntity.ok("Đã xóa tất cả người dùng không xác thực.");
    }
}