package org.example.tourmanagement.observerPattern;

import org.example.tourmanagement.booking.Booking;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BookingEventSubject {
    private static final BookingEventSubject INSTANCE = new BookingEventSubject();
    private final List<BookingEventObserver> observers = new CopyOnWriteArrayList<>();

    private BookingEventSubject() {}

    public static BookingEventSubject getInstance() {
        return INSTANCE;
    }

    public void addObserver(BookingEventObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(BookingEventObserver observer) {
        observers.remove(observer);
    }

    public void notifyBookingApproved(Booking booking) {
        for (BookingEventObserver observer : observers) {
            observer.onBookingApproved(booking);
        }
    }
}