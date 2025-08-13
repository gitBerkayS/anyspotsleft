package com.anyspotsleft.anyspotsleft.controller;

import com.anyspotsleft.anyspotsleft.profileimage.ProfileImageService;
import com.anyspotsleft.anyspotsleft.user.Role;
import com.anyspotsleft.anyspotsleft.user.UserModel;
import com.anyspotsleft.anyspotsleft.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class MainAccess {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProfileImageService profileImageService;

    @GetMapping("/")
    public String dashboard(@ModelAttribute("user") UserModel user) {

        Role userRole = user.getRole();

        if (userRole == Role.ADMIN) {
            return "admin-dashboard";
        }
        else if (userRole == Role.HOST) {
            return "host-dashboard";
        }
        else {
            return "dashboard";
        }


//        if (SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
//            String email = SecurityContextHolder.getContext().getAuthentication().getName();  // Get logged-in user's email
//
//            UserModel user = userRepository.findByEmail(email).orElse(null);
//
//            if (user != null) {
//                String profileImagePath = profileImageService.getProfileImage(user.getId());
//
//                model.addAttribute("user", user);
//                model.addAttribute("profileImagePath", profileImagePath);
//            }
//
//            return "dashboard";
//        } else {
//            return "dashboard";
//        }
    }

    @GetMapping("/host")
    public String host(@ModelAttribute("user") UserModel user) {
        return "host-dashboard";
    }

    @GetMapping("/admin")
    public String admin(@ModelAttribute("user") UserModel user) {
        return "admin-dashboard";
    }


}


