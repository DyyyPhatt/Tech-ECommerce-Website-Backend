//package hcmute.tech_ecommerce_website.util;
//
//import io.jsonwebtoken.*;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.stereotype.Component;
//
//import javax.crypto.SecretKey;
//import java.util.Date;
//
//@Component
//public class JwtUtil {
//
//    private final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);
//    private final long EXPIRATION_TIME = 3600000; // 1 giờ
//
//    // Tạo JWT với username và userId
//    public String generateToken(String userId, String username) {
//        return Jwts.builder()
//                .setSubject(username)  // Username làm subject
//                .claim("id", userId)   // Thêm userId vào claim
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Token hết hạn sau 1 giờ
//                .signWith(SECRET_KEY, SignatureAlgorithm.HS512)
//                .compact();
//    }
//
//    // Lấy thông tin username từ token
//    public String extractUsername(String token) {
//        try {
//            return Jwts.parserBuilder()
//                    .setSigningKey(SECRET_KEY)
//                    .build()
//                    .parseClaimsJws(token)
//                    .getBody()
//                    .getSubject();
//        } catch (JwtException e) {
//            return null; // Token không hợp lệ
//        }
//    }
//
//    // Lấy thông tin userId từ token
//    public String extractUserId(String token) {
//        try {
//            return Jwts.parserBuilder()
//                    .setSigningKey(SECRET_KEY)
//                    .build()
//                    .parseClaimsJws(token)
//                    .getBody()
//                    .get("id", String.class);
//        } catch (JwtException e) {
//            return null; // Token không hợp lệ
//        }
//    }
//
//    // Kiểm tra token có hợp lệ hay không
//    public boolean isTokenValid(String token) {
//        try {
//            Claims claims = Jwts.parserBuilder()
//                    .setSigningKey(SECRET_KEY)
//                    .build()
//                    .parseClaimsJws(token)
//                    .getBody();
//            return !isTokenExpired(claims);
//        } catch (JwtException e) {
//            return false; // Token không hợp lệ
//        }
//    }
//
//    // Kiểm tra token hết hạn
//    private boolean isTokenExpired(Claims claims) {
//        Date expiration = claims.getExpiration();
//        return expiration.before(new Date());
//    }
//}


package hcmute.tech_ecommerce_website.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    private final long EXPIRATION_TIME = 3600000;

    public String generateToken(String userId, String username) {
        return Jwts.builder()
                .setSubject(username)
                .claim("id", userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateEmployeeToken(String employeeId, String username) {
        return Jwts.builder()
                .setSubject(username)
                .claim("id", employeeId)
                .claim("type", "Employee")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS512)
                .compact();
    }

    public String extractUsername(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (JwtException e) {
            return null;
        }
    }

    public String extractUserId(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("id", String.class);
        } catch (JwtException e) {
            return null; // Token không hợp lệ
        }
    }

    public String extractType(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("type", String.class);
        } catch (JwtException e) {
            return null;
        }
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return !isTokenExpired(claims);
        } catch (JwtException e) {
            return false;
        }
    }

    private boolean isTokenExpired(Claims claims) {
        Date expiration = claims.getExpiration();
        return expiration.before(new Date());
    }
}
