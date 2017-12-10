package edu.gatech.ihi.nhaa;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "app")
@Component
public class AppConfig {
    private String secret;
    private long tokenValidityInSeconds;
    private String swaggerBase;
    private String fhirServer;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getTokenValidityInSeconds() {
        return tokenValidityInSeconds;
    }

    public void setTokenValidityInSeconds(long tokenValidityInSeconds) {
        this.tokenValidityInSeconds = tokenValidityInSeconds;
    }

    public String getSwaggerBase() {
        return swaggerBase;
    }

    public void setSwaggerBase(String swaggerBase) {
        this.swaggerBase = swaggerBase;
    }

    public String getFhirServer() {
        return fhirServer;
    }

    public void setFhirServer(String fhirServer) {
        this.fhirServer = fhirServer;
    }
}