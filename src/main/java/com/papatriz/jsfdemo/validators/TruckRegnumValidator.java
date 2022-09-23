package com.papatriz.jsfdemo.validators;

import com.papatriz.jsfdemo.services.ITruckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import java.util.regex.Pattern;

@Component
@Scope("request")
public class TruckRegnumValidator implements Validator {
    private final ITruckService truckService;

    @Autowired
    public TruckRegnumValidator(ITruckService truckService) {
        this.truckService = truckService;
    }

    @Override
    public void validate(FacesContext facesContext, UIComponent uiComponent, Object o) throws ValidatorException {

        boolean hasError = false;
        String errorMessage = "";

        String regnum = (String) o;

        System.out.println("Validate UIComponent: "+uiComponent.getId());
        boolean test1 = truckService.getTruckById(regnum).isPresent();
        boolean test2 = uiComponent.getId().equals("regnum_ed");
        boolean notAllowed = test1 && !test2;
        System.out.println("Truck present: "+ test1);
        System.out.println("UIComponent "+uiComponent.getId()+" is edit field: "+ test2);
        System.out.println("Need to check if exists: "+ notAllowed);

        boolean properFormat = Pattern.matches("\\D{2}\\d{5}", regnum);

        if (regnum.length() < 7) {
            hasError = true;
            errorMessage = "Registration number is too short. \nProper format: XX12345";
        }
        if (regnum.length() > 7) {
            hasError = true;
            errorMessage = "Registration number is too long. \nProper format: XX12345";
        }
        if (!properFormat && !hasError) {
            hasError = true;
            errorMessage = "Wrong registration number format. \nProper format: XX12345";
        }

        if (notAllowed) {
            hasError = true;
            errorMessage = "Truck with registration number "+regnum+" already exists";
        }

        if (hasError) {
            FacesMessage msg = new FacesMessage(errorMessage, errorMessage);
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);

            throw new ValidatorException(msg);
        }
    }
}
