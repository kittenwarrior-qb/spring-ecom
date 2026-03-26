package com.example.spring_ecom.repository.database.category;

import com.example.spring_ecom.repository.database.common.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categories")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryEntity extends BaseAuditEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "slug", nullable = false, unique = true, length = 100)
    private String slug;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "parent_id")
    private Long parentId;
    
    @Column(name = "display_order", nullable = false)
    @Builder.Default
    private Integer displayOrder = 0;
    
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}
