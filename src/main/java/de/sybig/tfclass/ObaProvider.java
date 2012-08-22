package de.sybig.tfclass;

import de.sybig.oba.client.OboConnector;

/**
 *
 * @author juergen.doenitz@bioinf.med.uni-goettingen.de
 */
public class ObaProvider {
    private static ObaProvider instance;
    private final OboConnector connector;


    private ObaProvider(){
        // private because singleton
        connector = new OboConnector("TFClass");
        connector.setBaseURI("http://localhost:9998/");
    }
    public static ObaProvider getInstance(){
        if (instance == null){
            instance  = new ObaProvider();
        }
        return instance;
    }

    public OboConnector getConnector() {
        return connector;
    }
    
}
