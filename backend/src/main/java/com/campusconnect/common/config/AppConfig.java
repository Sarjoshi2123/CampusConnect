package com.campusconnect.common.config;

import com.campusconnect.service.strategy.BalancedRoomAllocationStrategy;
import com.campusconnect.service.strategy.FillFirstRoomAllocationStrategy;
import com.campusconnect.service.strategy.RoomAllocationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AppConfig {

    @Bean
    @Primary
    public RoomAllocationStrategy fillFirstRoomAllocationStrategy() {
        return new FillFirstRoomAllocationStrategy();
    }

    @Bean
    public RoomAllocationStrategy balancedRoomAllocationStrategy() {
        return new BalancedRoomAllocationStrategy();
    }
}
