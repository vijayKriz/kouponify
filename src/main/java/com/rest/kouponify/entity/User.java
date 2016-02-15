package com.rest.kouponify.entity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import lombok.*;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.rest.kouponify.common.PersistableEntity;

/**
 * Created by vijay on 2/15/16.
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonRootName("user")
@JsonIgnoreProperties(ignoreUnknown = true)
public class User extends PersistableEntity implements UserDetails {

    private static final long serialVersionUID = -1284654508625552327L;

    private Long id;

    private String email;

    private String username;

    private String password;

    private String fName;

    private String lName;

    private String shop;

    private Boolean active;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        final Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("admin"));
        authorities.add(new SimpleGrantedAuthority("user"));
        return authorities;
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
        return true;
    }

}
