package com.papatriz.jsfdemo.security;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class RoleHandler {
    private final Map<String, String>  landingPage =
            Map.of("ROLE_MANAGER", "fleet", "ROLE_DRIVER", "driver", "ROLE_ADMIN", "userlist");

    public String getLandingPageForRole(String role) {

        if(!landingPage.containsKey(role)) return "error/baduser";

        return landingPage.get(role);
    }

}
