package com.unicompanion.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "adminRequests")
public class AdminRequest {

    @Id
    private String id;
    
    @DBRef
    private User user;
    
    private String name;
    private String email;
    private String university;
    private String area;
    private Integer yearOfStudy;
    
    private String idCardUrl;
    
    private String status; // PENDING, APPROVED, REJECTED
    
    private Instant createdAt;

    public AdminRequest() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getUniversity() { return university; }
    public void setUniversity(String university) { this.university = university; }
    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }
    public Integer getYearOfStudy() { return yearOfStudy; }
    public void setYearOfStudy(Integer yearOfStudy) { this.yearOfStudy = yearOfStudy; }
    public String getIdCardUrl() { return idCardUrl; }
    public void setIdCardUrl(String idCardUrl) { this.idCardUrl = idCardUrl; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
