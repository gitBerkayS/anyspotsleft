package com.anyspotsleft.anyspotsleft.vehicle;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/*
Vehicle Repository Class
 */
@Repository
public interface VehicleRepository extends JpaRepository<UserVehicle,Long> {

    Optional<UserVehicle>findById(Long id) throws IllegalArgumentException;

    Optional<UserVehicle>findByUserId(Long user) throws IllegalArgumentException;

}
