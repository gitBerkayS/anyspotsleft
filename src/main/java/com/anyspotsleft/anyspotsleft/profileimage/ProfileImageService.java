package com.anyspotsleft.anyspotsleft.profileimage;

import com.anyspotsleft.anyspotsleft.user.UserModel;
import com.anyspotsleft.anyspotsleft.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
/*
profile img bussiness logic for profile img model
 */
@Service
public class ProfileImageService {

    @Autowired
    private ProfileImageRepository profileImageRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public ProfileImageModel uploadImage(UserModel user, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("No file selected");
        }

        ProfileImageModel pim = user.getProfileImage();
        if (pim == null) {
            pim = new ProfileImageModel();
            pim.setUser(user);
            user.setProfileImage(pim);
        }

        pim.setImageContentType(file.getContentType());
        pim.setImage(file.getBytes());

        return profileImageRepository.save(pim);
    }


    public Optional<ProfileImageModel> findByUserId(Long user_id) {
        return profileImageRepository.findByUserId(user_id);
    }


    public String getProfileImage(Long user_id) {
        byte[] existingImage = getProfileImageExists(user_id);
        String currentImage;

        if (existingImage != null) {
            // converting byte array to string for html
            currentImage = "data:image/jpeg;base64," + java.util.Base64.getEncoder().encodeToString(existingImage);
        } else {
            //old & replaced but kept incase the other method errors.
            currentImage = getProfileImageDefault();
        }

        return currentImage;
    }

    private byte[] getProfileImageExists(Long user_id) {
        ProfileImageModel profileImage = profileImageRepository.findByUserId(user_id).orElse(null);
        if (profileImage != null) {
            return profileImage.getImage();
        }
        else  {
            return null;
        }
    }
    //file path moved
    private String getProfileImageDefault() {
        return "/images/profilepictures/defaultPfp.svg";
    }


}
