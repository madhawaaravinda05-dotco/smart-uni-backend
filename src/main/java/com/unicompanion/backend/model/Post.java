package com.unicompanion.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "posts")
public class Post {

    @Id
    private String id;

    private String category; // BOARDING, FOOD, TRANSPORT
    private String title;
    private String description;
    private String area;
    private String university;
    
    private String status; // PENDING, APPROVED, REJECTED
    
    private Location location;
    private List<String> images;

    @DBRef
    private User postedBy;
    
    private Instant createdAt;

    // --- BOARDING specific fields ---
    private Double price;
    private String genderType;
    private Boolean hasKitchen;
    private String contact;
    private Double distance; // might be calculated, but frontend expects it
    private Boolean verified;

    // --- FOOD specific fields ---
    private String priceRange;
    private List<String> tags;
    private Double rating;
    private String openHours;

    // --- TRANSPORT specific fields ---
    private String routeNumber;
    private String fromLocation;
    private String toLocation;
    private String frequency;
    private String lastBus;

    private Integer reportCount; // auto hide after 5

    public Post() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }
    public String getUniversity() { return university; }
    public void setUniversity(String university) { this.university = university; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }
    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }
    public User getPostedBy() { return postedBy; }
    public void setPostedBy(User postedBy) { this.postedBy = postedBy; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public String getGenderType() { return genderType; }
    public void setGenderType(String genderType) { this.genderType = genderType; }
    public Boolean getHasKitchen() { return hasKitchen; }
    public void setHasKitchen(Boolean hasKitchen) { this.hasKitchen = hasKitchen; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    public Double getDistance() { return distance; }
    public void setDistance(Double distance) { this.distance = distance; }
    public Boolean getVerified() { return verified; }
    public void setVerified(Boolean verified) { this.verified = verified; }
    public String getPriceRange() { return priceRange; }
    public void setPriceRange(String priceRange) { this.priceRange = priceRange; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
    public String getOpenHours() { return openHours; }
    public void setOpenHours(String openHours) { this.openHours = openHours; }
    public String getRouteNumber() { return routeNumber; }
    public void setRouteNumber(String routeNumber) { this.routeNumber = routeNumber; }
    public String getFromLocation() { return fromLocation; }
    public void setFromLocation(String fromLocation) { this.fromLocation = fromLocation; }
    public String getToLocation() { return toLocation; }
    public void setToLocation(String toLocation) { this.toLocation = toLocation; }
    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }
    public String getLastBus() { return lastBus; }
    public void setLastBus(String lastBus) { this.lastBus = lastBus; }
    public Integer getReportCount() { return reportCount; }
    public void setReportCount(Integer reportCount) { this.reportCount = reportCount; }
}
