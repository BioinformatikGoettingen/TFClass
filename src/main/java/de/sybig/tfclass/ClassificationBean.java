package de.sybig.tfclass;

import de.sybig.oba.client.OboClass;
import de.sybig.oba.client.OboConnector;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.model.DefaultTreeNode;
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

    public TreeNode getClassificationRoot() {
        
        return getTfTree().getRoot();
    }

}
