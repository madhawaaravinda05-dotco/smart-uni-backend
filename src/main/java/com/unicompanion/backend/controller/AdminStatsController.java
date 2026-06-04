package com.unicompanion.backend.controller;

import com.unicompanion.backend.dto.PlatformStatsResponse;
import com.unicompanion.backend.model.User;
import com.unicompanion.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin/stats")
public class AdminStatsController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasRole('MASTER_ADMIN')")
    public ResponseEntity<?> getPlatformStats() {
        List<User> users = userRepository.findAll();
        
        long totalStudents = 0;
        long totalAdmins = 0;
        long totalMasterAdmins = 0;
        
        Map<String, PlatformStatsResponse.UniversityStat> uniStats = new HashMap<>();
        
        for (User user : users) {
            String role = user.getRole();
            String uni = user.getUniversity();
            
            if ("ROLE_STUDENT".equals(role)) {
                totalStudents++;
            } else if ("ROLE_ADMIN".equals(role)) {
                totalAdmins++;
            } else if ("ROLE_MASTER_ADMIN".equals(role)) {
                totalMasterAdmins++;
            }
            
            if (uni != null && !uni.trim().isEmpty() && !("ROLE_MASTER_ADMIN".equals(role))) {
                uniStats.putIfAbsent(uni, new PlatformStatsResponse.UniversityStat(0, 0));
                PlatformStatsResponse.UniversityStat stat = uniStats.get(uni);
                if ("ROLE_STUDENT".equals(role)) {
                    stat.setStudents(stat.getStudents() + 1);
                } else if ("ROLE_ADMIN".equals(role)) {
                    stat.setAdmins(stat.getAdmins() + 1);
                }
            }
        }
        
        PlatformStatsResponse response = new PlatformStatsResponse();
        response.setTotalStudents(totalStudents);
        response.setTotalAdmins(totalAdmins);
        response.setTotalMasterAdmins(totalMasterAdmins);
        response.setActiveUnis(uniStats.size());
        response.setUniversityStats(uniStats);
        
        return ResponseEntity.ok(response);
    }
}
