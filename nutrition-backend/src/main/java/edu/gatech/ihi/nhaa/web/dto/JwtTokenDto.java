package edu.gatech.ihi.nhaa.web.dto;

public class JwtTokenDto {

    private String jwtToken;

    public JwtTokenDto(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }
}
