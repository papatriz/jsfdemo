package com.papatriz.jsfdemo.services;

import com.papatriz.jsfdemo.models.auth.User;
import com.papatriz.jsfdemo.repositories.auth.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, @Qualifier("passwordEncoderBCrypt") PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getUserList(){

        return  userRepository.findAll();
    }

    public void saveUser(User user){

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public void deleteUser(User user){
        userRepository.deleteById(user.getId());
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id).orElse(null);
    }

    public boolean checkIfValidOldPassword(final User user, final String oldPassword) {
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }
}
