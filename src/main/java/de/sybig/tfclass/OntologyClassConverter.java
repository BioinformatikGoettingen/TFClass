package de.sybig.tfclass;

import de.sybig.oba.client.OboClass;
import de.sybig.oba.client.OboConnector;
import java.util.HashMap;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converts an ontology class to a string and back again. As unique identifier
 * of an ontology class its name, without the name space, is chosen. The
 * converted objects are stored in a static map to retrieve the ontology class
 * for a key. If the user does not select an option from the suggestion list,
 * but type the name, the value of the input field is submitted to this
 * converter and not the the value of the option of the list. Therefor the
 * labels of the ontology classes are also stored as keys.
 * 
 * @author juergen.doenitz@bioinf.med.uni-goettingen.de
 */
@FacesConverter(value = "ontologyClassConverter")
public class OntologyClassConverter implements Converter {

    private static volatile Map<String, Object> cache;
    private static Logger log = LoggerFactory
	    .getLogger(OntologyClassConverter.class);
    private HashMap<String, Object> labelCache;

    public OntologyClassConverter() {
	super();
    }

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent component,
	    String submittedValue) {
	log.trace("request conversion for {} from {}", submittedValue,
		getCache().size());
	if (submittedValue == null || submittedValue.trim().equals("")) {
	    return null;
	} else {
	    if (getCache().containsKey(submittedValue)) {
		return getCache().get(submittedValue);
	    }
	    log.warn(
		    "Could not convert string '{}' back to object, string not found in map {}",
		    submittedValue, getCache().size());
             return null;
	}
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent component,
	    Object value) {
	log.debug("converting String '{}' to class ", value);
	if (value == null || value.equals("")) {
	    return "";
	} else {
	    String name = ((OboClass) value).getName();
	    getCache().put(name, value);
	    getCache().put(((OboClass) value).getLabel(), value);
	    return name;
	}
    }

    private Map<String, Object> getCache() {
	if (cache == null) {
	    cache = new HashMap<String, Object>();
	}
	return cache;
    }
}
