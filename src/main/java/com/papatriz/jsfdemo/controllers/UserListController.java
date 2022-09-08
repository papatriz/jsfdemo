package com.papatriz.jsfdemo.controllers;

import com.papatriz.jsfdemo.models.User;
import com.papatriz.jsfdemo.services.UserService;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.RequestAction;
import org.ocpsoft.rewrite.el.ELBeanName;
import org.ocpsoft.rewrite.faces.annotation.Deferred;
import org.ocpsoft.rewrite.faces.annotation.IgnorePostback;
import org.primefaces.PrimeFaces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Scope (value = "session")
@Component (value = "userListController")
@ELBeanName(value = "userListController")
@Join(path = "/userlist", to = "/userlist.xhtml")
public class UserListController {
    @Autowired
    private UserService userService;
    private List<User> users;

    private int counter; // TEST
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
/*
    public String deleteUser(User user){
        System.out.println("deleteUser executed, user id is "+user.getId());

        userService.deleteUser(user);

        return  "/userlist.xhtml?faces-redirect=true";
    }
*/
    public void deleteUser(User user){
        System.out.println("deleteUser executed, user id is "+user.getId());

        userService.deleteUser(user);
        loadData();
        PrimeFaces.current().ajax().update("form:testContainer");
        PrimeFaces.current().ajax().update("test");
       // return "/userlist.xhtml?faces-redirect=true";
    }

    public void populateTestUsers()
    {
        System.out.println("Populate users");
        for (int i=0; i < 4; i++){
            User tmpUser = new User("user", "pass", "mail@mail.com", "ROLE_USER");
            userService.saveUser(tmpUser);
        }
        loadData();
        PrimeFaces.current().ajax().update("form:testContainer");


      //  return "/userlist.xhtml?faces-redirect=true";
    }


    public String getMessage()
    {
        counter++;
        return "NOW COUNTER IS "+counter;
    }

    public String getCurrentPos(User user){
       // int pos = users.listIterator().nextIndex();
        int pos = users.indexOf(user) + 1;
        String result = String.valueOf(pos);

        return result;
    }


}
