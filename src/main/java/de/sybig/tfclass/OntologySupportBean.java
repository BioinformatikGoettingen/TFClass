package de.sybig.tfclass;

import de.sybig.oba.client.OboClass;
import de.sybig.oba.client.OboClassList;
import de.sybig.oba.client.tfclass.TFClassConnector;
import de.sybig.oba.server.JsonAnnotation;
import de.sybig.oba.server.JsonCls;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.Set;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import org.primefaces.model.TreeNode;

/**
 *
 * @author juergen.doenitz@bioinf.med.uni-goettingen.de
 */
@ManagedBean
@ApplicationScoped
public class OntologySupportBean {

    private HashMap<JsonCls, OboClassList> speciesDownstreamMap;
    private HashMap<JsonCls, OboClassList> generaDownstreamMap;
    private final TFClassConnector connector;

    public OntologySupportBean() {
        super();
        this.connector = ObaProvider.getInstance().getConnector3();
    }

    public OboClassList getSpeciesDownstream(TreeNode selectedNode) throws ConnectException {
        if (selectedNode == null) {
            return null;
        }
        JsonCls node = (JsonCls) selectedNode.getData();
        if (speciesDownstreamMap == null) {
            speciesDownstreamMap = new HashMap<JsonCls, OboClassList>();
        }
        if (!speciesDownstreamMap.containsKey(node)) {
            speciesDownstreamMap.put(node, connector.getSpeciesDownstream((JsonCls) selectedNode.getData()));
        }
        return speciesDownstreamMap.get(node);
    }

    public OboClassList getGeneraDownstream(TreeNode selectedNode) throws ConnectException {
        if (selectedNode == null) {
            return null;
        }
        JsonCls node = (JsonCls) selectedNode.getData();
        if (generaDownstreamMap == null) {
            generaDownstreamMap = new HashMap<JsonCls, OboClassList>();
        }
        if (!generaDownstreamMap.containsKey(node)) {
            generaDownstreamMap.put(node, connector.getGeneraDownstream((JsonCls) selectedNode.getData()));
        }
        return generaDownstreamMap.get(node);
    }
    
    public String getLevel(TreeNode node){
        if (node == null){
            return null;
        }
        OboClass cls = (OboClass) node.getData();
        Set<JsonAnnotation> annotations = cls.getAnnotationValues("level");
        if (annotations != null && annotations.size() > 0){
            return annotations.iterator().next().getValue();
        }
        return null;
    }
}
