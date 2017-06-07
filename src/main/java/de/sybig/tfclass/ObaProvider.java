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
    private final OboConnector connector3;

    private ObaProvider() {
        // private because singleton
        connector = new OboConnector("TFClass-human");
        connectorMouse = new OboConnector("TFClass-mouse");
        connector3 = new OboConnector("tfclass");
        connector3.setBaseURI("http://localhost:9998");
//        connector.setBaseURI("http://speedy2:9997/");
    }

    public static ObaProvider getInstance() {
        if (instance == null) {
            instance = new ObaProvider();
        }
        return instance;
    }

    public OboConnector getConnectorHuman() {
        return connector;
    }

    public OboConnector getConnectorMouse() {
        return connectorMouse;
    }

    public OboConnector getConnector3() {
        return connector3;
    }
}
