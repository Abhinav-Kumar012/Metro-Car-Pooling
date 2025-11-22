package com.metrocarpool.gateway.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class RiderStatusResponseDTO {
    private Boolean status;
    private String arrivalTime;
}
