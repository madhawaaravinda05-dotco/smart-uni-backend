package com.unicompanion.backend.dto;

public class ReportRequest {
    private String reason;
    private String message;

    public ReportRequest() {}

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
