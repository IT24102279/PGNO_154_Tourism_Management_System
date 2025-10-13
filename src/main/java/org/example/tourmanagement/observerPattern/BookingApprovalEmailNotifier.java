package org.example.tourmanagement.observerPattern;

import org.example.tourmanagement.booking.Booking;

public class BookingApprovalEmailNotifier implements BookingEventObserver {
    @Override
    public void onBookingApproved(Booking booking) {
        String recipient = booking.getUser().getEmail();
        String subject = "EMAIL Service API : Your Booking Has Been Approved!";
        String message = "Hello " + booking.getUser().getUsername() +
                ",\n\nYour booking with ID " + booking.getId() + " has been approved.\nEnjoy your trip!";
        // Replace this with your actual email sending logic
        System.out.println("[EMAIL to " + recipient + "] " + subject + "\n" + message);
        // EmailService.sendEmail(recipient, subject, message); // Uncomment if you have an EmailService
    }
}