package com.papatriz.jsfdemo.controllers;

import com.papatriz.jsfdemo.models.auth.User;
import com.papatriz.jsfdemo.services.UserService;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.el.ELBeanName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope(value = "session")
@Component(value = "addUserController")
@ELBeanName(value = "addUserController")
@Join(path = "/add", to = "/adduser.xhtml")
public class AddUserController {

    private final UserService userService;
    private User user = new User();

    @Autowired
    public AddUserController(UserService userService) {
        this.userService = userService;
    }

    public String addUser(){
        userService.saveUser(user);
        user = new User();

        return  "/userlist.xhtml?faces-redirect=true";
    }

    public User getUser() { return user; }


}
