package com.metrocarpool.user.grpc;

import com.metrocarpool.user.proto.UserServiceGrpc;
import com.metrocarpool.user.service.UserService;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
@Builder
public class UserGrpcServer extends UserServiceGrpc.UserServiceImplBase {
    @Autowired
    private UserService userService;


}
