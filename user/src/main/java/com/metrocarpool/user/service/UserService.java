package com.metrocarpool.user.service;

import com.metrocarpool.user.entity.DriverEntity;
import com.metrocarpool.user.entity.RiderEntity;
import com.metrocarpool.user.repository.DriverRepository;
import com.metrocarpool.user.repository.RiderRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Builder
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final DriverRepository driverRepository;
    private final RiderRepository riderRepository;

    public Boolean driverSignUp(String username, String password, Long licenseId){
        try {
            driverRepository.save(
                    DriverEntity.builder()
                    .username(username)
                    .password(password)
                    .licenseId(licenseId)
                    .build()
            );
            return true;
        }
        catch (Exception e){
            log.error(e.getMessage());
            return false;
        }
    }

    public Boolean riderSignUp(String username, String password){
        try {
            riderRepository.save(
                    RiderEntity.builder()
                            .username(username)
                            .password(password)
                            .build()
            );
            return true;
        }
        catch (Exception e){
            log.error(e.getMessage());
            return false;
        }
    }
}
