package com.anyspotsleft.anyspotsleft.listing;

import com.anyspotsleft.anyspotsleft.user.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
//listing repository to access db
@Repository
public interface ListingRepository extends JpaRepository<ListingModel,Long> {
    Optional<ListingModel> findById(Long id);
    List<ListingModel> findAllByHost (UserModel host);

    void deleteById(Long id);

    boolean existsByParkingSpotIdAndActiveIsTrue(Long parkingSpotId);

    List<ListingModel> getByCost(double cost);


}

