package com.example.spring_ecom.repository.database.role;

import java.io.Serializable;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RolePermissionId implements Serializable {
    private Long roleId;
    private Long permissionId;
}
