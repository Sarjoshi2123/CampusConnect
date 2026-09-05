package com.campusconnect.common.config;

import com.campusconnect.service.strategy.BalancedRoomAllocationStrategy;
import com.campusconnect.service.strategy.FillFirstRoomAllocationStrategy;
import com.campusconnect.service.strategy.RoomAllocationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Purpose: Provides application-wide configuration for the CampusConnect backend.
 * Role: Configures beans such as RoomAllocationStrategy.
 * Important Assumptions: Allows switching between room allocation strategies via Spring's @Primary annotation.
 */
@Configuration
public class AppConfig {

    /**
     * Configures the FillFirstRoomAllocationStrategy as the primary room allocation strategy.
     * To switch to BalancedRoomAllocationStrategy, comment out this @Primary annotation
     * and uncomment the @Primary annotation on balancedRoomAllocationStrategy().
     * @return an instance of FillFirstRoomAllocationStrategy.
     */
    @Bean
    @Primary
    public RoomAllocationStrategy fillFirstRoomAllocationStrategy() {
        return new FillFirstRoomAllocationStrategy();
    }

    /**
     * Configures the BalancedRoomAllocationStrategy as an alternative room allocation strategy.
     * @return an instance of BalancedRoomAllocationStrategy.
     */
    @Bean
    public RoomAllocationStrategy balancedRoomAllocationStrategy() {
        return new BalancedRoomAllocationStrategy();
    }
}
