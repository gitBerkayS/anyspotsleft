package com.anyspotsleft.anyspotsleft.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Authentication {

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }
    @GetMapping("/signup/host")
    public String signupHost() {
        return "signup-host";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
