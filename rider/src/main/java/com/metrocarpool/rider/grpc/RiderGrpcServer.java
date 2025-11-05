package com.metrocarpool.rider.grpc;

import com.metrocarpool.rider.proto.PostRider;
import com.metrocarpool.rider.proto.RiderServiceGrpc;
import com.metrocarpool.rider.proto.RiderStatusResponse;
import com.metrocarpool.rider.service.RiderService;
import io.grpc.stub.StreamObserver;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class RiderGrpcServer extends RiderServiceGrpc.RiderServiceImplBase {

    private final RiderService riderService;

    @Override
    public void postRiderInfo(PostRider request, StreamObserver<RiderStatusResponse> responseObserver) {
        try {
            // ✅ Call the business logic
            boolean success = riderService.processRiderInfo(
                    request.getRiderId(),
                    request.getPickUpStation(),
                    request.getDestinationPlace(),
                    request.getArrivalTime()
            );

            // ✅ Build the response
            RiderStatusResponse response = RiderStatusResponse.newBuilder()
                    .setStatus(success)
                    .build();

            // ✅ Send the response back to the client
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            // ❌ Handle any exception gracefully
            responseObserver.onError(e);
        }
    }
}
