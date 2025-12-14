package com.nhn.model.entity.rolePermission;

import java.io.Serial;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.DynamicUpdate;

import com.nhn.model.entity.BaseEntity;
import com.nhn.model.entity.user.AppUser;

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
@Table(name = "bs_roles")
public class Role extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 288633071249245281L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36)
    private UUID id;

    @Column(name = "role_no", unique = true, nullable = false, length = 50)
    private String roleNo;

    private String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "role")
    private List<AppUser> userList;
}
