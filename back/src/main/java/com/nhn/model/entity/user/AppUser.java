package com.nhn.model.entity.user;

import java.io.Serial;
import java.util.UUID;

import org.hibernate.annotations.DynamicUpdate;

import com.nhn.model.entity.BaseEntity;
import com.nhn.model.entity.rolePermission.Role;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldNameConstants
@Entity
@DynamicUpdate
@Table(name = "bs_users")
public class AppUser extends BaseEntity {
    @Serial
    private static final long serialVersionUID = -8863541666190391724L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36)
    private UUID id;

    @Column(name = "full_name", nullable = false, length = 50)
    private String fullName;

    @Column(unique = true, nullable = false, length = 50)
    private String email;

    @Column(name = "phone_number", unique = true, length = 20)
    private String phoneNumber;

    @Column(nullable = false)
    private String password;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "verification_code", length = 64)
    private String verificationCode;

    @Column(name = "reset_password_code", length = 64)
    private String resetPasswordCode;

    private Integer status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
}
