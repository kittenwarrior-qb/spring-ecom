package com.example.spring_ecom.repository.database.address;

import com.example.spring_ecom.repository.database.common.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressEntity extends BaseAuditEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;
    
    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;
    
    @Column(name = "address_line", nullable = false)
    private String addressLine;
    
    @Column(name = "ward", length = 100)
    private String ward;
    
    @Column(name = "district", nullable = false, length = 100)
    private String district;
    
    @Column(name = "city", nullable = false, length = 100)
    private String city;
    
    @Column(name = "postal_code", length = 20)
    private String postalCode;
    
    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;
}
