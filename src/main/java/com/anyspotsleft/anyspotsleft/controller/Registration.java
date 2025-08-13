package com.anyspotsleft.anyspotsleft.controller;

import com.anyspotsleft.anyspotsleft.user.Role;
import com.anyspotsleft.anyspotsleft.user.UserModel;
import com.anyspotsleft.anyspotsleft.user.UserRepository;
import com.anyspotsleft.anyspotsleft.user.UserService;
import com.anyspotsleft.anyspotsleft.vehicle.UserVehicle;
import com.anyspotsleft.anyspotsleft.vehicle.VehicleRepository;
import com.anyspotsleft.anyspotsleft.vehicle.VehicleTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Registration {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping(value = "/signup", consumes = "application/json")
    public UserModel signup(@RequestBody UserModel user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        UserModel savedUser = userRepository.save(user);

        UserVehicle vehicle = new UserVehicle();
        vehicle.setUser(savedUser);
        vehicle.setType(VehicleTypeEnum.SEDAN);

        vehicleRepository.save(vehicle);

        return savedUser;
    }

    @PostMapping(value = "/signup/host", consumes = "application/json")
    public UserModel signupHost(@RequestBody UserModel user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.HOST);

        return userRepository.save(user);
    }


    @GetMapping("/testsave")
    public String testSave() {
        UserModel user = new UserModel();
        user.setEmail("tes5t@test.com");
        user.setName("tes5t");
        user.setPassword(passwordEncoder.encode("te5st"));
        user.setRole(Role.HOST);
        userRepository.save(user);
        return "Saved";
    }

}

