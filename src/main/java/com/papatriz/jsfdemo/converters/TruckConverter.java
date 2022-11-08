package com.papatriz.jsfdemo.converters;

import com.papatriz.jsfdemo.models.main.Truck;
import com.papatriz.jsfdemo.services.ITruckService;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@RequestScoped
public class TruckConverter implements Converter {

    @Named("truckService")
    @Inject
    private final ITruckService service;

    public TruckConverter(@Qualifier("truckService") ITruckService service) {
        this.service = service;
    }

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) throws ConverterException {
        if (s==null || s.isEmpty()) return  null;
        String rn = s.substring(0, 7);

        return  service.getTruckById(rn).orElse(null);
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) throws ConverterException {
        if ((o == null)||(o.toString().equals("")))  return "";
        Truck oTruck = (Truck) o;
        return oTruck.toString();
    }
}
