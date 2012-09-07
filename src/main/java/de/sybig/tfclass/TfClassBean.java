package de.sybig.tfclass;

import de.sybig.oba.client.OboClass;
import de.sybig.oba.client.OboClassList;
import de.sybig.oba.client.OboConnector;
import de.sybig.oba.server.JsonAnnotation;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.NormaltissueFacadeREST;

/**
 *
 * @author juergen.doenitz@bioinf.med.uni-goettingen.de
 */
@ManagedBean
@ViewScoped
public class TfClassBean {

    private static final Logger log = LoggerFactory.getLogger(TfClassBean.class);
    @EJB
    NormaltissueFacadeREST ntf;
    private TfTree tfTree;
    private OboClass searchedClass;
    private TreeNode selectedNode;

    @PostConstruct
    void initialiseSession() {
        FacesContext.getCurrentInstance().getExternalContext().getSession(true);
    }

    public List<OboClass> search(String pattern) {
        System.out.println("searching for " + pattern);
        String searchPattern = pattern;
        if (!searchPattern.endsWith("*")) {
            searchPattern = searchPattern + "*";
        }
        OboConnector connector = ObaProvider.getInstance().getConnector();
        OboClassList searchResult = connector.searchCls(searchPattern);
        if (searchResult == null || searchResult.getEntities() == null) {
            return null;
        }
        return searchResult.getEntities();
    }

    public TreeNode getTfRoot() {
        return getTfTree().getRoot();
    }

    public List getExpressionTable() {
        TreeNode selected = getSelectedNode();
        if (selected == null) {
            return null;
        }
        Set<JsonAnnotation> annotations = ((OboClass) selectedNode.getData()).getAnnotations();
        for (JsonAnnotation a : annotations) {
            if (a.getName().equals("xref") && a.getValue().startsWith("ENSEMBL")) {
                System.out.println(a.getValue());
                Pattern regex = Pattern.compile("ENSEMBL:(ENSG\\d{11})[^\\w]*\"?([\\w\\s]*).*$");
                Matcher matcher = regex.matcher(a.getValue());
                String ensid = matcher.replaceAll("$1");
                return ntf.getWithEnsemblId(ensid);
            }
        }
//        ntf.getWithEnsemblId(getSelectedNode().g

        return null;
    }

    public void selectSearched() {
        TreeNode last = getTfTree().expandNode(getSearchedClass());
        last.setSelected(true);
    }

    public void expandAll() {
        getTfTree().expandTree();
    }

    public void expandSelected() {
//     System.out.println("expanding selected " + getSelectedNode());
        getTfTree().expandTree(getSelectedNode());
    }

    public void collapseAll() {
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

    public OboClass getSearchedClass() {
        return searchedClass;
    }

    public void setSearchedClass(OboClass searchedClass) {
//        System.out.println("searched " + searchedClass);
        this.searchedClass = searchedClass;
    }

    private TfTree getTfTree() {
        if (tfTree == null) {
            tfTree = new TfTree();
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

    public String getDefinition() {
        if (selectedNode == null) {
            return null;
        }
        String def = ((OboClass) selectedNode.getData()).getDefinition();
        if (def == null) {
            return null;
        }
        if (def.endsWith(" []")) {
            def = def.substring(0, def.length() - 3);
        }
        def = def.substring(1, def.length() - 1);
        def = replacePubMed(def);
        return def;
    }

    public String getProteinAtlas() {
        if (selectedNode == null) {
            return null;
        }
        Set<JsonAnnotation> annotations = ((OboClass) selectedNode.getData()).getAnnotations();
        for (JsonAnnotation a : annotations) {
            if (a.getName().equals("xref") && a.getValue().startsWith("ENSEMBL")) {
                return parseProteinAtlas(a.getValue());
            }
        }
        return null;
    }

    public String getTransfac() {
        if (selectedNode == null) {
            return null;
        }
        Set<JsonAnnotation> annotations = ((OboClass) selectedNode.getData()).getAnnotations();
        for (JsonAnnotation a : annotations) {
            if (a.getName().equals("xref") && a.getValue().startsWith("TRANSFAC")) {
                return parseTransfac(a.getValue());
            }
        }
        return null;
    }

    public String getUniprot() {
        if (selectedNode == null) {
            return null;
        }
        Set<JsonAnnotation> annotations = ((OboClass) selectedNode.getData()).getAnnotations();
        for (JsonAnnotation a : annotations) {
            if (a.getName().equals("xref") && a.getValue().startsWith("UNIPROT")) {
                return parseUniprot(a.getValue());
            }
        }
        return null;
    }

    private String replacePubMed(String text) {
        Pattern regex = Pattern.compile("PMID (\\d{7})");
        Matcher matcher = regex.matcher(text);
        String output = matcher.replaceAll("<a href=\"http://www.ncbi.nlm.nih.gov/pubmed/$1\">PMID $1</a>");
        return output;
    }

    private String parseProteinAtlas(String annoValue) {
        Pattern regex = Pattern.compile("ENSEMBL:(ENSG\\d{11})[^\\w]*([\\w\\s]*)");
        Matcher matcher = regex.matcher(annoValue);
        if (matcher.groupCount() > 1) {
            return matcher.replaceAll("<a href=\"http://www.proteinatlas.org/$1\">$1</a> ($2)");
        }
        return matcher.replaceAll("<a href=\"http://www.proteinatlas.org/$1\">$1</a> ($2)");

    }

    private String parseTransfac(String annoValue) {
        Pattern regex = Pattern.compile("TRANSFAC:([\\w\\d]{11}).*");
        Matcher matcher = regex.matcher(annoValue);
        return matcher.replaceAll("<a href=\"https://portal.biobase-international.com/cgi-bin/knowledgebase/pageview.cgi?view=LocusReport&protein_acc=$1\">$1</a>");
    }

    private String parseUniprot(String annoValue) {
        Pattern regex = Pattern.compile("UNIPROT:([\\w\\d]{6})");
        Matcher matcher = regex.matcher(annoValue);
        return matcher.replaceAll("<a href=\"http://www.uniprot.org/uniprot/$1\">$1</a>");
    }
}
