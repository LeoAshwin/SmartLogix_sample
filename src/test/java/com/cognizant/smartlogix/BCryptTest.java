package com.cognizant.smartlogix;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptTest {
    @Test
    public void printHash() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        System.out.println("Bcrypt Hash Output: " + encoder.encode("Password@123"));
    }
}
