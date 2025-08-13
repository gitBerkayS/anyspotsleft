package com.anyspotsleft.anyspotsleft.profileimage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/*
profile image db access
 */
@Repository
public interface ProfileImageRepository extends JpaRepository<ProfileImageModel, Long> {

    public Optional<ProfileImageModel> findByUserId(Long user_id);

}
