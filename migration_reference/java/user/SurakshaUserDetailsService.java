package com.suraksha.suraksha.user;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Spring Security calls this automatically during login to check the
// username and (hashed) password against the database.
@Service
public class SurakshaUserDetailsService implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    public SurakshaUserDetailsService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No account found with that username."));

        if (!user.isActive()) {
            throw new UsernameNotFoundException("This account has been deactivated.");
        }

        return User.builder()
                .username(user.getUsername())
                .password(user.getPasswordHash())
                .authorities("ROLE_" + user.getRole().name())
                .build();
    }
}