package de.sybig.tfclass;

import de.sybig.oba.client.OboConnector;
import de.sybig.oba.client.tfclass.TFClassConnector;

/**
 *
 * @author juergen.doenitz@bioinf.med.uni-goettingen.de
 */
public class ObaProvider {

    private static ObaProvider instance;
    private final OboConnector connector;
    private final OboConnector connectorMouse;
    private final TFClassConnector connector3;

    private ObaProvider() {
        // private because singleton
        connector = new OboConnector("TFClass-human");
        connectorMouse = new OboConnector("TFClass-mouse");
        connector3 = new TFClassConnector();
        connector3.setBaseURI("http://oba:9998");
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

    public TFClassConnector getConnector3() {
        return connector3;
    }
}
