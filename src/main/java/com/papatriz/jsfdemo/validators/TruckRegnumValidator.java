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

        boolean properFormat = Pattern.matches("\\D{2}\\d{5}", regnum);

        if (regnum.length() < 7) {
            hasError = true;
            errorMessage = "Reg.number too short. Proper format: XX12345";
        }
        if (regnum.length() > 7) {
            hasError = true;
            errorMessage = "Reg.number too long. Proper format: XX12345";
        }
        if (!properFormat && !hasError) {
            hasError = true;
            errorMessage = "Wrong reg.number format. Proper format: XX12345";
        }

        if (truckService.getTruckById(regnum).isPresent()) {
            hasError = true;
            errorMessage = "Truck with reg.number "+regnum+" already exists in database";
        }

        if (hasError) {
            FacesMessage msg = new FacesMessage("", errorMessage);
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);

            throw new ValidatorException(msg);
        }
    }
}
