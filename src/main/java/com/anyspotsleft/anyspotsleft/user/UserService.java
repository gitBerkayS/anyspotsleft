package com.anyspotsleft.anyspotsleft.user;

import com.anyspotsleft.anyspotsleft.listing.ListingModel;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
/*
user service class to manage bussiness logic for user model.
 */
@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository repository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<UserModel> user = repository.findByEmail(email);
        System.out.println("Trying login: " + email);
        if (user.isPresent()) {

            var userDetails = user.get();
            return User.builder()
                    .username(userDetails.getEmail())
                    .password(userDetails.getPassword())
                    .roles(userDetails.getRole().name())
                    .build();
        }
        else {
            throw new UsernameNotFoundException("Username not found");
        }

    }
    public int countUsers() {
        List<UserModel> allUsers = userRepository.findAll();
        return allUsers.size();
    }
    public Optional <UserModel> findUserByEmail(String email) {
        return repository.findByEmail(email);
    }

    public void changeFavourite(UserModel user, ListingModel listing)  {
        if(user.getFavouriteListings().contains(listing)) {
            user.getFavouriteListings().remove(listing);
        } else  {
            user.getFavouriteListings().add(listing);
        }
        repository.save(user);
    }



}
