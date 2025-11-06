package com.metrocarpool.gateway.controller;

import com.metrocarpool.gateway.client.UserGrpcClient;
import com.metrocarpool.gateway.dto.DriverSignUpRequestDTO;
import com.metrocarpool.gateway.dto.RiderSignUpRequestDTO;
import com.metrocarpool.gateway.dto.SignUpOrLoginResponseDTO;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Builder
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserGrpcClient userGrpcClient;

    @PostMapping(value = "/add-driver")
    public SignUpOrLoginResponseDTO addDriver(@RequestBody DriverSignUpRequestDTO driverSignUpRequestDTO) {
        return SignUpOrLoginResponseDTO.builder()
                .STATUS_CODE(userGrpcClient.DriverSignUpReq(driverSignUpRequestDTO).getSTATUSCODE())
                .build();
    }

    @PostMapping(value = "/add-rider")
    public SignUpOrLoginResponseDTO addRider(@RequestBody RiderSignUpRequestDTO riderSignUpRequestDTO) {
        return SignUpOrLoginResponseDTO.builder()
                .STATUS_CODE(userGrpcClient.RiderSignUpReq(riderSignUpRequestDTO).getSTATUSCODE())
                .build();
    }

}
