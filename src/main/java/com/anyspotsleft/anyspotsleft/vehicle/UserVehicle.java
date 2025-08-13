package com.anyspotsleft.anyspotsleft.vehicle;

/*
Vehicle Model Class
*/
import com.anyspotsleft.anyspotsleft.user.UserModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="vehicle")
public class UserVehicle {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private VehicleTypeEnum type =  VehicleTypeEnum.SEDAN;

    @Column(nullable = false)
    private boolean isElectric;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserModel user;
}

