package com.papatriz.jsfdemo.controllers;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.el.ELBeanName;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope(value = "session")
@Component(value = "managerController")
@ELBeanName(value = "managerController")
@Join(path = "/manager", to = "/manager.xhtml")
public class ManagerController {
}
