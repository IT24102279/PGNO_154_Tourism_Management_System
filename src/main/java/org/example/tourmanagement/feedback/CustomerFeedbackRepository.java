package org.example.tourmanagement.feedback;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerFeedbackRepository extends JpaRepository<CustomerFeedback, Long> {
    
    // Find approved feedback for display
    @Query("SELECT f FROM CustomerFeedback f WHERE f.isApproved = true ORDER BY f.createdAt DESC")
    List<CustomerFeedback> findApprovedFeedback();
    
    // Find recent approved feedback (for homepage display)
    @Query("SELECT f FROM CustomerFeedback f WHERE f.isApproved = true ORDER BY f.createdAt DESC")
    List<CustomerFeedback> findRecentApprovedFeedback();
    
    // Find feedback by email
    List<CustomerFeedback> findByEmail(String email);
    
    // Find feedback by rating
    List<CustomerFeedback> findByRating(Integer rating);
    
    // Count total feedback
    long count();
    
    // Count approved feedback
    long countByIsApprovedTrue();
    
    // Calculate average rating
    @Query("SELECT AVG(f.rating) FROM CustomerFeedback f WHERE f.isApproved = true AND f.rating IS NOT NULL")
    Double getAverageRating();
}
