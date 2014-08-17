package de.sybig.tfclass;

import de.sybig.oba.client.OboConnector;

/**
 *
 * @author juergen.doenitz@bioinf.med.uni-goettingen.de
 */
public class ObaProvider {
    private static ObaProvider instance;
    private final OboConnector connector;
    private final OboConnector connectorMouse;


    private ObaProvider(){
        // private because singleton
        connector = new OboConnector("TFClass-human");
        connectorMouse = new OboConnector("TFClass-mouse");
//        connector.setBaseURI("http://speedy2:9997/");
    }
    public static ObaProvider getInstance(){
        if (instance == null){
            instance  = new ObaProvider();
        }
        return instance;
    }

    public OboConnector getConnectorHuman() {
        return connector;
    }

    public OboConnector getConnectorMouse(){
        return connectorMouse;
    }
}
