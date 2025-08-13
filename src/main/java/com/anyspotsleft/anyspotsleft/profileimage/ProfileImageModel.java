package com.anyspotsleft.anyspotsleft.profileimage;

import com.anyspotsleft.anyspotsleft.user.UserModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
/*
profile image model seperated from user for organization
 */
@Entity
@Getter
@Setter
@Table(name = "profile_image")
public class ProfileImageModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String imageContentType;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "bytea")
    @JdbcTypeCode(SqlTypes.BINARY)
    private byte[] image;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserModel user;

    @Transient
    public String getImageDataUrl() {
        if (image == null || image.length == 0) return null;
        String ct = (imageContentType == null || imageContentType.isBlank()) ? "image/png" : imageContentType;
        return "data:" + ct + ";base64," + java.util.Base64.getEncoder().encodeToString(image);
    }
}
