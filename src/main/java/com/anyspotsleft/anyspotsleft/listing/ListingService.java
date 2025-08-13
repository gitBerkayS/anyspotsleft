package com.anyspotsleft.anyspotsleft.listing;

import com.anyspotsleft.anyspotsleft.parkingspots.ParkingSpotModel;
import com.anyspotsleft.anyspotsleft.parkingspots.ParkingSpotRepository;
import com.anyspotsleft.anyspotsleft.user.Role;
import com.anyspotsleft.anyspotsleft.user.UserModel;
import com.anyspotsleft.anyspotsleft.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
/*
listing repository for business logic relating to listing model
 */
@Service
@AllArgsConstructor
public class ListingService {

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    ParkingSpotRepository parkingSpotRepository;

    public List<ListingModel> findAll() {
        return listingRepository.findAll();
    }

    public Optional<ListingModel> findById(Long id) {
        return listingRepository.findById(id);
    }

    public List<ListingModel> findAllByHost(UserModel host) {
        return listingRepository.findAllByHost(host);
    }

    @Transactional
    public void userJoinListing(long listingId, @NotNull UserModel user) {
        ListingModel listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("Listing not found"));

        if (user.getRole() != Role.USER) {
            return;
        }

        if (user.getListingsEntered() == null) {
            user.setListingsEntered(new ArrayList<>());
        }
        if (listing.getUsersEntered() == null) {
            listing.setUsersEntered(new ArrayList<>());
        }

        boolean alreadyJoined = user.getListingsEntered()
                .stream().anyMatch(listings -> listings.getId().equals(listingId));
        if (alreadyJoined){
            return;
        }

        user.getListingsEntered().add(listing);
        listing.getUsersEntered().add(user);

        listingRepository.save(listing);
    }


    @Transactional
    public void removeById(UserModel user, Long id) {
        Optional<ListingModel> currentListing = listingRepository.findById(id);
        if (currentListing.isPresent()) {
            if (currentListing.get().getHost().equals(user)) {
                listingRepository.deleteById(id);
            }
        }
    }

    @Transactional
    public ListingModel createListing(UserModel host, Long parkingSpotId, double cost, String description, int availableSpots, LocalDateTime listingEnd) {

        LocalDateTime now = LocalDateTime.now();

        if (host.getRole() != Role.HOST) throw new IllegalArgumentException("Only HOST can create listings.");
        if (availableSpots < 1)         throw new IllegalArgumentException("availableSpots must be >= 1.");
        if (cost < 0)                   throw new IllegalArgumentException("cost must be >= 0.");
        if (listingEnd == null || !listingEnd.isAfter(now))
            throw new IllegalArgumentException("endsAt must be in the future.");

        ParkingSpotModel spot = parkingSpotRepository.findById(parkingSpotId)
                .orElseThrow(() -> new IllegalArgumentException("Parking spot not found."));
        if (!spot.getHost().getId().equals(host.getId()))
            throw new IllegalArgumentException("You can only list your own spot.");

        if (listingRepository.existsByParkingSpotIdAndActiveIsTrue(parkingSpotId))
            throw new IllegalStateException("This spot already has an active listing.");

        ListingModel listing = new ListingModel();
        listing.setCreatedAt(now);
        listing.setActive(true);
        listing.setCost(cost);
        listing.setHost(host);
        listing.setDescription(description);
        listing.setAvailableSpots(availableSpots);
        listing.setEndsAt(listingEnd);
        listing.setParkingSpot(spot);
        listing.setDuration((int) Duration.between(now, listingEnd).toMinutes()); // if your 'duration' is minutes

        return listingRepository.save(listing);
    }

    public long amountCentsFor(long listingId) {
        double cost = listingRepository.findById(listingId).get().getCost();

        cost = cost*100;

        return Math.round(cost);
    }
}
