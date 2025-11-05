package com.metrocarpool.gateway.client;

import com.metrocarpool.gateway.dto.PostRiderDTO;
import com.metrocarpool.rider.proto.PostRider;
import com.metrocarpool.rider.proto.RiderServiceGrpc;
import com.metrocarpool.rider.proto.RiderStatusResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.stereotype.Component;

@Component
public class RiderGrpcClient {
    private final RiderServiceGrpc.RiderServiceBlockingStub stub;

    public RiderGrpcClient() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("rider-service", 9090).usePlaintext().build();
        stub = RiderServiceGrpc.newBlockingStub(channel);
    }

    public RiderStatusResponse postRiderInfo(PostRiderDTO postRiderDTO) {
        PostRider postRider = PostRider.newBuilder()
                .setRiderId(postRiderDTO.getRiderId())
                .setPickUpStation(postRiderDTO.getPickUpStation())
                .setArrivalTime(postRiderDTO.getArrivalTime())
                .setDestinationPlace(postRiderDTO.getDestinationPlace())
                .build();

        return stub.postRiderInfo(postRider);
    }
}
