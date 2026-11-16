package org.example.ecommerceapi.config;

import org.example.ecommerceapi.security.CustomAuthenticationEntryPoint;
import org.example.ecommerceapi.security.SecurityConfig;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;

@TestConfiguration
@Import({SecurityConfig.class, CustomAuthenticationEntryPoint.class})
public class SecurityTestConfig {
}
