package com.anyspotsleft.anyspotsleft.controller;

import com.anyspotsleft.anyspotsleft.listing.ListingModel;
import com.anyspotsleft.anyspotsleft.listing.ListingService;
import com.anyspotsleft.anyspotsleft.payments.ChargeRequest;
import com.anyspotsleft.anyspotsleft.user.UserModel;
import com.anyspotsleft.anyspotsleft.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class Checkout{

    @Autowired
    UserService userService;

    @Autowired
    ListingService listingService;

    @GetMapping("/checkout/success")
    public String success(@RequestParam("session_id") String sessionId,
                          @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails principal,
                          RedirectAttributes ra) throws Exception {

        var retrieveParams = com.stripe.param.checkout.SessionRetrieveParams.builder()
                .addExpand("payment_intent").build();

        var session = com.stripe.model.checkout.Session.retrieve(sessionId, retrieveParams, null);

        // Verify paid
        boolean paid = "paid".equalsIgnoreCase(session.getPaymentStatus());
        if (!paid) {
            ra.addFlashAttribute("error", "Payment not completed");
            return "redirect:/discover";
        }

        Long listingId = Long.valueOf(session.getMetadata().get("listingId"));
        UserDetails user = userService.loadUserByUsername(principal.getUsername());
        UserModel userModel = (UserModel) user;

        listingService.userJoinListing(listingId, userModel);

        ra.addFlashAttribute("message", "Joined listing!");
        return "redirect:/discover?joined=" + listingId;
    }

}

