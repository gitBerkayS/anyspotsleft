package com.anyspotsleft.anyspotsleft.user;

import com.anyspotsleft.anyspotsleft.listing.ListingModel;
import com.anyspotsleft.anyspotsleft.profileimage.ProfileImageModel;
import com.anyspotsleft.anyspotsleft.vehicle.UserVehicle;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/*
User model class, used for access control represents a customer.
 */
@Entity
@Getter
@Setter
@Table(name = "user_model")
public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String email;
    private String name;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToOne(mappedBy = "user")
    private ProfileImageModel profileImage;

    @OneToOne(mappedBy = "user")
    private UserVehicle vehicle;

    @ManyToMany(mappedBy = "usersEntered")
    private List<ListingModel> listingsEntered;

    @OneToMany(mappedBy = "host")
    private List<ListingModel> listingsHosted;

    @ManyToMany
    @JoinTable(
            name = "user_favourite_listings",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "listing_id", referencedColumnName = "id")
    )
    private List<ListingModel> favouriteListings;

}