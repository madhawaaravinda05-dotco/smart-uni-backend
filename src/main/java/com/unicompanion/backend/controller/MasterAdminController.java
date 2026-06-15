package com.unicompanion.backend.controller;

import com.unicompanion.backend.dto.MessageResponse;
import com.unicompanion.backend.model.Post;
import com.unicompanion.backend.model.AdminRequest;
import com.unicompanion.backend.model.User;
import com.unicompanion.backend.repository.AdminRequestRepository;
import com.unicompanion.backend.repository.PostRepository;
import com.unicompanion.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/master/admins")
@PreAuthorize("hasRole('MASTER_ADMIN')")
public class MasterAdminController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    AdminRequestRepository adminRequestRepository;

    @GetMapping
    public ResponseEntity<?> getActiveAdmins() {
        List<User> admins = userRepository.findByRole("ROLE_ADMIN");
        return ResponseEntity.ok(admins);
    }

    @PutMapping("/{id}/downgrade")
    public ResponseEntity<?> downgradeAdmin(@PathVariable String id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Admin not found"));
        }

        User user = userOpt.get();
        if (!"ROLE_ADMIN".equals(user.getRole())) {
            return ResponseEntity.badRequest().body(new MessageResponse("User is not a university admin"));
        }

        user.setRole("ROLE_STUDENT");
        userRepository.save(user);

        Optional<AdminRequest> adminRequestOpt = adminRequestRepository.findByUser(user);
        adminRequestOpt.ifPresent(adminRequest -> adminRequestRepository.delete(adminRequest));

        return ResponseEntity.ok(new MessageResponse("Admin successfully downgraded to student."));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAdmin(@PathVariable String id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Admin not found"));
        }

        User user = userOpt.get();
        if (!"ROLE_ADMIN".equals(user.getRole())) {
            return ResponseEntity.badRequest().body(new MessageResponse("User is not a university admin"));
        }

        Optional<AdminRequest> adminRequestOpt = adminRequestRepository.findByUser(user);
        adminRequestOpt.ifPresent(adminRequest -> adminRequestRepository.delete(adminRequest));

        userRepository.delete(user);

        return ResponseEntity.ok(new MessageResponse("Admin account completely deleted."));
    }

    @GetMapping("/{id}/activities")
    public ResponseEntity<?> getAdminActivities(@PathVariable String id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Admin not found"));
        }

        User user = userOpt.get();
        String university = user.getUniversity();

        if (university == null || university.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Admin has no assigned university"));
        }

        List<Post> uniPosts = postRepository.findAll().stream()
                .filter(p -> university.equals(p.getUniversity()))
                .collect(Collectors.toList());

        long pending = uniPosts.stream().filter(p -> "PENDING".equals(p.getStatus())).count();
        long approved = uniPosts.stream().filter(p -> "APPROVED".equals(p.getStatus())).count();
        long rejected = uniPosts.stream().filter(p -> "REJECTED".equals(p.getStatus())).count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("university", university);
        stats.put("totalPosts", uniPosts.size());
        stats.put("pending", pending);
        stats.put("approved", approved);
        stats.put("rejected", rejected);

        return ResponseEntity.ok(stats);
    }
}
