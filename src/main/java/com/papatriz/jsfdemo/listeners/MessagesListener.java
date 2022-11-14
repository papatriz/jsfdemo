package com.papatriz.jsfdemo.listeners;

import org.primefaces.PrimeFaces;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import java.util.Iterator;

public class MessagesListener implements PhaseListener {

    private boolean wasMessage = false;

    @Override
    public void afterPhase(PhaseEvent phaseEvent) {
      //  System.out.println("After RENDER_RESPONSE");

    }
    @Override
    public void beforePhase(PhaseEvent phaseEvent) {
     //   System.out.println("Before RENDER_RESPONSE");
     //   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "", "Before RENDER_RESPONSE"));

        System.out.println("BEFORE RESTORE VIEW");
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RESTORE_VIEW;
    }
}
