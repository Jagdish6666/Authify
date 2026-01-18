package com.Authentication.demo.service;

import com.Authentication.demo.Repository.UserRepository;
import com.Authentication.demo.entity.UserEntity;
import com.Authentication.demo.io.ProfileRequest;
import com.Authentication.demo.io.ProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {  // ✅ FIXED: changed from ProfileSeviceImpl

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    public ProfileResponse createProfile(ProfileRequest request) {
        if (!userRepository.existsByEmail(request.getEmail())) {
            UserEntity newProfile = convertToUserEntity(request);
            newProfile = userRepository.save(newProfile);
            return convertToProfileResponse(newProfile);
        }

        throw new ResponseStatusException(HttpStatus.CONFLICT,"Email already exists");
    }

    @Override
    public ProfileResponse getProfile(String email) {
        UserEntity existingUser = userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("User not found:"+email));

        return convertToProfileResponse(existingUser);
    }

    @Override
    public void sendResetOtp(String email) {
        UserEntity existingEntity = userRepository.findByEmail(email)
                .orElseThrow(()->new UsernameNotFoundException("User not found:" +email));

        //Generate the 6  figit otp
        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000,1000000));

        //expiry of otp time current + 15 min
        long expiryTime = System.currentTimeMillis()+(15 * 60 * 1000);

        //Update the profile/user
        existingEntity.setResetOtp(otp);
        existingEntity.setResetOtpExpireAt(expiryTime);

        // save into the databse
        userRepository.save(existingEntity);

        try{
            emailService.sendResetOtpEmail(existingEntity.getEmail(),otp);
        }catch(Exception ex)
        {
            throw new RuntimeException("Unable to send Email");
        }

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