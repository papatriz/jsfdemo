package com.papatriz.jsfdemo.services;

import com.papatriz.jsfdemo.models.User;
import com.papatriz.jsfdemo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

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
        System.out.println("USER ID IS "+user.getId());
        userRepository.deleteById(user.getId());
    }
}
