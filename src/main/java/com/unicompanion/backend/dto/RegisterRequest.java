package com.unicompanion.backend.dto;

public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private String university;
    private String area;
    private Integer yearOfStudy;
    private String role;

    public RegisterRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getUniversity() { return university; }
    public void setUniversity(String university) { this.university = university; }
    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }
    public Integer getYearOfStudy() { return yearOfStudy; }
    public void setYearOfStudy(Integer yearOfStudy) { this.yearOfStudy = yearOfStudy; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
