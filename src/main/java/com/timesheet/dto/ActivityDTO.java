package com.timesheet.dto;

import java.time.LocalDateTime;

public class ActivityDTO {
    private String icon;
    private String iconColor;
    private String title;
    private String time;
    private LocalDateTime timestamp;

    // Constructors
    public ActivityDTO() {}

    public ActivityDTO(String icon, String iconColor, String title, String time, LocalDateTime timestamp) {
        this.icon = icon;
        this.iconColor = iconColor;
        this.title = title;
        this.time = time;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getIconColor() { return iconColor; }
    public void setIconColor(String iconColor) { this.iconColor = iconColor; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
