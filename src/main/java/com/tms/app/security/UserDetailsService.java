package com.tms.app.security;

import com.tms.app.entities.feature.Feature;
import com.tms.app.entities.role.Role;
import com.tms.app.entities.user.User;
import com.tms.app.repositories.feature.FeatureRepository;
import com.tms.app.repositories.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRepository userRepository;
    private final FeatureRepository featureRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findActiveUserByEmailOrUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not Found"));

        Role role = user.getRole();
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), true,
                true, true, true, getAuthorities(role));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(
            Role role) {
        return getGrantedAuthorities(getPermissions(role));
    }

    private List<String> getPermissions(Role role) {

        List<Feature> featureList = featureRepository.findFeaturesByRoleId(role.getId());
        List<String> features = new ArrayList<>(featureList.stream().map(Feature::getFeatureValue).toList());
        features.add(role.getRoleName());
        return features;
    }

    private List<GrantedAuthority> getGrantedAuthorities(List<String> privileges) {

        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String privilege : privileges) {
            authorities.add(new SimpleGrantedAuthority(privilege));
        }
        return authorities;
    }

}
