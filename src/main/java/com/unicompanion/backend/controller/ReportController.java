package com.unicompanion.backend.controller;

import com.unicompanion.backend.dto.MessageResponse;
import com.unicompanion.backend.dto.ReportRequest;
import com.unicompanion.backend.dto.StatusUpdateRequest;
import com.unicompanion.backend.model.Post;
import com.unicompanion.backend.model.Report;
import com.unicompanion.backend.model.User;
import com.unicompanion.backend.repository.PostRepository;
import com.unicompanion.backend.repository.ReportRepository;
import com.unicompanion.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ReportRepository reportRepository;

    @PostMapping("/{postId}")
    public ResponseEntity<?> submitReport(@PathVariable String postId, @RequestBody ReportRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> userOpt = userRepository.findByEmail(auth.getName());
        if (userOpt.isEmpty()) return ResponseEntity.badRequest().body(new MessageResponse("User not found"));

        Optional<Post> postOpt = postRepository.findById(postId);
        if (postOpt.isEmpty()) return ResponseEntity.badRequest().body(new MessageResponse("Post not found"));

        Post post = postOpt.get();
        int count = post.getReportCount() == null ? 0 : post.getReportCount();
        post.setReportCount(count + 1);

        if (post.getReportCount() >= 5) {
            post.setStatus("PENDING"); // Auto hide
        }
        postRepository.save(post);

        Report report = new Report();
        report.setPost(post);
        report.setReportedBy(userOpt.get());
        report.setUniversity(post.getUniversity());
        report.setReason(request.getReason() != null ? request.getReason() : "Other");
        report.setMessage(request.getMessage());
        report.setStatus("PENDING");
        report.setCreatedAt(Instant.now());
        reportRepository.save(report);

        return ResponseEntity.ok(new MessageResponse("Post reported successfully"));
    }

    @GetMapping("/university/{university}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MASTER_ADMIN')")
    public ResponseEntity<?> getAdminReports(@PathVariable String university) {
        String decodedUni = URLDecoder.decode(university, StandardCharsets.UTF_8);
        List<Report> reports = reportRepository.findByUniversityAndStatusOrderByCreatedAtDesc(decodedUni, "PENDING");
        return ResponseEntity.ok(reports);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MASTER_ADMIN')")
    public ResponseEntity<?> updateReportStatus(@PathVariable String id, @RequestBody StatusUpdateRequest request) {
        Optional<Report> reportOpt = reportRepository.findById(id);
        if (reportOpt.isEmpty()) return ResponseEntity.badRequest().body(new MessageResponse("Report not found"));

        Report report = reportOpt.get();
        report.setStatus(request.getStatus());
        reportRepository.save(report);

        return ResponseEntity.ok(new MessageResponse("Report status updated to " + request.getStatus()));
    }
}
