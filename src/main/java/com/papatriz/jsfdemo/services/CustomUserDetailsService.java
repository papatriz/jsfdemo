package com.papatriz.jsfdemo.services;

import com.papatriz.jsfdemo.models.User;
import com.papatriz.jsfdemo.repositories.UserRepository;
import com.papatriz.jsfdemo.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository repository;

    @Autowired
    public CustomUserDetailsService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<User> user = repository.findByUsername(username);
        if(!user.isPresent()) throw new UsernameNotFoundException("User not found");

        System.out.println("Load user by name, user is: "+user.get().getUsername());

        return new UserDetailsImpl(user.get());
    }
}