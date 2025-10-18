package org.example.tourmanagement.web;

import org.example.tourmanagement.destination.Destination;
import org.example.tourmanagement.destination.DestinationRepository;
import org.example.tourmanagement.feedback.CustomerFeedback;
import org.example.tourmanagement.feedback.CustomerFeedbackRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class PageController {
    
    private final DestinationRepository destinationRepository;
    private final CustomerFeedbackRepository feedbackRepository;
    
    public PageController(DestinationRepository destinationRepository, CustomerFeedbackRepository feedbackRepository) {
        this.destinationRepository = destinationRepository;
        this.feedbackRepository = feedbackRepository;
    }
    
    @GetMapping("/")
    public String home(Model model) {
        // Get top 3 popular destinations
        List<Destination> popularDestinations = destinationRepository.findPopularDestinations();
        if (popularDestinations.size() > 3) {
            popularDestinations = popularDestinations.subList(0, 3);
        }
        
        // Get recent approved feedback (limit to 5 for homepage)
        List<CustomerFeedback> recentFeedback = feedbackRepository.findRecentApprovedFeedback();
        if (recentFeedback.size() > 5) {
            recentFeedback = recentFeedback.subList(0, 5);
        }
        
        model.addAttribute("popularDestinations", popularDestinations);
        model.addAttribute("recentFeedback", recentFeedback);
        return "index";
    }
}


