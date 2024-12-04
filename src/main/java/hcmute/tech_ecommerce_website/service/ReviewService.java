package hcmute.tech_ecommerce_website.service;

import hcmute.tech_ecommerce_website.model.Product;
import hcmute.tech_ecommerce_website.model.Review;
import hcmute.tech_ecommerce_website.repository.ProductRepository;
import hcmute.tech_ecommerce_website.repository.ReviewRepository;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    public ReviewService(ReviewRepository reviewRepository, ProductRepository productRepository) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
    }

    private void updateProductRatings(ObjectId productId) {
        List<Review> reviews = reviewRepository.findByProduct(productId);
        double averageRating = reviews.stream().mapToDouble(Review::getRating).average().orElse(0.0);
        int totalReviews = reviews.size();

        productRepository.findById(productId.toString()).ifPresent(product -> {
            Product.Ratings ratings = product.getRatings();
            if (ratings == null) {
                ratings = new Product.Ratings();
            }
            ratings.setAverage(averageRating);
            ratings.setTotalReviews(totalReviews);
            product.setRatings(ratings);
            productRepository.save(product);
        });
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public List<Review> getReviewsByUser(ObjectId userId) {
        return reviewRepository.findByUser(userId);
    }

    public List<Review> getReviewsByProduct(ObjectId productId) {
        return reviewRepository.findByProduct(productId);
    }

    public Optional<Review> getReviewById(ObjectId id) {
        return reviewRepository.findById(id);
    }

    public Review addReview(Review review) {
        Review savedReview = reviewRepository.save(review);
        updateProductRatings(review.getProduct());
        return savedReview;
    }

    public Optional<Review> updateReview(ObjectId id, Review updatedReview) {
        return reviewRepository.findById(id).map(existingReview -> {
            existingReview.setRating(updatedReview.getRating());
            existingReview.setComment(updatedReview.getComment());
            Review savedReview = reviewRepository.save(existingReview);
            updateProductRatings(existingReview.getProduct());
            return savedReview;
        });
    }

    public boolean deleteReview(ObjectId id) {
        Optional<Review> reviewOptional = reviewRepository.findById(id);
        if (reviewOptional.isPresent()) {
            Review review = reviewOptional.get();
            reviewRepository.deleteById(id);
            updateProductRatings(review.getProduct());
            return true;
        }
        return false;
    }
}