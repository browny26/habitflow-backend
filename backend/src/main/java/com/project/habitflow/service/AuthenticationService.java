package com.project.habitflow.service;

import com.project.habitflow.request.AuthenticationRequest;
import com.project.habitflow.request.RegisterRequest;
import com.project.habitflow.response.AuthenticationResponse;

public interface AuthenticationService {
    void register(RegisterRequest input) throws Exception;
    AuthenticationResponse login(AuthenticationRequest request);
}
