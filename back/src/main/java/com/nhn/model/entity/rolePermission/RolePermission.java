package com.nhn.model.entity.rolePermission;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

import org.hibernate.annotations.DynamicUpdate;

import com.nhn.model.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DynamicUpdate
@Table(name = "bs_role_permission")
@IdClass(RolePermission.PrimaryKeys.class)
public class RolePermission extends BaseEntity {
    @Serial
    private static final long serialVersionUID = -1559846592927919521L;

    @Data
    public static class PrimaryKeys implements Serializable {
        @Serial
        private static final long serialVersionUID = -7092851044011534195L;

        private UUID roleId;

        private UUID permissionId;
    }

    @Id
    @Column(name = "role_id", nullable = false, length = 36)
    private UUID roleId;

    @Id
    @Column(name = "permission_id", nullable = false, length = 36)
    private UUID permissionId;
}
