package com.example.spring_ecom.repository.database.userInfo;

import com.example.spring_ecom.repository.database.common.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "user_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoEntity extends BaseAuditEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;
    
    @Column(name = "first_name", length = 50)
    private String firstName;
    
    @Column(name = "last_name", length = 50)
    private String lastName;
    
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;
    
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    
    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;
    
    @Column(name = "address", length = 255)
    private String address;
    
    @Column(name = "ward", length = 100)
    private String ward;
    
    @Column(name = "district", length = 100)
    private String district;
    
    @Column(name = "city", length = 100)
    private String city;
    
    @Column(name = "postal_code", length = 20)
    private String postalCode;
}