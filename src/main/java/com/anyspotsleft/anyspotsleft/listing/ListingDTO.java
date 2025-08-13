package com.anyspotsleft.anyspotsleft.listing;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
//data transfer object for listing forms to have safer data transfers

@Setter
@Getter
public class ListingDTO {

    private Long parkingSpotId;
    private Integer availableSpots;
    private double cost;
    private String description;


    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endsAt;
}