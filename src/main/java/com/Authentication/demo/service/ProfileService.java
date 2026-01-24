package com.Authentication.demo.service;

import com.Authentication.demo.io.ProfileRequest;
import com.Authentication.demo.io.ProfileResponse;

public interface ProfileService {


    ProfileResponse createProfile(ProfileRequest request);
    //this is function

    ProfileResponse getProfile(String email);

    void sendResetOtp(String email);

    void resetPassword(String email, String otp, String newPassword);

    void sendOtp(String email);

    void verifyOtp(String email, String otp);

    String getLoggedInUserId(String email);

}