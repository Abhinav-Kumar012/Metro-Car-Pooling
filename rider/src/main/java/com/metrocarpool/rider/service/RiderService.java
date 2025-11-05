package com.metrocarpool.rider.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RiderService {
    // ✅ Inject KafkaTemplate to publish events (assuming Spring Boot Kafka configured)
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String RIDER_TOPIC = "rider-events";

    /**
     * Process the driver info and publish it as an event to Kafka
     *
     * @param riderId         Unique ID of the rider
     * @param pickUpStation    pick up metro station of the rider
     * @param destinationPlace destination of the rider
     * @param arrivalTime arrival time of the rider at the pick up metro station
     * @return true if published successfully, false otherwise
     */
    public boolean processRiderInfo(Long riderId, String pickUpStation,
                                    String destinationPlace, com.google.protobuf.Timestamp arrivalTime) {
        try {
            // ✅ Construct the event payload
            Map<String, Object> event = new HashMap<>();
            event.put("riderId", riderId);
            event.put("pickUpStation", pickUpStation);
            event.put("destinationPlace", destinationPlace);
            event.put("arrivalTime", arrivalTime);

            // ✅ Publish to Kafka topic
            kafkaTemplate.send(RIDER_TOPIC, riderId.toString(), event);

            // Add this to Redis cache of driver service
            // code here

            log.info("🚗 Published rider event for ID {} to topic '{}': {}", riderId, RIDER_TOPIC, event);
            return true;
        } catch (Exception e) {
            log.error("❌ Failed to process rider info for ID {}: {}", riderId, e.getMessage());
            return false;
        }
    }
}
