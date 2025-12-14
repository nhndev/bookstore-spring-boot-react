package com.nhn.service.user;

import java.util.Objects;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhn.mapper.UserMapper;
import com.nhn.model.dto.response.user.AuthUserRoleInfo;
import com.nhn.model.entity.user.AuthUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        final AuthUserRoleInfo user     = this.userMapper.getUserRoleInfoByEmail(username);
        if (Objects.isNull(user)) {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }
        return new AuthUser(user);
    }
}
