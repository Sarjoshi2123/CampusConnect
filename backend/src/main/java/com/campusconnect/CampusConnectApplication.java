package com.campusconnect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Purpose: Application entry point for the CampusConnect backend.
 * Role: Bootstraps the Spring Boot application context. Scheduling is enabled so
 *       NoShowService can periodically sweep exam slots whose check-in window has
 *       closed and mark absent students as NO_SHOW.
 * Important Assumptions: All persistence is in-memory (see repository package) —
 *       there is no database to configure or migrate.
 */
@SpringBootApplication
@EnableScheduling
public class CampusConnectApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusConnectApplication.class, args);
    }
}
