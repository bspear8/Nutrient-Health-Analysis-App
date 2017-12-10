package edu.gatech.ihi.nhaa.web.dto;

import edu.gatech.ihi.nhaa.validation.ValidEmail;
import edu.gatech.ihi.nhaa.validation.ValidPassword;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UserLoginDto {

    private String email;

    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
