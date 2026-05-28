package co.edu.cesde.pps.security;

import org.springframework.stereotype.Component;

@Component
public class UuidSessionTokenGenerator implements SessionTokenGenerator {

    @Override
    public String generate() {
        return java.util.UUID.randomUUID().toString();
    }
}
