package io.ao9.flow.api.user.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import io.ao9.flow.api.user.shared.UserDto;

public interface UserService extends UserDetailsService {
    
	UserDto findUserByUserId(String userId);
    UserDto findUserByEmail(String email);
    UserDto createUser(UserDto inputUserDto);
    
}