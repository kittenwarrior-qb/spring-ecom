package com.example.spring_ecom.repository.database.permission;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "role_permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(RolePermissionId.class)
public class RolePermissionEntity {

    @Id
    @Column(name = "role_id", nullable = false)
    private Long roleId;

    @Id
    @Column(name = "permission_id", nullable = false)
    private Long permissionId;
}
