package com.anyspotsleft.anyspotsleft.parkingspots;

import com.anyspotsleft.anyspotsleft.vehicle.VehicleTypeEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ParkingSpotDTO {
//data transfer object for parking spots ( database not exposed to forms )
    private String address;
    private String city;
    private String postalCode;
    private String country;

    private List<VehicleTypeEnum> vehiclesAllowed = new ArrayList<>();

    private MultipartFile image;

}
