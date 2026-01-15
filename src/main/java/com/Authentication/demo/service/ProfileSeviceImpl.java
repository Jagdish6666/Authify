package com.Authentication.demo.service;

import com.Authentication.demo.Repository.UserRepository;
import com.Authentication.demo.entity.UserEntity;
import com.Authentication.demo.io.ProfileRequest;
import com.Authentication.demo.io.ProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileSeviceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ProfileResponse createProfile(ProfileRequest request) {
        if (!userRepository.existsByEmail(request.getEmail())) {
            UserEntity newProfile = convertToUserEntity(request);
            newProfile = userRepository.save(newProfile);
            return convertToProfileResponse(newProfile);
        }

        throw new ResponseStatusException(HttpStatus.CONFLICT,"Email already exists");
    }

    private UserEntity convertToUserEntity(ProfileRequest request) {
        return UserEntity.builder()
                .email(request.getEmail())
                .userId(UUID.randomUUID().toString())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .isAccountVerified(false)
                .resetOtpExpireAt(0L)
                .verifyOtp(null)
                .verifyOtpExpireAt(0L)
                .resetOtp(null)
                .build();
    }

    private ProfileResponse convertToProfileResponse(UserEntity newProfile) {
        return ProfileResponse.builder()
                .name(newProfile.getName())
                .email(newProfile.getEmail())
                .userId(newProfile.getUserId())
                .isAccountVerified(newProfile.getIsAccountVerified())
                .build();
    }
}