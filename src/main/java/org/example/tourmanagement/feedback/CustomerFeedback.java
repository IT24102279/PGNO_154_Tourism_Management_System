package org.example.tourmanagement.feedback;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_feedback")
public class CustomerFeedback {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String email;
    
    @Column(nullable = false, length = 1000)
    private String message;
    
    @Column(name = "rating")
    private Integer rating; // 1-5 star rating
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "is_approved")
    private Boolean isApproved = false; // For admin moderation
    
    // Constructors
    public CustomerFeedback() {
        this.createdAt = LocalDateTime.now();
    }
    
    public CustomerFeedback(String name, String email, String message, Integer rating) {
        this();
        this.name = name;
        this.email = email;
        this.message = message;
        this.rating = rating;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Integer getRating() {
        return rating;
    }
    
    public void setRating(Integer rating) {
        this.rating = rating;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public Boolean getIsApproved() {
        return isApproved;
    }
    
    public void setIsApproved(Boolean isApproved) {
        this.isApproved = isApproved;
    }
}
