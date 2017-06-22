package de.sybig.tfclass;

import de.sybig.oba.client.OboClass;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import org.primefaces.model.TreeNode;

/**
 *
 * @author juergen.doenitz@bioinf.med.uni-goettingen.de
 */

@ManagedBean
@ViewScoped
public class SpeciesSelection {
    
    TreeNode[] selectedNodes;
    @ManagedProperty(value="#{dashboard}")
    DetailsDashboard dashboard;
    @ManagedProperty(value="#{speciesTree}")
    SpeciesTree speciesTree;

    public TreeNode[] getSelectedNodes() {
        if (selectedNodes == null){
            selectedNodes = getDefaultSpecies();
        }
        
//        Set<TreeNode> set = new HashSet<TreeNode>(Arrays.asList(selectedNodes));
//        selectedNodes = set.toArray(selectedNodes);
        //System.out.println("getting selected species " + selectedNodes.length);
        return selectedNodes;
    }

    public void setSelectedNodes(TreeNode[] selectedNodes) {
        
        Set<TreeNode> selSet = new HashSet<TreeNode>();
        for (TreeNode tn : selectedNodes){
            if (! selSet.contains(tn)){
                selSet.add(tn);
            }else{
                System.out.println("removing " + tn);
            }
        }
        this.selectedNodes = selSet.toArray(new TreeNode[selSet.size()]);
        //System.out.println("setting selected nodes " + this.selectedNodes);
    }
    
    public void update(){
        System.out.println("updating ....");
        List<OboClass> selected = new LinkedList<OboClass>();
        for (TreeNode s: getSelectedNodes()){
            selected.add((OboClass) s.getData());
        }
        dashboard.setSelectedSpecies(selected);
        
    }
    public void setDashboard(DetailsDashboard dashboard){
        this.dashboard = dashboard;
    }
    public void setSpeciesTree(SpeciesTree tree){
        this.speciesTree = tree;
    }

    private TreeNode[] getDefaultSpecies() {
        System.out.println("setting default species");
        TreeNode root = speciesTree.getRoot();
        TreeNode node = searchSpecies(speciesTree.getRoot(), "9606"); //human
        TreeNode[] defaultSpecies = new TreeNode[1];
        defaultSpecies[0] = node;
        return defaultSpecies;
        
    }
    private TreeNode searchSpecies(TreeNode node, String name){
        if ( ((OboClass)node.getData()).getName().equals(name)){
            return node;
        }
        for (TreeNode child: node.getChildren()){
            TreeNode foundNode = searchSpecies(child, name);
            if (foundNode != null){
                return foundNode;
            }
        }
        return null;
    }
}
