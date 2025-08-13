package com.anyspotsleft.anyspotsleft.controller;

import com.anyspotsleft.anyspotsleft.listing.ListingDTO;
import com.anyspotsleft.anyspotsleft.listing.ListingModel;
import com.anyspotsleft.anyspotsleft.listing.ListingService;
import com.anyspotsleft.anyspotsleft.parkingspots.ParkingSpotDTO;
import com.anyspotsleft.anyspotsleft.parkingspots.ParkingSpotModel;
import com.anyspotsleft.anyspotsleft.parkingspots.ParkingSpotRepository;
import com.anyspotsleft.anyspotsleft.parkingspots.ParkingSpotsService;
import com.anyspotsleft.anyspotsleft.profileimage.ProfileImageService;
import com.anyspotsleft.anyspotsleft.user.UserRepository;
import com.anyspotsleft.anyspotsleft.user.UserService;
import com.anyspotsleft.anyspotsleft.vehicle.VehicleService;
import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.fasterxml.jackson.databind.type.LogicalType.DateTime;
import static java.util.Arrays.stream;

/*
global saved variables called model attributes able to call from java,html,js,css files
super useful for dynamic data.
also heavily used for receiving the user from the authentication system, to get the current user authenticated
 */
@ControllerAdvice
@AllArgsConstructor
public class GlobalUserAttributes {

    @Autowired
    private UserService userService;

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private ProfileImageService profileImageService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ListingService listingService;
    @Autowired
    private ParkingSpotRepository parkingSpotRepository;
    @Autowired
    private ParkingSpotsService parkingSpotsService;

    @ModelAttribute
    public void addAttributes(Model model, Authentication authentication) {
        List<ListingModel> allListings = listingService.findAll()
                .stream()
                .filter(ListingModel::isActive)
                .toList();
        model.addAttribute("activeListings", allListings);

        LocalDateTime now = LocalDateTime.now();

        model.addAttribute("parkingSpotCount", parkingSpotRepository.findAll().size());

        model.addAttribute("listingsCount", allListings.size());

        model.addAttribute("userCount",userService.countUsers());

        List<ParkingSpotModel> allPendingParkingSpots = parkingSpotRepository.findAllByApprovedFalse()
                .stream()
                .toList();
        model.addAttribute("allPendingParkingSpots", allPendingParkingSpots);

        model.addAttribute("pendingParkingSpotCount", allPendingParkingSpots.size());

        Map <Long, String> durationByID = new HashMap<>();
        parkingSpotRepository.findAll().forEach(spot -> {
            durationByID.put(spot.getId(), parkingSpotsService.getDurationSince(spot.getId()));
        });
        model.addAttribute("durationByID", durationByID);


        List<ParkingSpotModel> allApprovedParkingSpots = parkingSpotRepository.findAll()
                .stream()
                .filter(ParkingSpotModel::isApproved)
                .toList();
        model.addAttribute("allApprovedParkingSpots", allApprovedParkingSpots);

        // for JS accessible data
        List<Map<String, Object>> listingJSData = allListings.stream()
                .map(listing -> {
                            var parkingSpot = listing.getParkingSpot();
                            String fullAddress = Stream.of(parkingSpot.getAddress(), parkingSpot.getCity(), parkingSpot.getPostalCode(), parkingSpot.getCity())
                                    .filter(StringUtils::isNotBlank)
                                    .map(String::trim)
                                    .filter(s -> !s.isEmpty())
                                    .collect(Collectors.joining(", "));
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", listing.getId());
                    m.put("fullAddress", fullAddress);
                    m.put("title", parkingSpot.getCity());
                    return m;
                })
                .toList();
        model.addAttribute("listingJSData", listingJSData);

        model.addAttribute("listingJSData", listingJSData);

        model.addAttribute("vehicleTypes", vehicleService.getVehicleTypes());

        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();


            userRepository.findByEmail(email).ifPresent(user -> {
                model.addAttribute("user", user);
                model.addAttribute("favouriteListings", user.getFavouriteListings());

                List<ListingModel> hostListings = listingService.findAllByHost(user)
                        .stream()
                        .filter(ListingModel::isActive)
                        .toList();
                model.addAttribute("activeHostListings", hostListings);

                List<ParkingSpotModel> allParkingSpotsByUser = parkingSpotRepository.findAllByHost(user)
                        .stream()
                        .filter(ParkingSpotModel::isApproved)
                        .toList();
                model.addAttribute("allApprovedParkingSpotsByUser", allParkingSpotsByUser);

                List<ParkingSpotModel> allPendingParkingSpotsByUser = parkingSpotRepository.findAllByHostAndApprovedFalse(user)
                        .stream()
                        .toList();
                model.addAttribute("allPendingParkingSpotsByUser", allPendingParkingSpotsByUser);



                List<ListingModel> allListingsEnteredByUser = user.getListingsEntered();
                model.addAttribute("allListingsEnteredByUser", allListingsEnteredByUser);

                List<Long> idOfAllListingsEnteredByUser = allListingsEnteredByUser.stream()
                        .map(ListingModel::getId)
                        .toList();
                model.addAttribute("idOfAllListingsEnteredByUser", idOfAllListingsEnteredByUser);

                model.addAttribute("vehicleTypes", vehicleService.getVehicleTypes());

                model.addAttribute("listingForm", new ListingDTO());

                model.addAttribute("parkingSpotForm", new ParkingSpotDTO());


                vehicleService.findUserVehicleByUser(user).ifPresent(userVehicle -> {
                    model.addAttribute("vehicle", userVehicle);
                });
            });

        }
    }
}
