package com.anyspotsleft.anyspotsleft.parkingspots;

import com.anyspotsleft.anyspotsleft.user.Role;
import com.anyspotsleft.anyspotsleft.user.UserModel;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
/*
bussiness logic for parking spot model
 */
@Service
@AllArgsConstructor
public class ParkingSpotsService {
    private final ParkingSpotRepository parkingSpotRepository;

    @Transactional
    public ParkingSpotModel createSpot(UserModel host, ParkingSpotDTO form) throws java.io.IOException {
        // guards
        if (host == null || host.getId() == null) throw new IllegalArgumentException("Auth required.");
        if (host.getRole() != Role.HOST)          throw new IllegalArgumentException("Only HOST can create spots.");

        if (!StringUtils.hasText(form.getAddress()))    throw new IllegalArgumentException("Address required.");
        if (!StringUtils.hasText(form.getCity()))       throw new IllegalArgumentException("City required.");
        if (!StringUtils.hasText(form.getPostalCode())) throw new IllegalArgumentException("Postal code required.");
        if (!StringUtils.hasText(form.getCountry()))    throw new IllegalArgumentException("Country required.");
        if (form.getVehiclesAllowed() == null || form.getVehiclesAllowed().isEmpty())
            throw new IllegalArgumentException("Select at least one vehicle type.");

        // map
        ParkingSpotModel spot = new ParkingSpotModel();
        spot.setAddress(form.getAddress().trim());
        spot.setCity(form.getCity().trim());
        spot.setPostalCode(form.getPostalCode().trim());
        spot.setCountry(form.getCountry().trim());
        spot.setVehiclesAllowed(form.getVehiclesAllowed());
        spot.setHost(host);
        spot.setApproved(false);

        MultipartFile img = form.getImage();
        if (img != null && !img.isEmpty()) {
            spot.setImageContentType(img.getContentType());
            spot.setImageData(img.getBytes());
        }

        return parkingSpotRepository.save(spot);
    }

    @Transactional
    public void changeApproved(long spotId, UserModel user) {
        if (user.getRole() != Role.ADMIN) {
            return;
        }
        parkingSpotRepository.findById(spotId).ifPresent(parkingSpot -> {
            parkingSpot.setApproved(true);
            parkingSpot.setTimeApprovedAt(LocalDateTime.now());
            parkingSpotRepository.save(parkingSpot);
        });
         }

    public String getDurationSince(long spotId) {
        return parkingSpotRepository.findById(spotId)
                .map(ps -> {
                    var start = ps.getTimeApprovedAt();
                    if (start == null) return "pending";
                    var d = java.time.Duration.between(start, java.time.LocalDateTime.now());
                    long days = d.toDays();           d = d.minusDays(days);
                    long hours = d.toHours();         d = d.minusHours(hours);
                    long mins = d.toMinutes();
                    return (days > 0 ? days + "d " : "")
                            + (hours > 0 ? hours + "h " : "")
                            + mins + "m";
                })
                .orElse(null);
    }
}
