package de.sybig.tfclass;

import de.sybig.oba.client.OboClass;
import de.sybig.oba.client.OboConnector;
import de.sybig.oba.server.JsonAnnotation;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author juergen.doenitz@bioinf.med.uni-goettingend.e
 */
@ManagedBean
@ApplicationScoped
public class SpeciesBean {

    private final static Logger log = LoggerFactory.getLogger(SpeciesBean.class);
    private final OboConnector connector;
    private Set species;
    private final Map<String, String> scientificNames = new HashMap<String, String>();
    private final Map<String, String> speciesNames = new HashMap<String, String>();
    private final Map<String, String> genBankNames = new HashMap<String, String>();

    public SpeciesBean() {
        System.out.println("New species bean");
        connector = ObaProvider.getInstance().getConnector3();
    }
    public String getScientificName(String taxon) {
        if (taxon.startsWith(taxon)){
            taxon = taxon.substring(1, taxon.length());
        }
        System.out.println("taxon "+ taxon);
        if (!scientificNames.containsKey(taxon)) {
            scientificNames.put(taxon, getAnnotation(taxon, "label"));
        }
        System.out.println("- " + scientificNames.get(taxon));
        return scientificNames.get(taxon);
    }
    
    public String getCommonName(String taxon) {
        if (!speciesNames.containsKey(taxon)) {
            speciesNames.put(taxon, getAnnotation(taxon, "common_name"));
        }
        return speciesNames.get(taxon);
    }

    public String getGenBankName(String taxon) {
        if (!genBankNames.containsKey(taxon)) {
            genBankNames.put(taxon, getAnnotation(taxon, "genbank_common_name"));
        }
        return genBankNames.get(taxon);
    }

    private String getAnnotation(String taxon, String annotation) {
        for (OboClass species : getSpecies()) {
            if (species.getName().equals(taxon)) {
                Set<JsonAnnotation> annotationValues = species.getAnnotationValues(annotation);
                if (!annotationValues.isEmpty()) {
                    return annotationValues.iterator().next().getValue();

                }
                return null;
            }
        }
        return null;
    }

    private Set<OboClass> getSpecies() {
        if (species == null) {
            try {
                OboClass speciesRoot = connector.searchCls("species").getEntities().get(0);
                species = speciesRoot.getChildren();
            } catch (ConnectException ex) {
                log.error("could not get root node for the species", ex);
            }
        }
        return species;
    }
}
