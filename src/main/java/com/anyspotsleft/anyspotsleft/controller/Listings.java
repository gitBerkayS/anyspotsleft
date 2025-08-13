package com.anyspotsleft.anyspotsleft.controller;

import com.anyspotsleft.anyspotsleft.listing.ListingDTO;
import com.anyspotsleft.anyspotsleft.listing.ListingModel;
import com.anyspotsleft.anyspotsleft.listing.ListingService;
import com.anyspotsleft.anyspotsleft.payments.ChargeRequest;
import com.anyspotsleft.anyspotsleft.payments.StripeService;
import com.anyspotsleft.anyspotsleft.user.UserModel;
import com.anyspotsleft.anyspotsleft.user.UserService;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Controller
@AllArgsConstructor
public class Listings {

    @Autowired
    ListingService listingService;

    @Autowired
    UserService userService;

    @Autowired
    private StripeService paymentsService;

    @PostMapping("/listings/remove")
    public RedirectView removeListing(@RequestParam("listingId") long listingId, @ModelAttribute("user") UserModel user) {
        listingService.removeById(user, listingId);
        return new RedirectView("/");
    }

    @PostMapping("/listings/create")
    public RedirectView createListing(@ModelAttribute("user") UserModel user, @ModelAttribute ListingDTO listingDTO) {
        try {
            listingService.createListing(user, listingDTO.getParkingSpotId(), listingDTO.getCost(), listingDTO.getDescription(), listingDTO.getAvailableSpots(),listingDTO.getEndsAt());
        }
        catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            return new RedirectView("/");
        }
        return new RedirectView("/");
    }

    @PostMapping("/listings/join/{listingId}")
    public RedirectView createCheckoutSession(
            @PathVariable(name = "listingId")  long listingId,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails principal,
            HttpServletRequest req) throws Exception {

        var listing = listingService.findById(listingId)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Listing not found"));

        UserModel user = userService.findUserByEmail(principal.getUsername()).orElseThrow();

        long amountInCents = java.math.BigDecimal.valueOf(listing.getCost())
                .movePointRight(2).setScale(0, java.math.RoundingMode.HALF_UP)
                .longValueExact();
        var params = com.stripe.param.checkout.SessionCreateParams.builder()
                .setMode(com.stripe.param.checkout.SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(baseUrl(req) + "/checkout/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(baseUrl(req) + "/discover?canceled=1")
                .addLineItem(
                        com.stripe.param.checkout.SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("cad")
                                                .setUnitAmount(amountInCents)
                                                .setProductData(
                                                        com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Join listing #" + listingId)
                                                                .setDescription(listing.getDescription())
                                                                .build())
                                                .build())
                                .build())
                .setBillingAddressCollection(
                        com.stripe.param.checkout.SessionCreateParams.BillingAddressCollection.AUTO)
                .setCustomerEmail(user.getEmail())
                .putMetadata("listingId", String.valueOf(listingId))
                .putMetadata("userId", user.getId().toString())
                .build();

        var session = com.stripe.model.checkout.Session.create(params);
        return new RedirectView(session.getUrl());
    }

    private static String baseUrl(HttpServletRequest req) {
        String xfProto = req.getHeader("X-Forwarded-Proto");
        String xfHost  = req.getHeader("X-Forwarded-Host");
        if (xfProto != null && xfHost != null) return xfProto + "://" + xfHost;
        int p = req.getServerPort();
        String port = (p == 80 || p == 443) ? "" : ":" + p;
        return req.getScheme() + "://" + req.getServerName() + port;
    }




    @ExceptionHandler(StripeException.class)
    public String handleError(Model model, StripeException ex) {
        model.addAttribute("error", ex.getMessage());
        return "result";
    }


}
