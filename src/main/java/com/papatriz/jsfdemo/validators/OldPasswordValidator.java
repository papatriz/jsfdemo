package com.papatriz.jsfdemo.validators;

import com.papatriz.jsfdemo.models.User;
import com.papatriz.jsfdemo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import java.util.UUID;

@Component
@Scope("request")
public class OldPasswordValidator implements Validator {
    private final UserService userService;
    @Autowired
    public OldPasswordValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void validate(FacesContext facesContext, UIComponent uiComponent, Object o) throws ValidatorException {
        String input = (String) o;
        UUID userId = (UUID) uiComponent.getAttributes().get("userId");
        User user = userService.getUserById(userId);

        if(!userService.checkIfValidOldPassword(user, input)) {
            FacesMessage msg = new FacesMessage("Old password invalid", "");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }
        String newPassword = facesContext.getExternalContext().getRequestParameterMap().get("changePasswordForm:newPassword");

        if (input.equals(newPassword))  {
            FacesMessage msg = new FacesMessage("The new password must be different from the old one", "");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }
    }
}
