package com.anyspotsleft.anyspotsleft.vehicle;

import com.anyspotsleft.anyspotsleft.user.UserModel;
import com.anyspotsleft.anyspotsleft.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
/*
bussiness logic for vehicle model related logic.
 */
@Service
public class VehicleService {

    @Autowired
    private UserRepository user;

    @Autowired
    private VehicleRepository vehicle;

    public Optional<UserVehicle> findByUserId(Long userId) {
        return vehicle.findByUserId(userId);
    }
    public Optional<UserVehicle> findUserVehicleByUser(UserModel user) {
        return vehicle.findByUserId(user.getId());
    }

    public List<VehicleTypeEnum> getVehicleTypes() {
        List<VehicleTypeEnum> vehicleTypes = new ArrayList<>();
        vehicleTypes.add(VehicleTypeEnum.SEDAN);
        vehicleTypes.add(VehicleTypeEnum.SUV);
        vehicleTypes.add(VehicleTypeEnum.CROSSOVER);
        vehicleTypes.add(VehicleTypeEnum.PICKUP_TRUCK);
        vehicleTypes.add(VehicleTypeEnum.HATCHBACK);
        vehicleTypes.add(VehicleTypeEnum.CONVERTIBLE);
        vehicleTypes.add(VehicleTypeEnum.MINIVAN);
        return vehicleTypes;
    }

    @Transactional
    public void saveUserVehicleType(UserModel user, VehicleTypeEnum type) {
        UserVehicle existingVehicle = vehicle.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        existingVehicle.setType(type);
        vehicle.save(existingVehicle);
    }

    //future implementation unused method
    @Transactional
    public void setElectricTrue(UserModel user) {
        Optional<UserVehicle> currentVehicle = findUserVehicleByUser(user);

        currentVehicle.ifPresent(x -> x.setElectric(true));


    }
}
