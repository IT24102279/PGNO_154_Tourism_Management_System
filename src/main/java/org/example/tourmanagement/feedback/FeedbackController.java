package org.example.tourmanagement.feedback;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Controller
public class FeedbackController {
    
    private final CustomerFeedbackRepository feedbackRepository;
    
    public FeedbackController(CustomerFeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }
    
    // Handle feedback submission from homepage
    @PostMapping("/feedback/submit")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> submitFeedback(@Valid @RequestBody FeedbackForm form, BindingResult result) {
        Map<String, Object> response = new HashMap<>();
        
        if (result.hasErrors()) {
            response.put("success", false);
            response.put("message", "Please fill in all required fields correctly.");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            CustomerFeedback feedback = new CustomerFeedback();
            feedback.setName(form.getName());
            feedback.setEmail(form.getEmail());
            feedback.setMessage(form.getMessage());
            feedback.setRating(form.getRating());
            feedback.setIsApproved(true); // Auto-approve feedback for immediate display
            
            CustomerFeedback savedFeedback = feedbackRepository.save(feedback);
            
            response.put("success", true);
            response.put("message", "Thank you for your feedback! We appreciate your input.");
            response.put("feedbackId", savedFeedback.getId());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "An error occurred while submitting your feedback. Please try again.");
            return ResponseEntity.status(500).body(response);
        }
    }
    
    // Get approved feedback for display (AJAX endpoint)
    @GetMapping("/feedback/approved")
    @ResponseBody
    public ResponseEntity<List<CustomerFeedback>> getApprovedFeedback() {
        List<CustomerFeedback> approvedFeedback = feedbackRepository.findApprovedFeedback();
        return ResponseEntity.ok(approvedFeedback);
    }
    
    // Update feedback by ID
    @PutMapping("/feedback/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateFeedback(@PathVariable Long id, @Valid @RequestBody FeedbackForm form, BindingResult result) {
        Map<String, Object> response = new HashMap<>();
        
        if (result.hasErrors()) {
            response.put("success", false);
            response.put("message", "Please fill in all required fields correctly.");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            CustomerFeedback feedback = feedbackRepository.findById(id).orElse(null);
            if (feedback == null) {
                response.put("success", false);
                response.put("message", "Feedback not found.");
                return ResponseEntity.notFound().build();
            }
            
            feedback.setName(form.getName());
            feedback.setEmail(form.getEmail());
            feedback.setMessage(form.getMessage());
            feedback.setRating(form.getRating());
            
            feedbackRepository.save(feedback);
            
            response.put("success", true);
            response.put("message", "Feedback updated successfully!");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "An error occurred while updating your feedback. Please try again.");
            return ResponseEntity.status(500).body(response);
        }
    }
    
    // Delete feedback by ID
    @DeleteMapping("/feedback/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteFeedback(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            CustomerFeedback feedback = feedbackRepository.findById(id).orElse(null);
            if (feedback == null) {
                response.put("success", false);
                response.put("message", "Feedback not found.");
                return ResponseEntity.notFound().build();
            }
            
            feedbackRepository.delete(feedback);
            
            response.put("success", true);
            response.put("message", "Feedback deleted successfully!");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "An error occurred while deleting your feedback. Please try again.");
            return ResponseEntity.status(500).body(response);
        }
    }
    
    // Feedback form class for validation
    public static class FeedbackForm {
        @jakarta.validation.constraints.NotBlank(message = "Name is required")
        private String name;
        
        @jakarta.validation.constraints.NotBlank(message = "Email is required")
        @jakarta.validation.constraints.Email(message = "Please provide a valid email")
        private String email;
        
        @jakarta.validation.constraints.NotBlank(message = "Message is required")
        @jakarta.validation.constraints.Size(max = 1000, message = "Message must be less than 1000 characters")
        private String message;
        
        @jakarta.validation.constraints.Min(value = 1, message = "Rating must be at least 1")
        @jakarta.validation.constraints.Max(value = 5, message = "Rating must be at most 5")
        private Integer rating;
        
        // Constructors
        public FeedbackForm() {}
        
        public FeedbackForm(String name, String email, String message, Integer rating) {
            this.name = name;
            this.email = email;
            this.message = message;
            this.rating = rating;
        }
        
        // Getters and Setters
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
    }
}
