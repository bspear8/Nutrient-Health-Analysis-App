package edu.gatech.ihi.nhaa.service;

import edu.gatech.ihi.nhaa.repository.RoleRepository;
import edu.gatech.ihi.nhaa.web.dto.UserProfileDto;
import edu.gatech.ihi.nhaa.web.dto.UserRegistrationDto;
import edu.gatech.ihi.nhaa.web.error.UserAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import edu.gatech.ihi.nhaa.entity.User;
import edu.gatech.ihi.nhaa.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

@Service
public class UserService implements IUserService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;

	@Autowired
	public UserService(UserRepository userRepository,
					   RoleRepository roleRepository,
					   PasswordEncoder passwordEncoder) {

		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public User findByEmail(String email) {
		return userRepository.findByUsername(email);
	}

	@Override
	public User saveRegisteredUser(final User user) {
		return userRepository.save(user);
	}

	@Override
	public boolean emailExists(final String email) {
		return userRepository.findByUsername(email) != null;
	}

	@Override
	public User registerNewAccount(final UserRegistrationDto userDto) {
		if(emailExists(userDto.getEmail())) {
			throw new UserAlreadyExistsException("There is an account with that email address: " + userDto.getEmail());
		}
		final User user = new User();

		user.setFirstName(userDto.getFirstName());
		user.setLastName(userDto.getLastName());
		user.setPassword(passwordEncoder.encode(userDto.getPassword()));
		user.setUsername(userDto.getEmail());
		user.setPatientId(UUID.randomUUID().toString());
		user.setRoles(Collections.singletonList(roleRepository.findByName("ROLE_USER")));

		return userRepository.save(user);
	}

	public void updateUserAccount(User user, final UserProfileDto profileDto) {
		if (!profileDto.getEmail().equalsIgnoreCase(user.getUsername())
				&& emailExists(profileDto.getEmail())) {
			throw new UserAlreadyExistsException("There is an account with that email address: " + profileDto.getEmail());
		}

		user.setFirstName(profileDto.getFirstName());
		user.setLastName(profileDto.getLastName());

		if(profileDto.getPassword() != null && !profileDto.getPassword().isEmpty()) {
			user.setPassword(passwordEncoder.encode(profileDto.getPassword()));
		}

		user.setUsername(profileDto.getEmail());
		user.setPatientId(profileDto.getPatientId());

		userRepository.save(user);
	}

	public UserProfileDto getUserProfile(User user) {
		UserProfileDto dto = new UserProfileDto();
		dto.setFirstName(user.getFirstName());
		dto.setLastName(user.getLastName());
		dto.setEmail(user.getUsername());
		dto.setPatientId(user.getPatientId());

		return dto;
	}
}
