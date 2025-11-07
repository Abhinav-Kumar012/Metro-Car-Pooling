package com.metrocarpool.driver.cache;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DriverCache {
    private Integer availableSeats;
    private List<String> routePlaces;
    private String nextPlace;
    private Duration timeToNextPlace;
    private Double distanceToNextPlace;
    private String finalDestination;
    // NEW: store last seen metro station id (empty string if none)
    private String lastSeenMetroStation;
}
