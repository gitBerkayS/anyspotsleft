package com.anyspotsleft.anyspotsleft.controller;

import com.anyspotsleft.anyspotsleft.listing.ListingModel;
import com.anyspotsleft.anyspotsleft.listing.ListingService;
import com.anyspotsleft.anyspotsleft.payments.ChargeRequest;
import com.anyspotsleft.anyspotsleft.profileimage.ProfileImageService;
import com.anyspotsleft.anyspotsleft.user.UserModel;
import com.anyspotsleft.anyspotsleft.user.UserRepository;
import com.anyspotsleft.anyspotsleft.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class Discovery {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private final ListingService listingService;
    @Autowired
    private ProfileImageService profileImageService;

    @Value("${STRIPE.PUBLIC_KEY}")
    private String stripePublicKey;


    @GetMapping({"/discover",})
    public String discover(@ModelAttribute("user") UserModel user,
                           Model model) {

        model.addAttribute("listings", listingService.findAll());

        if (user != null && user.getId() != null) {
            String profileImagePath = profileImageService.getProfileImage(user.getId());
            model.addAttribute("user", user);
            model.addAttribute("profileImagePath", profileImagePath);
        }

        return "discover";
    }
    @PostMapping({ "/discover/{listingId}"})
    public String discoverID(@ModelAttribute("user") UserModel user, @PathVariable(value = "listingId") Long listingId, Model model, Authentication auth) {
        if (auth.getName().equals(user.getEmail())) {


            model.addAttribute("listings", listingService.findAll());

            if (user != null && user.getId() != null) {
                String profileImagePath = profileImageService.getProfileImage(user.getId());
                model.addAttribute("user", user);
                model.addAttribute("profileImagePath", profileImagePath);
            }
            if (user.getListingsEntered().contains(listingService.findById(listingId))) {

                model.addAttribute("amount", Math.round(listingService.findById(listingId).get().getCost() * 100)); // cent conversion
                model.addAttribute("currency", ChargeRequest.Currency.CAD);
                model.addAttribute("stripePublicKey", stripePublicKey);
            }

        }
        return "discover";
    }



    @PostMapping("/discover/favourite/{listingId}")
    public String toggleFavourite(@ModelAttribute(name = "user") UserModel user, @PathVariable Long listingId) {

        ListingModel listing = listingService.findById(listingId).orElseThrow();
        userService.changeFavourite(user, listing);
        return "redirect:/discover";
    }
}




