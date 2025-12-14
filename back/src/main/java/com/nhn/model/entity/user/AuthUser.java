package com.nhn.model.entity.user;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.nhn.enums.UserStatusEnum;
import com.nhn.model.dto.response.user.AuthUserRoleInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class AuthUser implements UserDetails {
    @Serial
    private static final long serialVersionUID = -6789584656024611720L;

    private AuthUserRoleInfo user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        final List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(this.user.getRoleNo()));
        return authorities;

    }

    @Override
    public String getPassword() {
        return this.user.getPassword();
    }

    @Override
    public String getUsername() {
        return this.getUser().getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return UserStatusEnum.INACTIVE.getCode() != this.user.getStatus();
    }
}
