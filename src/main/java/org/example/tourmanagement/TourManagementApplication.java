package org.example.tourmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
    "org.example.tourmanagement", 
    "org.example.tourmanagement.admin", 
    "org.example.tourmanagement.destination", 
    "org.example.tourmanagement.finance",
    "org.example.tourmanagement.web",
    "org.example.tourmanagement.*"
})
public class TourManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(TourManagementApplication.class, args);
    }

}
