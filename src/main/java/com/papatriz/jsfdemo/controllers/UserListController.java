package com.papatriz.jsfdemo.controllers;

import com.papatriz.jsfdemo.models.User;
import com.papatriz.jsfdemo.services.UserService;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.RequestAction;
import org.ocpsoft.rewrite.el.ELBeanName;
import org.ocpsoft.rewrite.faces.annotation.Deferred;
import org.ocpsoft.rewrite.faces.annotation.IgnorePostback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope (value = "session")
@Component (value = "userList")
@ELBeanName(value = "userList")
@Join(path = "/userlist", to = "/userlist.xhtml")
public class UserListController {
    @Autowired
    private UserService userService;

    private List<User> users;

    @Deferred
    @RequestAction
    @IgnorePostback
    public void loadData() {
        users = userService.getUserList();
        System.out.println(("loadData executed"));
    }

    public List<User> getUsers() {
        System.out.println(("getUsers executed"));

        return users;
    }
}
