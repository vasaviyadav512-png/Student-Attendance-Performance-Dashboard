package com.college.dashboard.service;

import com.college.dashboard.dto.LoginRequest;
import com.college.dashboard.dto.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    LoginResponse getCurrentUser(String username);
}
