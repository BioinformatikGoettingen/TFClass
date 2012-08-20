package de.sybig.tfclass;

import com.sun.org.apache.regexp.internal.REUtil;
import de.sybig.oba.client.OboClass;
import de.sybig.oba.client.OboClassList;
import de.sybig.oba.client.OboConnector;
import java.util.List;
import javax.ejb.EJB;
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
public class TfClassBean {
    private static final Logger log = LoggerFactory.getLogger(TfClassBean.class);
    private TfTree tfTree;
    private OboClass searchedClass;
    private TreeNode selectedNode;

    public List<OboClass> search(String pattern){
        System.out.println("searching for " + pattern);
        String searchPattern = pattern;
        if (!searchPattern.endsWith("*")){
            searchPattern = searchPattern+"*";
        }
        OboConnector connector = ObaProvider.getInstance().getConnector();
        OboClassList searchResult = connector.searchCls(searchPattern);
        if (searchResult == null || searchResult.getEntities() == null){
            return null;
        }
        return searchResult.getEntities();
    }

    public TreeNode getTfRoot(){
        return getTfTree().getRoot();
    }
    public void selectSearched(){
        TreeNode last = getTfTree().expandNode(getSearchedClass());
        last.setSelected(true);
    }
    public void expandToClass(){
        getTfTree().expandToSubSet("Class");
    }
    public OboClass getSearchedClass() {
        return searchedClass;
    }

    public void setSearchedClass(OboClass searchedClass) {
        System.out.println("searched " + searchedClass);
        this.searchedClass = searchedClass;
    }

    private TfTree getTfTree(){
        if (tfTree == null){
            tfTree =  new TfTree();
        }
        return tfTree;
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
//        System.out.println("selected " + selectedNode);
        this.selectedNode = selectedNode;
    }

    

}
