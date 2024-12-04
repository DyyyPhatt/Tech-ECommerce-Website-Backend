package hcmute.tech_ecommerce_website.service;

import hcmute.tech_ecommerce_website.model.Cart;
import hcmute.tech_ecommerce_website.model.Cart.Item;
import hcmute.tech_ecommerce_website.repository.CartRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    public Cart createCartForUser(ObjectId userId) {
        Cart cart = new Cart();
        cart.setUser(userId);
        cart.setCreatedAt(new Date());
        return cartRepository.save(cart);
    }

    public Optional<Cart> getCartByUserId(ObjectId userId) {
        return cartRepository.findByUser(userId);
    }

    public Cart addItemToCart(ObjectId userId, ObjectId productId, int quantity, String color) {
        Optional<Cart> cartOptional = cartRepository.findByUser(userId);
        Cart cart = cartOptional.orElseGet(() -> createCartForUser(userId));

        boolean productExists = false;
        for (Item item : cart.getItems()) {
            if (item.getProduct().equals(productId) && item.getColor().equals(color)) {
                item.setQuantity(item.getQuantity() + quantity);
                productExists = true;
                break;
            }
        }

        if (!productExists) {
            Item newItem = new Item();
            newItem.setProduct(productId);
            newItem.setQuantity(quantity);
            newItem.setColor(color);
            cart.getItems().add(newItem);
        }

        return cartRepository.save(cart);
    }

    public Cart updateCartItem(ObjectId userId, ObjectId productId, int quantity, String color) {
        Optional<Cart> cartOptional = cartRepository.findByUser(userId);

        if (cartOptional.isPresent()) {
            Cart cart = cartOptional.get();

            for (Item item : cart.getItems()) {
                if (item.getProduct().equals(productId)) {
                    if (!item.getColor().equals(color)) {
                        item.setColor(color);
                    }
                    if (item.getQuantity() != quantity) {
                        item.setQuantity(quantity);
                    }
                    break;
                }
            }
            return cartRepository.save(cart);
        }
        throw new IllegalArgumentException("Không tìm thấy giỏ hàng cho người dùng: " + userId);
    }

    public Cart removeItemFromCart(ObjectId userId, ObjectId productId) {
        Optional<Cart> cartOptional = cartRepository.findByUser(userId);

        if (cartOptional.isPresent()) {
            Cart cart = cartOptional.get();

            cart.getItems().removeIf(item -> item.getProduct().equals(productId));

            return cartRepository.save(cart);
        }

        throw new IllegalArgumentException("Không tìm thấy giỏ hàng cho người dùng: " + userId);
    }

    public void deleteCartByUserId(ObjectId userId) {
        cartRepository.deleteByUser(userId);
    }

    public Cart syncCart(ObjectId userId, List<Item> cartItems) {
        Optional<Cart> cartOptional = cartRepository.findByUser(userId);
        Cart cart = cartOptional.orElseGet(() -> createCartForUser(userId));

        cart.setItems(cartItems);

        return cartRepository.save(cart);
    }

    public Cart clearCart(ObjectId userId) {
        Optional<Cart> cartOptional = cartRepository.findByUser(userId);
        if (cartOptional.isPresent()) {
            Cart cart = cartOptional.get();
            cart.setItems(new ArrayList<>());
            return cartRepository.save(cart);
        }
        return null;
    }
}