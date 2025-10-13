// src/main/java/org/example/tourmanagement/observer/BookingApprovalLogger.java

package org.example.tourmanagement.observerPattern;

import org.example.tourmanagement.booking.Booking;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class BookingApprovalLogger implements BookingEventObserver {
    private static final String LOG_FILE_PATH = "src/main/logs/bookinglogs.txt";

    @Override
    public void onBookingApproved(Booking booking) {
        String logEntry = "[SYSTEM LOG] Booking ID " + booking.getId() +
                " approved for user: " + booking.getUser().getUsername();

        // Write log entry to file (append mode)
        try (PrintWriter out = new PrintWriter(new FileWriter(LOG_FILE_PATH, true))) {
            out.println(logEntry);
        } catch (IOException e) {
            // Optional: handle logging error (e.g., print to console)
            System.err.println("Failed to write log: " + e.getMessage());
        }
    }
}