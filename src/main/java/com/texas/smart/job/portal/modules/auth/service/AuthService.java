package com.texas.smart.job.portal.modules.auth.service;

import com.texas.smart.job.portal.modules.auth.dto.request.LoginRequest;
import com.texas.smart.job.portal.modules.auth.dto.response.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);
}