package com.Authentication.demo.service;

import com.Authentication.demo.Repository.UserRepository;
import com.Authentication.demo.entity.UserEntity;
import com.Authentication.demo.io.ProfileRequest;
import com.Authentication.demo.io.ProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileSeviceImpl implements ProfileService{

    private final UserRepository userRepository;

    @Override
    public ProfileResponse createProfile(ProfileRequest request)
    {
        UserEntity newProfile = convertToUserEntity(request);
        newProfile = userRepository.save(newProfile);
        return convertToProfileResponse(newProfile);
    }

    private UserEntity convertToUserEntity(ProfileRequest request) {
        return UserEntity.builder()
                .email(request.getEmail())
                .userId(UUID.randomUUID().toString())
                .name(request.getName())
                .password(request.getPassword())
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
                .build(); // hello
    }
}
