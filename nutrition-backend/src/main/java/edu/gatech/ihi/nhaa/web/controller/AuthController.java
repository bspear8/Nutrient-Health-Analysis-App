package edu.gatech.ihi.nhaa.web.controller;

import edu.gatech.ihi.nhaa.Application;
import edu.gatech.ihi.nhaa.entity.User;
import edu.gatech.ihi.nhaa.security.jwt.TokenProvider;
import edu.gatech.ihi.nhaa.service.IUserService;
import edu.gatech.ihi.nhaa.web.dto.JwtTokenDto;
import edu.gatech.ihi.nhaa.web.dto.UserLoginDto;
import edu.gatech.ihi.nhaa.web.dto.UserRegistrationDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
public class AuthController {

    private final TokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final IUserService userService;

    public AuthController(TokenProvider tokenProvider, AuthenticationManager authenticationManager,
                          IUserService userService) {
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @PostMapping(value = "/auth/login", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JwtTokenDto login(@RequestBody UserLoginDto userLoginDto, HttpServletResponse response) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userLoginDto.getEmail(), userLoginDto.getPassword());

        try {
            this.authenticationManager.authenticate(authenticationToken);
            return new JwtTokenDto(tokenProvider.createToken(userLoginDto.getEmail()));
        }
        catch (AuthenticationException e) {
            Application.logger.info("Security exception {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }
    }

    @PostMapping(path = "/auth/register", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity register(@Valid @RequestBody UserRegistrationDto userDto) {
        final User registeredUser = this.userService.registerNewAccount(userDto);

        if(registeredUser != null) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
