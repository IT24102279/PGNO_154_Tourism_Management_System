package org.example.tourmanagement.admin;

import org.example.tourmanagement.user.User;
import org.example.tourmanagement.user.UserRepository;
import org.example.tourmanagement.support.InquiryRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

//Observer Func Import
import org.example.tourmanagement.observerPattern.BookingEventSubject;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final InquiryRepository inquiryRepository;

    public AdminController(UserRepository userRepository, PasswordEncoder passwordEncoder, InquiryRepository inquiryRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.inquiryRepository = inquiryRepository;
    }

    @GetMapping
    public String admin(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        model.addAttribute("newUser", new CreateUserForm());
        
        // Calculate analytics data
        calculateAnalytics(model);
        
        return "admin";
    }
    
    private void calculateAnalytics(Model model) {
        // Total users count
        long totalUsers = userRepository.count();
        model.addAttribute("totalUsers", totalUsers);
        
        // Active bookings count (APPROVED status)
        // long activeBookings = bookingRepository.countByStatus(BookingStatus.APPROVED);
        // model.addAttribute("activeBookings", activeBookings);
        
        // Support tickets count (all inquiries)
        long supportTickets = inquiryRepository.count();
        model.addAttribute("supportTickets", supportTickets);
        
        // Revenue calculation for current month
        BigDecimal monthlyRevenue = calculateMonthlyRevenue();
        model.addAttribute("monthlyRevenue", monthlyRevenue);
        
        // Additional analytics
        // long totalBookings = bookingRepository.count();
        // long pendingBookings = bookingRepository.countByStatus(BookingStatus.PENDING);
        // long rejectedBookings = bookingRepository.countByStatus(BookingStatus.REJECTED);
        // long completedBookings = bookingRepository.countByStatus(BookingStatus.COMPLETED);
        
        // model.addAttribute("totalBookings", totalBookings);
        // model.addAttribute("pendingBookings", pendingBookings);
        // model.addAttribute("rejectedBookings", rejectedBookings);
        // model.addAttribute("completedBookings", completedBookings);
    }
    
    private BigDecimal calculateMonthlyRevenue() {
        LocalDate now = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(now);
        LocalDate startOfMonth = currentMonth.atDay(1);
        LocalDate endOfMonth = currentMonth.atEndOfMonth();
        
        // List<Booking> monthlyBookings = bookingRepository.findByTravelDateBetween(startOfMonth, endOfMonth);
        
        double totalRevenue = 0; // monthlyBookings.stream()
                // .filter(booking -> booking.getStatus() == BookingStatus.APPROVED || booking.getStatus() == BookingStatus.COMPLETED)
                // .mapToDouble(booking -> {
                //     if (booking.getDestination() != null && booking.getDestination().getPrice() != null) {
                //         return booking.getDestination().getPrice();
                //     }
                //     return 0.0;
                // })
                // .sum();
        
        return BigDecimal.valueOf(totalRevenue);
    }

    @PostMapping("/users")
    public String createUser(@ModelAttribute("newUser") CreateUserForm form, BindingResult bindingResult) {
        if (form.getUsername() == null || form.getUsername().isBlank()) {
            bindingResult.rejectValue("username", "required", "Username is required");
        }
        if (form.getEmail() == null || form.getEmail().isBlank()) {
            bindingResult.rejectValue("email", "required", "Email is required");
        }
        if (form.getPassword() == null || form.getPassword().length() < 6) {
            bindingResult.rejectValue("password", "min", "Password must be at least 6 characters");
        }
        if (bindingResult.hasErrors()) {
            return "redirect:/admin?error";
        }
        User user = new User();
        user.setUsername(form.getUsername());
        user.setEmail(form.getEmail());
        user.setPasswordHash(passwordEncoder.encode(form.getPassword()));
        user.setRole(form.getRole() == null || form.getRole().isBlank() ? "USER" : form.getRole().toUpperCase());
        userRepository.save(user);
        return "redirect:/admin?created";
    }

    @PostMapping("/users/{id}/update")
    public String updateUser(@PathVariable("id") Long id,
                             @ModelAttribute("user") UpdateUserForm form,
                             BindingResult bindingResult) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return "redirect:/admin?notfound";
        }
        if (form.getUsername() == null || form.getUsername().isBlank()) {
            bindingResult.rejectValue("username", "required", "Username is required");
        }
        if (form.getEmail() == null || form.getEmail().isBlank()) {
            bindingResult.rejectValue("email", "required", "Email is required");
        }
        if (bindingResult.hasErrors()) {
            return "redirect:/admin?error";
        }
        user.setUsername(form.getUsername());
        user.setEmail(form.getEmail());
        if (form.getPassword() != null && !form.getPassword().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(form.getPassword()));
        }
        if (form.getRole() != null && !form.getRole().isBlank()) {
            user.setRole(form.getRole().toUpperCase());
        }
        userRepository.save(user);
        return "redirect:/admin?updated";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable("id") Long id) {
        userRepository.findById(id).ifPresent(userRepository::delete);
        return "redirect:/admin?deleted";
    }

    public static class CreateUserForm {
        private String username;
        private String email;
        private String password;
        private String role;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }

    public static class UpdateUserForm {
        private String username;
        private String email;
        private String password;
        private String role;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
}

    // public static class CreateDestinationForm {
    //     private String name;
    //     private String description;
    //     private String imageUrl;
    //     private String region;
    //     private Double price;
    //     private Double rating;
    //     private Integer reviewCount;
    //     private String badge;

    //     public String getName() { return name; }
    //     public void setName(String name) { this.name = name; }
    //     public String getDescription() { return description; }
    //     public void setDescription(String description) { this.description = description; }
    //     public String getImageUrl() { return imageUrl; }
    //     public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    //     public String getRegion() { return region; }
    //     public void setRegion(String region) { this.region = region; }
    //     public Double getPrice() { return price; }
    //     public void setPrice(Double price) { this.price = price; }
    //     public Double getRating() { return rating; }
    //     public void setRating(Double rating) { this.rating = rating; }
    //     public Integer getReviewCount() { return reviewCount; }
    //     public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }
    //     public String getBadge() { return badge; }
    //     public void setBadge(String badge) { this.badge = badge; }
    }

//     public static class UpdateDestinationForm {
//         private String name;
//         private String description;
//         private String imageUrl;
//         private String region;
//         private Double price;
//         private Double rating;
//         private Integer reviewCount;
//         private String badge;
//         private Boolean isActive;

//         public String getName() { return name; }
//         public void setName(String name) { this.name = name; }
//         public String getDescription() { return description; }
//         public void setDescription(String description) { this.description = description; }
//         public String getImageUrl() { return imageUrl; }
//         public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
//         public String getRegion() { return region; }
//         public void setRegion(String region) { this.region = region; }
//         public Double getPrice() { return price; }
//         public void setPrice(Double price) { this.price = price; }
//         public Double getRating() { return rating; }
//         public void setRating(Double rating) { this.rating = rating; }
//         public Integer getReviewCount() { return reviewCount; }
//         public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }
//         public String getBadge() { return badge; }
//         public void setBadge(String badge) { this.badge = badge; }
//         public Boolean getIsActive() { return isActive; }
//         public void setIsActive(Boolean isActive) { this.isActive = isActive; }
//     }
// }
//         public void setRating(Double rating) { this.rating = rating; }
//         public Integer getReviewCount() { return reviewCount; }
//         public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }
//         public String getBadge() { return badge; }
//         public void setBadge(String badge) { this.badge = badge; }
//         public Boolean getIsActive() { return isActive; }
//         public void setIsActive(Boolean isActive) { this.isActive = isActive; }
//     }
// }


