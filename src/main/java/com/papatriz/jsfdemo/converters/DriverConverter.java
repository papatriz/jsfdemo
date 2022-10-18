package com.papatriz.jsfdemo.converters;

import com.papatriz.jsfdemo.models.Driver;
import com.papatriz.jsfdemo.services.IDriverService;

import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@RequestScoped
public class DriverConverter implements Converter {
    @Named(value = "driverService")
    @Inject
    private final IDriverService driverService;

    public DriverConverter(IDriverService driverService) {
        this.driverService = driverService;
    }


    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) throws ConverterException {
        if (s==null || s.isEmpty()) return  null;
        String idStr = s.substring(s.indexOf("id:")+4);
        int id = Integer.parseInt(idStr);
        Driver d = null;
        try {
         d = driverService.getDriverById(id);
        }
        catch (Exception e)
        {
            System.out.println("Have got exception "+e.getMessage());
        }

        return  d;
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) throws ConverterException {
        if ((o == null)||(o.toString().equals("")))  return "";
        Driver d = (Driver) o;
        return d.toString();
    }
}
