package com.unicompanion.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Document(collection = "users")
public class User {

    @Id
    private String id;

    @NotBlank(message = "Name is required")
    @Size(min = 3, message = "Name must be at least 3 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Indexed(unique = true)
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Role is required")
    private String role; // ROLE_STUDENT, ROLE_ADMIN, ROLE_MASTER_ADMIN

    @NotBlank(message = "University is required")
    private String university;

    private String area;

    private Integer yearOfStudy; // Can be null for Admins/Master Admins

    private Boolean isApprovedByMaster; // true if admin request approved, default true for students

    public User() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getUniversity() { return university; }
    public void setUniversity(String university) { this.university = university; }
    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }
    public Integer getYearOfStudy() { return yearOfStudy; }
    public void setYearOfStudy(Integer yearOfStudy) { this.yearOfStudy = yearOfStudy; }
    public Boolean getIsApprovedByMaster() { return isApprovedByMaster; }
    public void setIsApprovedByMaster(Boolean isApprovedByMaster) { this.isApprovedByMaster = isApprovedByMaster; }
}
