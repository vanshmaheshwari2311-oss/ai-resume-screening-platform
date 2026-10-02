package com.vansh.resume_screening;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    /*
     * ============================================================
     * LOGIN PAGE
     *
     * URL:
     * http://localhost:8080/login
     * ============================================================
     */
    @GetMapping("/login")
    public String login() {

        return "login";
    }

}