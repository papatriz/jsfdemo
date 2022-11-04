package com.papatriz.jsfdemo.controllers;

import com.papatriz.jsfdemo.models.User;
import com.papatriz.jsfdemo.security.UserDetailsImpl;
import com.papatriz.jsfdemo.services.UserService;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.RequestAction;
import org.ocpsoft.rewrite.el.ELBeanName;
import org.ocpsoft.rewrite.faces.annotation.Deferred;
import org.ocpsoft.rewrite.faces.annotation.IgnorePostback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.Serializable;

@Scope(value = "session")
@Component(value = "changePassController")
@ELBeanName(value = "changePassController")
@Join(path = "/changePassword", to = "/changePassword.xhtml")
public class ChangePasswordController implements Serializable {
    @Autowired
    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(ChangePasswordController.class);

    private User user;
    private String oldPassword = "";
    private String newPassword = "";

    public ChangePasswordController(UserService userService) {
        this.userService = userService;
    }

    public boolean isPassStrongEnough() {
        return passStrongEnough;
    }

    public void setPassStrongEnough(boolean passStrongEnough) {
        this.passStrongEnough = passStrongEnough;
    }

    private boolean passStrongEnough;

    @Deferred
    @RequestAction
    @IgnorePostback
    public void init() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl principal = (UserDetailsImpl) auth.getPrincipal();
        user = principal.getUser();
        logger.info("USER WITH EXPIRED PASS: "+user.getUsername());
    }

    public String saveNewPassword() throws IOException {

        user.setPassword(newPassword);
       // user.setNeedChangePassword(false);
        userService.saveUser(user);
        FacesContext.getCurrentInstance().getExternalContext().redirect("/logout?faces-redirect=true");
        return "/logout?faces-redirect=true";
    }
    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public User getUser() {
        return user;
    }

}
