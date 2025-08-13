package com.anyspotsleft.anyspotsleft.controller;

import ch.qos.logback.core.model.Model;
import com.anyspotsleft.anyspotsleft.listing.ListingDTO;
import com.anyspotsleft.anyspotsleft.parkingspots.ParkingSpotDTO;
import com.anyspotsleft.anyspotsleft.parkingspots.ParkingSpotModel;
import com.anyspotsleft.anyspotsleft.parkingspots.ParkingSpotRepository;
import com.anyspotsleft.anyspotsleft.parkingspots.ParkingSpotsService;
import com.anyspotsleft.anyspotsleft.user.UserModel;
import com.anyspotsleft.anyspotsleft.user.UserRepository;
import com.anyspotsleft.anyspotsleft.vehicle.VehicleTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ParkingSpot {

    @Autowired
    private final ParkingSpotsService parkingSpotsService;

    @Autowired
    private final ParkingSpotRepository parkingSpotRepository;

    @PostMapping("/parkingSpot/create")
    public RedirectView createParkingSpot(@ModelAttribute("parkingSpotForm") ParkingSpotDTO form, @ModelAttribute("user") UserModel user) throws Exception {
        try {
            parkingSpotsService.createSpot(user, form);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            return new RedirectView("/");
        }
        return new RedirectView("/");
    }

    @PostMapping("/parkingSpot/verify")
    public RedirectView verifyParkingSpot(@ModelAttribute(name = "user")UserModel user, @RequestParam(name = "spot") long spot) throws Exception {
        try {

            parkingSpotsService.changeApproved(spot, user);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            return new RedirectView("/");
        }
        return new RedirectView("/");
    }

}
