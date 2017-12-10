package edu.gatech.ihi.nhaa.service;

import edu.gatech.ihi.nhaa.entity.User;
import edu.gatech.ihi.nhaa.web.dto.UserProfileDto;
import edu.gatech.ihi.nhaa.web.dto.UserRegistrationDto;

public interface IUserService {
    User findByEmail(String email);
    User saveRegisteredUser(User user);
    boolean emailExists(String email);
    User registerNewAccount(UserRegistrationDto userDto);
    void updateUserAccount(User user, UserProfileDto profileDto);
    UserProfileDto getUserProfile(User user);
}
