package com.papatriz.jsfdemo.services;

import com.papatriz.jsfdemo.models.User;
import com.papatriz.jsfdemo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUserList(){

        return  userRepository.findAll();
    }

    public void saveUser(User user){
        userRepository.save(user);
    }

    public void deleteUser(User user){
        System.out.println("USER ID IS "+user.getId());
        userRepository.deleteById(user.getId());
    }
}
