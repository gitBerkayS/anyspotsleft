package com.anyspotsleft.anyspotsleft.controller;

import com.anyspotsleft.anyspotsleft.user.UserModel;
import com.anyspotsleft.anyspotsleft.user.UserRepository;
import com.anyspotsleft.anyspotsleft.profileimage.ProfileImageService;
import com.anyspotsleft.anyspotsleft.user.UserService;
import com.anyspotsleft.anyspotsleft.vehicle.VehicleService;
import com.anyspotsleft.anyspotsleft.vehicle.VehicleTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;

@Controller
public class UserProfile {

    @Autowired
    VehicleService vehicleService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ProfileImageService profileImageService;

    @GetMapping("/profile/{userId}")
    public String userProfile(@PathVariable Long userId, Model model) {
        return "userAccount";
    }

    @PostMapping("/profile/{userId}")
    public String updateVehicleType(@PathVariable Long userId,
                                    @RequestParam VehicleTypeEnum vehicleType) {
        userRepository.findById(userId).ifPresent(user -> {
            vehicleService.saveUserVehicleType(user, vehicleType);
        });

        return "redirect:/profile/" + userId;
    }

    @PostMapping("/profile/{userId}/uploadImage")
    public String updatePfp(@PathVariable Long userId, @ModelAttribute("user") UserModel user, @RequestParam MultipartFile file) throws IOException {
        profileImageService.uploadImage(user, file);
        return "redirect:/profile/" + userId;
    }
    @PostMapping("/profile/{userId}/changeEmailPassword")
    public String updateEmailPassword(@PathVariable Long userId, @RequestParam(required = false) String email, @RequestParam(required = false) String password, Authentication auth, RedirectAttributes ra) {

        var current = userRepository.findByEmail(auth.getName()).orElseThrow();

        boolean emailChanged = email != null && !email.isBlank() && !email.equals(current.getEmail());
        boolean passChanged    = password != null && !password.isBlank();

        if (emailChanged) current.setEmail(email.trim());
        if (passChanged)    current.setPassword(passwordEncoder.encode(password));
        userRepository.save(current);

        if (passChanged) {
            var newAuth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(current.getEmail(), password) // raw new pw
            );
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        } else if (emailChanged) {
            var details = userDetailsService.loadUserByUsername(current.getEmail());
            var newAuth = new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }

        ra.addFlashAttribute("msg", "Updated successfully.");
        return "redirect:/profile/" + current.getId();
    }

}

