package com.anyspotsleft.anyspotsleft.parkingspots;

import com.anyspotsleft.anyspotsleft.user.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
/*
parking spot repository for db access
 */
@Repository
public interface ParkingSpotRepository extends JpaRepository<ParkingSpotModel, Long> {

    List<ParkingSpotModel> findAllByHost(UserModel host);

    List<ParkingSpotModel> findAllByHostAndApprovedFalse(UserModel host);

    List<ParkingSpotModel> findAllByApprovedFalse();

}
