package com.Authentication.demo.service;

import com.Authentication.demo.io.ProfileRequest;
import com.Authentication.demo.io.ProfileResponse;

public interface ProfileService {


    ProfileResponse createProfile(ProfileRequest request);
    //this is function

    ProfileResponse getProfile(String email);

    void sendResetOtp(String email);

}