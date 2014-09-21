package de.sybig.tfclass;

import de.sybig.oba.client.OboClass;
import de.sybig.oba.client.OboConnector;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.model.TreeNode;

/**
 *
 * @author jdo
 */
@ManagedBean
@ViewScoped
public class TreeBean {

    private OboConnector connector;
    private String species;
    private TfTree tfTree;
    private TreeNode selectedNode;

    public TreeBean(OboConnector connector, String title) {
        super();
        this.connector = connector;
        this.species = title;
    }

    public void expandAll() {
        getTfTree().expandTree();
    }

    public void expandSelected() {
        getTfTree().expandTree(getSelectedNode());
    }

    /**
     * Unsets the selected node and collapse all nodes in the tree object.
     */
    public void collapseAll() {
        selectedNode = null;
        getTfTree().collapseTree();
    }

    public void onNodeCollapse(NodeCollapseEvent event) {
        getTfTree().collapseTree(event.getTreeNode());
    }

    public void expandToSuperClass() {
        getTfTree().expandToSubSet("Superclass");
    }

    public void expandToClass() {
        getTfTree().expandToSubSet("Class");
    }

    public void expandToFamily() {
        getTfTree().expandToSubSet("Family");
    }

    public void expandToSubfamily() {
        getTfTree().expandToSubSet("Subfamily");
    }

    public void expandToGenus() {
        getTfTree().expandToSubSet("Genus");
    }

    public void expandToFactorSpecies() {
        getTfTree().expandToSubSet("Factor species");
    }

    public void expandCurrentToClass() {
        if (getSelectedNode() == null) {
            return;
        }
        getTfTree().expandToSubSet("Class", getSelectedNode());
    }

    public void expandCurrentToFamily() {
        if (getSelectedNode() == null) {
            return;
        }
        getTfTree().expandToSubSet("Family", getSelectedNode());
    }

    public void expandCurrentToSubfamily() {
        if (getSelectedNode() == null) {
            return;
        }
        getTfTree().expandToSubSet("Subfamily", getSelectedNode());
    }

    public void expandCurrentToGenus() {
        if (getSelectedNode() == null) {
            return;
        }
        getTfTree().expandToSubSet("Genus", getSelectedNode());
    }

    public void expandCurrentToFactorSpecies() {
        if (getSelectedNode() == null) {
            return;
        }
        getTfTree().expandToSubSet("Factor species", getSelectedNode());
    }

    public TreeNode getTfRoot() {
        return getTfTree().getRoot();
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public void setSelectedNode(OboClass cls) {
//         if (selectedOba.getSubsets().equals("Genus") || selectedOba.getSubsets().equals("Factor species"))
        if (cls.getSubsets().equals("Factor species")) {
            cls = (OboClass) cls.getParents().iterator().next();
        }
        while (cls.getParents() != null && cls.getParents().size() > 0) {
            OboClass selectedCls = connector.getCls(cls.getName(), null);
            if (selectedCls == null) {
                cls = (OboClass) cls.getParents().iterator().next();
                continue;
            }
            
            if (selectedNode != null) {
                selectedNode.setSelected(false);
            }
            collapseAll();
            selectedNode = tfTree.expandNode(selectedCls);
            selectedNode.setSelected(true);
            return;
        }

    }

    public TfTree getTfTree() {
        if (tfTree == null) {
            tfTree = new TfTree(connector);
        }
        return tfTree;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String title) {
        this.species = title;
    }

}
