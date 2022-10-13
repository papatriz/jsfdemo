package com.papatriz.jsfdemo.converters;

import com.papatriz.jsfdemo.models.Truck;
import com.papatriz.jsfdemo.services.ITruckService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
@Data
@FacesConverter(value = "truckConverter")
public class TruckConverter implements Converter {
    /* private final ITruckService service;

    @Inject
    public TruckConverter(ITruckService service) {
        this.service = service;
    }
*/
    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) throws ConverterException {
        return new Truck("XX66666");// service.getTruckById(s);
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) throws ConverterException {
        if ((o == null)||(o.toString().equals("")))  return "";
        Truck oTruck = (Truck) o;
        return oTruck.getRegNumber();
    }
}
