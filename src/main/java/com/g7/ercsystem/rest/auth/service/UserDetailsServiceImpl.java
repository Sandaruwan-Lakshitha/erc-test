package com.g7.ercsystem.rest.auth.service;

import com.g7.ercsystem.repository.UserRepository;
import com.g7.ercsystem.rest.auth.model.User;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(()-> new UsernameNotFoundException("User not found with user name :" + username));
        return UserDetailsImpl.build(user);
    }

    public UserDetails loadUserByUserid(String id) throws UsernameNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(()-> new UsernameNotFoundException("User not found with user name :" + id));
        return UserDetailsImpl.build(user);
    }
}
