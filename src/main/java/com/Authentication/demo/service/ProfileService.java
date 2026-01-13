package com.Authentication.demo.service;

import com.Authentication.demo.io.ProfileRequest;
import com.Authentication.demo.io.ProfileResponse;

public interface ProfileService {


    ProfileResponse createProfile(ProfileRequest request);

}