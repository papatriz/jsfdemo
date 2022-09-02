package com.papatriz.jsfdemo.controllers;

import com.papatriz.jsfdemo.repositories.UserRepository;
import lombok.Data;
import org.ocpsoft.rewrite.annotation.Join;
        import org.ocpsoft.rewrite.el.ELBeanName;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.context.annotation.Scope;
        import org.springframework.stereotype.Component;

@Data
@Scope(value = "session")
@Component(value = "welcomeController")
@ELBeanName(value = "welcomeController")

@Join(path = "/", to = "/index.xhtml")
public class WelcomeController {

    private String message = "I am dynamic message";

    public void setNewMessage(){
       // setMessage(message);
    }

    public void setMessage(String message) {
        this.message = message;
        System.out.println("Message setted");
    }
}