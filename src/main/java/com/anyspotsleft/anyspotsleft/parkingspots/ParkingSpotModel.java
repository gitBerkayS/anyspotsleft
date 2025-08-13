package com.anyspotsleft.anyspotsleft.parkingspots;

import com.anyspotsleft.anyspotsleft.user.UserModel;
import com.anyspotsleft.anyspotsleft.vehicle.VehicleTypeEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
/*
parking spot model for listings
 */
@Entity
@Getter
@Setter
@Table(name="parking_spots")
public class ParkingSpotModel {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private String imageContentType;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "bytea")
    @JdbcTypeCode(SqlTypes.BINARY)
    private byte[] imageData;

    private LocalDateTime timeApprovedAt;
    private String city;
    private String address;
    private String postalCode;
    private String country;

    private boolean approved = false;

    // store allowed vehicle types as a value-collection
    @ElementCollection(targetClass = VehicleTypeEnum.class)
    @CollectionTable(name = "parking_spot_allowed_vehicle",
            joinColumns = @JoinColumn(name = "parking_spot_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type")
    private List<VehicleTypeEnum> vehiclesAllowed;

    // host can own multiple spots
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", referencedColumnName = "id", nullable = false)
    private UserModel host;

    @Transient
    public String getImageDataUrl() {
        if (imageData == null || imageData.length == 0) return null;
        String ct = (imageContentType == null || imageContentType.isBlank()) ? "image/png" : imageContentType;
        return "data:" + ct + ";base64," + java.util.Base64.getEncoder().encodeToString(imageData);
    }
}
