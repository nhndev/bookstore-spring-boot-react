package com.nhn.model.entity.rolePermission;

import java.io.Serial;
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
@Table(name = "bs_permissions")
public class Permission extends BaseEntity {
    @Serial
    private static final long serialVersionUID = -8896635480402178521L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36)
    private UUID id;

    @Column(name = "permission_no", unique = true, nullable = false, length = 50)
    private String permissionNo;

    @Column(nullable = false, length = 1000)
    private String description;
}

