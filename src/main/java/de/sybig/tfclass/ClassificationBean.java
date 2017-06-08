package de.sybig.tfclass;

import de.sybig.oba.client.OboClass;
import de.sybig.oba.client.OboConnector;
import de.sybig.oba.server.JsonAnnotation;
import de.sybig.oba.server.JsonObjectPropertyExpression;
import java.util.HashSet;
import java.util.Set;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author juergen.doenitz@bioinf.med.uni-goettingen.de
 */
@ManagedBean
@ViewScoped
public class ClassificationBean {

    private final OboConnector connector;
    private static final Logger log = LoggerFactory.getLogger(ClassificationBean.class);

    private ClassificationTree tfTree;
    private TreeNode selectedNode;

    public ClassificationBean() {
        super();
        this.connector = ObaProvider.getInstance().getConnector3();
    }

    public ClassificationTree getTfTree() {
        if (tfTree == null) {
            tfTree = new ClassificationTree(connector);
        }
        return tfTree;
    }

    public Set<OboClass> getSpeciesOfSelectedNode() {
        if (selectedNode == null) {
            return null;
        }

        OboClass oc = (OboClass) selectedNode.getData();
        if (oc == null || oc.getProperties() == null) {
            return null;
        }
        Set<OboClass> speciesList = new HashSet<OboClass>();
        for (JsonObjectPropertyExpression props : (Set<JsonObjectPropertyExpression>) oc.getProperties()) {
            if ("contains".equals(props.getProperty().getName())) { //todo use restriction object?
                speciesList.add((OboClass) props.getTarget().getParents().iterator().next());
            }
        }
        return speciesList;
    }

    public TreeNode getClassificationRoot() {
        return getTfTree().getRoot();
    }

    public String getAnnotationValueOfSelectedNode(String annotation) {
        if (selectedNode == null) {
            return null;
        }
        OboClass oc = (OboClass) selectedNode.getData();
        Set<JsonAnnotation> annotations = oc.getAnnotationValues(annotation);
        if (annotations == null || annotations.isEmpty()) {
            return null;
        }
        return annotations.iterator().next().getValue();
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }
}
