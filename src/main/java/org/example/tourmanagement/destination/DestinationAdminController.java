package org.example.tourmanagement.destination;

import org.example.tourmanagement.destination.Destination;
import org.example.tourmanagement.destination.DestinationRepository;
import org.example.tourmanagement.booking.BookingRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/destinations") // Change from "/destinations" to "/admin/destinations"
public class DestinationAdminController { // Renamed from DestinationController

    private final DestinationRepository destinationRepository;
    private final BookingRepository bookingRepository;

    public DestinationAdminController(DestinationRepository destinationRepository, BookingRepository bookingRepository) {
        this.destinationRepository = destinationRepository;
        this.bookingRepository = bookingRepository;
    }

    @GetMapping
    public String manageDestinations(Model model) {
        List<Destination> destinations = destinationRepository.findAll();
        model.addAttribute("destinations", destinations);
        model.addAttribute("newDestination", new CreateDestinationForm());
        return "manage-destinations";
    }

    @PostMapping
    public String createDestination(@ModelAttribute("newDestination") CreateDestinationForm form, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (form.getName() == null || form.getName().isBlank()) {
            bindingResult.rejectValue("name", "required", "Destination name is required");
        }
        if (form.getDescription() == null || form.getDescription().isBlank()) {
            bindingResult.rejectValue("description", "required", "Description is required");
        } else if (form.getDescription().length() < 10 || form.getDescription().length() > 500) {
            bindingResult.rejectValue("description", "size", "Description must be between 10 and 500 characters");
        }
        if (form.getImageUrl() == null || form.getImageUrl().isBlank()) {
            bindingResult.rejectValue("imageUrl", "required", "Image URL is required");
        }
        if (form.getRegion() == null || form.getRegion().isBlank()) {
            bindingResult.rejectValue("region", "required", "Region is required");
        }
        if (form.getPrice() == null || form.getPrice() <= 0) {
            bindingResult.rejectValue("price", "required", "Valid price is required");
        }
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please correct the form. " + (form.getDescription() != null && form.getDescription().length() < 10 ? "Description is too short." : ""));
            return "redirect:/admin/destinations";
        }
        
        Destination destination = new Destination();
        destination.setName(form.getName());
        destination.setDescription(form.getDescription());
        destination.setImageUrl(form.getImageUrl());
        destination.setRegion(form.getRegion());
        destination.setPrice(form.getPrice());
        destination.setRating(form.getRating());
        destination.setReviewCount(form.getReviewCount());
        destination.setBadge(form.getBadge());
        destination.setIsActive(true);
        
        destinationRepository.save(destination);
        return "redirect:/admin/destinations?created";
    }

    @PostMapping("/{id}/update")
    public String updateDestination(@PathVariable("id") Long id,
                                   @ModelAttribute("destination") UpdateDestinationForm form,
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes) {
        Destination destination = destinationRepository.findById(id).orElse(null);
        if (destination == null) {
            return "redirect:/admin/destinations?notfound";
        }
        if (form.getName() == null || form.getName().isBlank()) {
            bindingResult.rejectValue("name", "required", "Destination name is required");
        }
        if (form.getDescription() == null || form.getDescription().isBlank()) {
            bindingResult.rejectValue("description", "required", "Description is required");
        } else if (form.getDescription().length() < 10 || form.getDescription().length() > 500) {
            bindingResult.rejectValue("description", "size", "Description must be between 10 and 500 characters");
        }
        if (form.getImageUrl() == null || form.getImageUrl().isBlank()) {
            bindingResult.rejectValue("imageUrl", "required", "Image URL is required");
        }
        if (form.getRegion() == null || form.getRegion().isBlank()) {
            bindingResult.rejectValue("region", "required", "Region is required");
        }
        if (form.getPrice() == null || form.getPrice() <= 0) {
            bindingResult.rejectValue("price", "required", "Valid price is required");
        }
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please correct the form. " + (form.getDescription() != null && form.getDescription().length() < 10 ? "Description is too short." : ""));
            return "redirect:/admin/destinations";
        }
        
        destination.setName(form.getName());
        destination.setDescription(form.getDescription());
        destination.setImageUrl(form.getImageUrl());
        destination.setRegion(form.getRegion());
        destination.setPrice(form.getPrice());
        destination.setRating(form.getRating());
        destination.setReviewCount(form.getReviewCount());
        destination.setBadge(form.getBadge());
        if (form.getIsActive() != null) {
            destination.setIsActive(form.getIsActive());
        }
        
        destinationRepository.save(destination);
        return "redirect:/admin/destinations?updated";
    }

    @PostMapping("/{id}/delete")
    public String deleteDestination(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Destination destination = destinationRepository.findById(id).orElse(null);
        if (destination == null) {
            redirectAttributes.addFlashAttribute("error", "Destination not found");
            return "redirect:/admin/destinations";
        }

        long usageCount = bookingRepository.countByDestination(destination);
        if (usageCount > 0) {
            redirectAttributes.addFlashAttribute(
                "error",
                "Cannot delete destination: it is referenced by " + usageCount + " booking(s)."
            );
            return "redirect:/admin/destinations";
        }

        destinationRepository.delete(destination);
        redirectAttributes.addFlashAttribute("success", "Destination deleted");
        return "redirect:/admin/destinations";
    }

    public static class CreateDestinationForm {
        private String name;
        private String description;
        private String imageUrl;
        private String region;
        private Double price;
        private Double rating;
        private Integer reviewCount;
        private String badge;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public String getRegion() { return region; }
        public void setRegion(String region) { this.region = region; }
        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }
        public Double getRating() { return rating; }
        public void setRating(Double rating) { this.rating = rating; }
        public Integer getReviewCount() { return reviewCount; }
        public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }
        public String getBadge() { return badge; }
        public void setBadge(String badge) { this.badge = badge; }
    }

    public static class UpdateDestinationForm {
        private String name;
        private String description;
        private String imageUrl;
        private String region;
        private Double price;
        private Double rating;
        private Integer reviewCount;
        private String badge;
        private Boolean isActive;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public String getRegion() { return region; }
        public void setRegion(String region) { this.region = region; }
        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }
        public Double getRating() { return rating; }
        public void setRating(Double rating) { this.rating = rating; }
        public Integer getReviewCount() { return reviewCount; }
        public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }
        public String getBadge() { return badge; }
        public void setBadge(String badge) { this.badge = badge; }
        public Boolean getIsActive() { return isActive; }
        public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    }
}