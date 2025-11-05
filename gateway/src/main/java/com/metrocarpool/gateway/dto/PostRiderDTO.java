package com.metrocarpool.gateway.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class PostRiderDTO {
    private Long riderId;
    private String pickUpStation;
    private String destinationPlace;
    private com.google.protobuf.Timestamp arrivalTime;
}
