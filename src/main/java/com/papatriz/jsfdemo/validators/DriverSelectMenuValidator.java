package com.papatriz.jsfdemo.validators;

import org.primefaces.PrimeFaces;
import org.primefaces.component.selectcheckboxmenu.SelectCheckboxMenu;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("driverSelectValidator")
public class DriverSelectMenuValidator implements Validator
{
    @Override
    public void validate(FacesContext facesContext, UIComponent uiComponent, Object o) throws ValidatorException {
        Integer limit = Integer.parseInt((String)uiComponent.getAttributes().get("driverNumber"));
        SelectCheckboxMenu checkboxMenu = (SelectCheckboxMenu)uiComponent;
        checkboxMenu.setStyleClass("ui-state-default");
        PrimeFaces.current().ajax().update(uiComponent);

        if (((String[])checkboxMenu.getSubmittedValue()).length != limit) {
            FacesMessage msg = new FacesMessage("Number of drivers for this truck must be "+limit, "");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            checkboxMenu.setStyleClass("ui-state-error");
            PrimeFaces.current().ajax().update(uiComponent);

            throw new ValidatorException(msg);
        }
    }
}
