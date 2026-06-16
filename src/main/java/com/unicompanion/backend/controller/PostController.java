package com.unicompanion.backend.controller;

import com.unicompanion.backend.dto.MessageResponse;
import com.unicompanion.backend.dto.ReviewRequest;
import com.unicompanion.backend.dto.StatusUpdateRequest;
import com.unicompanion.backend.model.Post;
import com.unicompanion.backend.model.Review;
import com.unicompanion.backend.model.User;
import com.unicompanion.backend.repository.PostRepository;
import com.unicompanion.backend.repository.ReviewRepository;
import com.unicompanion.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.unicompanion.backend.service.FileStorageService;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    FileStorageService fileStorageService;

    @PostMapping("/upload-images")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') or hasRole('MASTER_ADMIN')")
    public ResponseEntity<?> uploadPostImages(@RequestParam("images") MultipartFile[] images) {
        try {
            java.util.List<String> fileUrls = new java.util.ArrayList<>();
            for (MultipartFile file : images) {
                fileUrls.add(fileStorageService.storeFile(file));
            }
            return ResponseEntity.ok(fileUrls);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error uploading images: " + e.getMessage()));
        }
    }

    @GetMapping("/active/{university}")
    public ResponseEntity<?> getActivePosts(@PathVariable String university,
                                            @RequestParam(required = false) String category) {
        String decodedUni = URLDecoder.decode(university, StandardCharsets.UTF_8);
        List<Post> posts;
        if (category != null && !category.isEmpty()) {
            System.out.println("Fetching active posts for uni: '" + decodedUni + "', category: '" + category + "'");
            posts = postRepository.findByUniversityAndStatusAndCategory(decodedUni, "APPROVED", category.trim());
            System.out.println("Found: " + posts.size());
        } else {
            posts = postRepository.findByUniversityAndStatus(decodedUni, "APPROVED");
        }
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/pending/{university}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MASTER_ADMIN')")
    public ResponseEntity<?> getPendingPosts(@PathVariable String university) {
        String decodedUni = URLDecoder.decode(university, StandardCharsets.UTF_8);
        List<Post> posts = postRepository.findByUniversityAndStatus(decodedUni, "PENDING");
        return ResponseEntity.ok(posts);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPost(@RequestBody Post postRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> userOpt = userRepository.findByEmail(auth.getName());
        if (!userOpt.isPresent()) return ResponseEntity.badRequest().body(new MessageResponse("User not found"));
        
        User user = userOpt.get();

        // Enforce pricing tier (max 2 free Boarding or Food posts lifetime)
        boolean isBoardingOrFood = "BOARDING".equalsIgnoreCase(postRequest.getCategory()) || "FOOD".equalsIgnoreCase(postRequest.getCategory());
        if (isBoardingOrFood && !postRequest.isPremium()) {
            int used = user.getFreePostsUsed() != null ? user.getFreePostsUsed() : 0;
            
            // Auto-initialize for older users who don't have the counter set yet
            if (used == 0 && user.getFreePostsUsed() == null) {
                long existingCount = postRepository.findByPostedById(user.getId()).stream()
                    .filter(p -> "BOARDING".equalsIgnoreCase(p.getCategory()) || "FOOD".equalsIgnoreCase(p.getCategory()))
                    .count();
                used = (int) existingCount;
                user.setFreePostsUsed(used);
                userRepository.save(user);
            }
            
            if (used >= 2) {
                return ResponseEntity.status(403).body(new MessageResponse("PAYMENT_REQUIRED"));
            } else {
                // Increment free post usage
                user.setFreePostsUsed(used + 1);
                userRepository.save(user);
            }
        }

        postRequest.setPostedBy(user);
        postRequest.setStatus("PENDING");
        postRequest.setCreatedAt(Instant.now());
        postRequest.setReportCount(0);
        postRequest.setVerified(false);

        postRepository.save(postRequest);
        return ResponseEntity.ok(new MessageResponse("Post created successfully!"));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MASTER_ADMIN')")
    public ResponseEntity<?> updatePostStatus(@PathVariable String id, @RequestBody StatusUpdateRequest request) {
        Optional<Post> postOpt = postRepository.findById(id);
        if (postOpt.isEmpty()) return ResponseEntity.badRequest().body(new MessageResponse("Post not found"));

        Post post = postOpt.get();
        post.setStatus(request.getStatus());
        postRepository.save(post);

        return ResponseEntity.ok(new MessageResponse("Status updated to " + request.getStatus()));
    }



    @PostMapping("/{postId}/reviews")
    public ResponseEntity<?> addReview(@PathVariable String postId, @RequestBody ReviewRequest reviewRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> userOpt = userRepository.findByEmail(auth.getName());
        if (!userOpt.isPresent()) return ResponseEntity.badRequest().body(new MessageResponse("User not found"));

        Optional<Post> postOpt = postRepository.findById(postId);
        if (postOpt.isEmpty()) return ResponseEntity.badRequest().body(new MessageResponse("Post not found"));

        Review review = new Review();
        review.setPost(postOpt.get());
        review.setUser(userOpt.get());
        review.setRating(reviewRequest.getRating());
        review.setComment(reviewRequest.getComment());
        review.setCreatedAt(Instant.now());

        reviewRepository.save(review);
        return ResponseEntity.ok(new MessageResponse("Review added successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MASTER_ADMIN') or hasRole('STUDENT')")
    public ResponseEntity<?> deletePost(@PathVariable String id) {
        Optional<Post> postOpt = postRepository.findById(id);
        if (postOpt.isEmpty()) return ResponseEntity.badRequest().body(new MessageResponse("Post not found"));

        Post post = postOpt.get();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> userOpt = userRepository.findByEmail(auth.getName());
        
        if (userOpt.isEmpty()) return ResponseEntity.badRequest().body(new MessageResponse("User not found"));
        User user = userOpt.get();

        // Check permissions: Owner, or Admin of the same university, or Master Admin
        boolean isOwner = post.getPostedBy().getId().equals(user.getId());
        boolean isCampusAdmin = "ROLE_ADMIN".equals(user.getRole()) && user.getUniversity().equals(post.getUniversity());
        boolean isMasterAdmin = "ROLE_MASTER_ADMIN".equals(user.getRole());

        if (!isOwner && !isCampusAdmin && !isMasterAdmin) {
            return ResponseEntity.status(403).body(new MessageResponse("You don't have permission to delete this post."));
        }

        postRepository.delete(post);
        return ResponseEntity.ok(new MessageResponse("Post deleted successfully"));
    }
}
