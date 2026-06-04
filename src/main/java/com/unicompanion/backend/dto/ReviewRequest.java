package com.unicompanion.backend.dto;

public class ReviewRequest {
    private Double rating;
    private String comment;

    public ReviewRequest() {}

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
