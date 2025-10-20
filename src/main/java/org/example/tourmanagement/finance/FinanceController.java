package org.example.tourmanagement.finance;

import org.example.tourmanagement.booking.Booking;
import org.example.tourmanagement.booking.BookingRepository;
import org.example.tourmanagement.booking.BookingStatus;
import org.example.tourmanagement.destination.Destination;
import org.example.tourmanagement.destination.DestinationRepository;
import org.example.tourmanagement.observerPattern.BookingEventSubject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class FinanceController {

    private final BookingRepository bookingRepository;
    private final DestinationRepository destinationRepository;

    public FinanceController(BookingRepository bookingRepository, DestinationRepository destinationRepository) {
        this.bookingRepository = bookingRepository;
        this.destinationRepository = destinationRepository;
    }

    @GetMapping("/bookings")
    public String manageBookings(Model model,
                                @RequestParam(value = "status", required = false) String status,
                                @RequestParam(value = "destination", required = false) Long destinationId) {
        List<Booking> bookings;
        if (status != null && !status.isEmpty()) {
            try {
                BookingStatus bookingStatus = BookingStatus.valueOf(status.toUpperCase());
                bookings = bookingRepository.findByStatusOrderByCreatedAtDesc(bookingStatus);
            } catch (IllegalArgumentException e) {
                bookings = bookingRepository.findAll();
            }
        } else if (destinationId != null) {
            Destination destination = destinationRepository.findById(destinationId).orElse(null);
            if (destination != null) {
                bookings = bookingRepository.findByDestinationOrderByCreatedAtDesc(destination);
            } else {
                bookings = bookingRepository.findAll();
            }
        } else {
            bookings = bookingRepository.findAll();
        }

        List<Destination> destinations = destinationRepository.findByIsActiveTrue();

        model.addAttribute("bookings", bookings);
        model.addAttribute("destinations", destinations);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedDestination", destinationId);

        return "manage-bookings";
    }
    public String generateMailtoLink(Booking booking) {
        String recipient = booking.getUser().getEmail();
        String subject = "Your Booking Has Been Approved!";
        StringBuilder body = new StringBuilder();
        body.append("Hello ").append(booking.getUser().getUsername()).append(",%0D%0A%0D%0A");
        body.append("Your booking with ID ").append(booking.getId()).append(" has been approved.%0D%0A");
        body.append("Destination: ").append(booking.getDestination().getName()).append("%0D%0A");
        body.append("Travel Date: ").append(booking.getTravelDate()).append("%0D%0A");
        body.append("Number of Travelers: ").append(booking.getNumberOfTravelers()).append("%0D%0A");
        body.append("%0D%0AEnjoy your trip!%0D%0A%0D%0A");
        body.append("View your destination image here: ").append(booking.getDestination().getImageUrl()).append("%0D%0A");

        return String.format(
                "mailto:%s?subject=%s&body=%s",
                java.net.URLEncoder.encode(recipient, java.nio.charset.StandardCharsets.UTF_8),
                java.net.URLEncoder.encode(subject, java.nio.charset.StandardCharsets.UTF_8),
                body.toString()
        );
    }
    @PostMapping("/bookings/{id}/approve")
    public String approveBooking(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Booking booking = bookingRepository.findById(id).orElse(null);
        if (booking == null) {
            redirectAttributes.addFlashAttribute("error", "Booking not found");
            return "redirect:/admin/bookings";
        }

        if (booking.getStatus() != BookingStatus.PENDING) {
            redirectAttributes.addFlashAttribute("error", "Only pending bookings can be approved");
            return "redirect:/admin/bookings";
        }

        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);
        BookingEventSubject.getInstance().notifyBookingApproved(booking);
        redirectAttributes.addFlashAttribute("success", "Booking approved successfully");
        redirectAttributes.addFlashAttribute("mailtoLink", generateMailtoLink(booking));
        return "redirect:/admin/bookings";
    }

    @PostMapping("/bookings/{id}/reject")
    public String rejectBooking(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Booking booking = bookingRepository.findById(id).orElse(null);
        if (booking == null) {
            redirectAttributes.addFlashAttribute("error", "Booking not found");
            return "redirect:/admin/bookings";
        }

        if (booking.getStatus() != BookingStatus.PENDING) {
            redirectAttributes.addFlashAttribute("error", "Only pending bookings can be rejected");
            return "redirect:/admin/bookings";
        }

        booking.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(booking);
        redirectAttributes.addFlashAttribute("success", "Booking rejected successfully");
        return "redirect:/admin/bookings";
    }

    @PostMapping("/bookings/{id}/update-status")
    public String updateBookingStatus(@PathVariable("id") Long id,
                                     @RequestParam("status") String status,
                                     RedirectAttributes redirectAttributes) {
        Booking booking = bookingRepository.findById(id).orElse(null);
        if (booking == null) {
            redirectAttributes.addFlashAttribute("error", "Booking not found");
            return "redirect:/admin/bookings";
        }

        try {
            BookingStatus newStatus = BookingStatus.valueOf(status.toUpperCase());
            
            // Prevent invalid status transitions
            if (booking.getStatus() == BookingStatus.PENDING && 
                (newStatus == BookingStatus.CANCELLED || newStatus == BookingStatus.COMPLETED)) {
                redirectAttributes.addFlashAttribute("error", "Pending bookings must be approved or rejected first");
                return "redirect:/admin/bookings";
            }
            
            booking.setStatus(newStatus);
            bookingRepository.save(booking);
            redirectAttributes.addFlashAttribute("success", "Booking status updated successfully");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Invalid status");
        }

        return "redirect:/admin/bookings";
    }
}