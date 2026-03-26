package com.example.spring_ecom.repository.database.cart;

import com.example.spring_ecom.repository.database.common.BaseCartEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "carts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartEntity extends BaseCartEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
}
