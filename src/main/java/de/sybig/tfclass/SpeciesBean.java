package de.sybig.tfclass;

import de.sybig.oba.client.OboClass;
import de.sybig.oba.client.OboConnector;
import de.sybig.oba.server.JsonAnnotation;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
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
    private List<OboClass> species;
    private final Map<String, String> scientificNames = new HashMap<String, String>();
    private final Map<String, String> speciesNames = new HashMap<String, String>();
    private final Map<String, String> genBankNames = new HashMap<String, String>();
//    private ArrayList<OboClass> selectedSpecies;

    public SpeciesBean() {
        connector = ObaProvider.getInstance().getConnector3();
    }

    public String getScientificName(String taxon) {
        if (taxon.startsWith("s")) {
            taxon = taxon.substring(1, taxon.length());
        }
        if (!scientificNames.containsKey(taxon)) {
            scientificNames.put(taxon, getAnnotation(taxon, "label"));
        }
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

    public List<OboClass> getSpecies() {
        if (species == null) {
            try {
                OboClass speciesRoot = connector.searchCls("species").getEntities().get(0);
                Set speciesSet = speciesRoot.getChildren();
                species = new ArrayList<OboClass>(speciesSet);
                species.sort(new ScientificNameComparator());

            } catch (ConnectException ex) {
                log.error("could not get root node for the species", ex);
            }
        }
        return species;
    }



    public class ScientificNameComparator implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {

            OboClass cls1 = (OboClass) o1;
            OboClass cls2 = (OboClass) o2;

            String name1 = getScientificName(cls1.getName());
            String name2 = getScientificName(cls2.getName());
            if (name1 == null){
                return 1;
            }
            if (name2 == null){
                return -1;
            }
            return name1.compareTo(name2);
        }

    }
}
