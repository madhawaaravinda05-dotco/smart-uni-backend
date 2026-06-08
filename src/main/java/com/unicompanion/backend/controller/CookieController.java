package com.unicompanion.backend.controller;

import com.unicompanion.backend.dto.MessageResponse;
import com.unicompanion.backend.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/cookies")
public class CookieController {

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/clear")
    public ResponseEntity<?> clearCookies() {
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new MessageResponse("Cookies cleared successfully!"));
    }
}
