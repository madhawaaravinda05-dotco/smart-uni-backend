package com.unicompanion.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.util.Base64;
import java.util.Map;

@Service
public class FileStorageService {

    @Value("${app.imgbb.key}")
    private String imgbbApiKey;

    public String storeFile(MultipartFile file) {
        try {
            if (imgbbApiKey == null || imgbbApiKey.isEmpty()) {
                throw new RuntimeException("ImgBB API key is not configured");
            }

            RestTemplate restTemplate = new RestTemplate();
            String url = "https://api.imgbb.com/1/upload?key=" + imgbbApiKey;

            // Convert MultipartFile to Base64 string for ImgBB API
            String base64Image = Base64.getEncoder().encodeToString(file.getBytes());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("image", base64Image);

            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
                return (String) data.get("display_url");
            } else {
                throw new RuntimeException("ImgBB API upload failed");
            }
        } catch (Exception ex) {
            throw new RuntimeException("Could not upload file to ImgBB. " + ex.getMessage(), ex);
        }
    }
}
