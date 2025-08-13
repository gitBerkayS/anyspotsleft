package com.anyspotsleft.anyspotsleft.listing;

import com.anyspotsleft.anyspotsleft.parkingspots.ParkingSpotModel;
import com.anyspotsleft.anyspotsleft.user.UserModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
/*
listing model creation
 */
@Entity
@Getter
@Setter
@Table(name="listing")
public class ListingModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int availableSpots;
    private int duration;
    private double cost;

    private LocalDateTime createdAt;
    private LocalDateTime endsAt;

    @Column(name="active")
    private boolean active = true;
    private String description;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "parking_spot_id", referencedColumnName = "id")
    private ParkingSpotModel parkingSpot;

    @ManyToMany(mappedBy = "favouriteListings")
    private List<UserModel> favouritedListingsByUsers;

    @ManyToMany
    @JoinTable(
            name = "listing_user",
            joinColumns = @JoinColumn(name = "listing_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id")
    )
    private List<UserModel> usersEntered;

    @ManyToOne
    @JoinColumn(name = "host_id", referencedColumnName = "id")
    private UserModel host;
}
