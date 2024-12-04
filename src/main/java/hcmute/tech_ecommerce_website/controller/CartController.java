package hcmute.tech_ecommerce_website.controller;

import hcmute.tech_ecommerce_website.model.Cart;
import hcmute.tech_ecommerce_website.service.CartService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/sync/{userId}")
    public ResponseEntity<?> syncCart(
            @PathVariable String userId,
            @RequestBody List<Cart.Item> cartItems) {
        try {
            ObjectId userObjectId = new ObjectId(userId);
            Cart updatedCart = cartService.syncCart(userObjectId, cartItems);
            return ResponseEntity.ok(updatedCart);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Định dạng ID không hợp lệ hoặc lỗi khi đồng bộ giỏ hàng.");
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getCartByUserId(@PathVariable String userId) {
        try {
            ObjectId userObjectId = new ObjectId(userId);
            Optional<Cart> cart = cartService.getCartByUserId(userObjectId);

            if (cart.isPresent()) {
                return ResponseEntity.ok(cart.get());
            } else {
                return ResponseEntity.status(404).body("Giỏ hàng không tồn tại.");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("ID người dùng không hợp lệ.");
        }
    }

    @PostMapping("/add/{userId}")
    public ResponseEntity<?> addItemToCart(
            @PathVariable String userId,
            @RequestParam String productId,
            @RequestParam int quantity,
            @RequestParam String color) {
        try {
            ObjectId userObjectId = new ObjectId(userId);
            ObjectId productObjectId = new ObjectId(productId);

            Cart updatedCart = cartService.addItemToCart(userObjectId, productObjectId, quantity, color);
            return ResponseEntity.ok(updatedCart);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Định dạng ID không hợp lệ.");
        }
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<?> updateCartItem(
            @PathVariable String userId,
            @RequestParam String productId,
            @RequestParam int quantity,
            @RequestParam String color) {
        try {
            ObjectId userObjectId = new ObjectId(userId);
            ObjectId productObjectId = new ObjectId(productId);

            Cart updatedCart = cartService.updateCartItem(userObjectId, productObjectId, quantity, color);
            return ResponseEntity.ok(updatedCart);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Định dạng ID không hợp lệ hoặc không tìm thấy giỏ hàng.");
        }
    }

    @DeleteMapping("/remove/{userId}")
    public ResponseEntity<?> removeItemFromCart(
            @PathVariable String userId,
            @RequestParam String productId) {
        try {
            ObjectId userObjectId = new ObjectId(userId);
            ObjectId productObjectId = new ObjectId(productId);

            Cart updatedCart = cartService.removeItemFromCart(userObjectId, productObjectId);
            return ResponseEntity.ok(updatedCart);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Định dạng ID không hợp lệ hoặc không tìm thấy giỏ hàng.");
        }
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<?> deleteCartByUserId(@PathVariable String userId) {
        try {
            ObjectId userObjectId = new ObjectId(userId);
            cartService.deleteCartByUserId(userObjectId);
            return ResponseEntity.ok("Xóa giỏ hàng thành công.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("ID người dùng không hợp lệ.");
        }
    }

    @PutMapping("/clear/{userId}")
    public ResponseEntity<?> clearCart(@PathVariable String userId) {
        try {
            ObjectId userObjectId = new ObjectId(userId);
            Cart clearedCart = cartService.clearCart(userObjectId);

            if (clearedCart != null) {
                return ResponseEntity.ok(clearedCart);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Giỏ hàng không tìm thấy.");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Định dạng ID không hợp lệ.");
        }
    }
}