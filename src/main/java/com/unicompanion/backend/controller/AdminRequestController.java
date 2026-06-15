package com.unicompanion.backend.controller;

import com.unicompanion.backend.dto.MessageResponse;
import com.unicompanion.backend.model.AdminRequest;
import com.unicompanion.backend.model.User;
import com.unicompanion.backend.repository.AdminRequestRepository;
import com.unicompanion.backend.repository.UserRepository;
import com.unicompanion.backend.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin/requests")
public class AdminRequestController {

    @Autowired
    AdminRequestRepository adminRequestRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    FileStorageService fileStorageService;

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> submitAdminRequest(
            @RequestParam("idCard") MultipartFile idCard,
            @RequestParam("university") String university,
            @RequestParam("yearOfStudy") Integer yearOfStudy) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> userOpt = userRepository.findByEmail(auth.getName());
        if (userOpt.isEmpty()) return ResponseEntity.badRequest().body(new MessageResponse("User not found"));

        User user = userOpt.get();

        // Check if request already exists
        Optional<AdminRequest> existingRequest = adminRequestRepository.findByUser(user);
        if (existingRequest.isPresent() && !existingRequest.get().getStatus().equals("REJECTED")) {
            return ResponseEntity.badRequest().body(new MessageResponse("You already have a pending or approved request."));
        }

        String fileUrl = fileStorageService.storeFile(idCard);

        AdminRequest adminRequest = new AdminRequest();
        adminRequest.setUser(user);
        adminRequest.setName(user.getName());
        adminRequest.setEmail(user.getEmail());
        adminRequest.setUniversity(university);
        adminRequest.setArea(user.getArea());
        adminRequest.setYearOfStudy(yearOfStudy);
        adminRequest.setIdCardUrl(fileUrl);
        adminRequest.setStatus("PENDING");
        adminRequest.setCreatedAt(Instant.now());

        adminRequestRepository.save(adminRequest);

        return ResponseEntity.ok(new MessageResponse("Admin request submitted successfully"));
    }

    @GetMapping("/my-status")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getMyAdminRequestStatus() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> userOpt = userRepository.findByEmail(auth.getName());
        if (userOpt.isEmpty()) return ResponseEntity.badRequest().body(new MessageResponse("User not found"));

        Optional<AdminRequest> requestOpt = adminRequestRepository.findByUser(userOpt.get());
        Map<String, String> response = new HashMap<>();
        if (requestOpt.isPresent()) {
            if (requestOpt.get().getStatus().equals("APPROVED") && "ROLE_STUDENT".equals(userOpt.get().getRole())) {
                adminRequestRepository.delete(requestOpt.get());
                response.put("status", "NONE");
            } else {
                response.put("status", requestOpt.get().getStatus());
            }
        } else {
            response.put("status", "NONE");
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('MASTER_ADMIN')")
    public ResponseEntity<?> getAdminRequests() {
        List<AdminRequest> requests = adminRequestRepository.findAll();
        return ResponseEntity.ok(requests);
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('MASTER_ADMIN')")
    public ResponseEntity<?> approveAdminRequest(@PathVariable String id) {
        Optional<AdminRequest> requestOpt = adminRequestRepository.findById(id);
        if (requestOpt.isEmpty()) return ResponseEntity.badRequest().body(new MessageResponse("Request not found"));

        AdminRequest request = requestOpt.get();
        request.setStatus("APPROVED");
        adminRequestRepository.save(request);

        User user = request.getUser();
        user.setRole("ROLE_ADMIN");
        user.setUniversity(request.getUniversity()); // update to new assigned university
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Request approved successfully"));
    }
}
