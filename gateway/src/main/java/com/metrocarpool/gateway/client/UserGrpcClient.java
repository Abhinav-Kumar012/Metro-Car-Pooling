package com.metrocarpool.gateway.client;

import com.metrocarpool.gateway.dto.DriverSignUpRequestDTO;
import com.metrocarpool.gateway.dto.RiderSignUpRequestDTO;
import com.metrocarpool.user.proto.DriverSignUp;
import com.metrocarpool.user.proto.RiderSignUp;
import com.metrocarpool.user.proto.SignUpOrLoginResponse;
import com.metrocarpool.user.proto.UserServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.stereotype.Component;

@Component
public class UserGrpcClient {
    private final UserServiceGrpc.UserServiceBlockingStub stub;

    public UserGrpcClient() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("user-service", 9090).usePlaintext().build();
        stub = UserServiceGrpc.newBlockingStub(channel);
    }

    public SignUpOrLoginResponse DriverSignUpReq(DriverSignUpRequestDTO driverSignUpRequestDTO) {
        DriverSignUp driverSignUp = DriverSignUp.newBuilder()
                .setUsername(driverSignUpRequestDTO.getUsername())
                .setPassword(driverSignUpRequestDTO.getPassword())
                .setLicenseId(driverSignUpRequestDTO.getLicenseId())
                .build();

        return stub.driverSignUpRequest(driverSignUp);
    }

    public SignUpOrLoginResponse RiderSignUpReq(RiderSignUpRequestDTO riderSignUpRequestDTO) {
        RiderSignUp riderSignUp = RiderSignUp.newBuilder()
                .setUsername(riderSignUpRequestDTO.getUsername())
                .setPassword(riderSignUpRequestDTO.getPassword())
                .build();

        return stub.riderSignUpRequest(riderSignUp);
    }
}
