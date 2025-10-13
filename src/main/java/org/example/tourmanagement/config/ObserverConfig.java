// This adds the Loggetr and Email notier to the observerPattern as concrete Observeers

package org.example.tourmanagement.config;

import jakarta.annotation.PostConstruct;
import org.example.tourmanagement.observerPattern.*;

import org.springframework.context.annotation.Configuration;

@Configuration
public class ObserverConfig {

    @PostConstruct
    public void registerBookingObservers() {
        BookingEventSubject.getInstance().addObserver(new BookingApprovalLogger());
        BookingEventSubject.getInstance().addObserver(new BookingApprovalEmailNotifier());
    }
}