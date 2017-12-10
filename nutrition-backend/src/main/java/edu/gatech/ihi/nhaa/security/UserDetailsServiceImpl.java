package edu.gatech.ihi.nhaa.security;

import edu.gatech.ihi.nhaa.entity.Role;
import edu.gatech.ihi.nhaa.entity.User;
import edu.gatech.ihi.nhaa.service.IUserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    private final IUserService userService;

    public UserDetailsServiceImpl(IUserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        final User user = this.userService.findByEmail(email);
        if(user == null) {
            throw new UsernameNotFoundException("User '" + email + "' not found");
        }

        return user;
//        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
//        for(Role role : user.getRoles()) {
//            grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
//        }
//
//        return org.springframework.security.core.userdetails.User
//                .withUsername(email)
//                .password(user.getPassword())
//                .authorities(grantedAuthorities)
//                .accountExpired(false)
//                .accountLocked(user.isAccountLocked())
//                .credentialsExpired(false)
//                .disabled(user.isAccountDisabled())
//                .build();
    }

}
