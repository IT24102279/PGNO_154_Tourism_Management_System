package org.example.tourmanagement.observerPattern;

import org.example.tourmanagement.booking.Booking;

public interface BookingEventObserver {
    void onBookingApproved(Booking booking);
}