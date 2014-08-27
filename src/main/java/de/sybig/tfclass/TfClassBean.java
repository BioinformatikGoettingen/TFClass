package de.sybig.tfclass;

import de.sybig.oba.client.OboClass;
import de.sybig.oba.client.OboClassList;
import de.sybig.oba.client.OboConnector;
import de.sybig.oba.server.JsonAnnotation;
import de.sybig.palinker.NormalTissueCytomer;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import org.primefaces.component.tabview.TabView;
import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.TabChangeEvent;
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
    private NormaltissueFacadeREST ntf;
    private static final String LOGOTABID = "logoTab";
    private static final String EXPRESSTABID = "expressionTab";
//    private TfTree tfTree;
    private OboClass searchedClass;

    private Map<String, List<NormalTissueCytomer>> tissueMap = new HashMap<String, List<NormalTissueCytomer>>();
    private List<NormalTissueCytomer> filteredTissues;
    private LinkedList<String> fieldList;
    private TfTree tfTree2;
    private TreeBean firstTree;
    private TreeBean humanTree;
    private TreeBean secondTree;
    private TreeBean mouseTree;
    private TreeNode selectedNode1;

    @PostConstruct
    void initialiseSession() {
        FacesContext.getCurrentInstance().getExternalContext().getSession(true);
//        System.out.println("session ready");
    }

    public String init() {
        // used on the top of the page to init the bean
        humanTree = firstTree = new TreeBean(ObaProvider.getInstance().getConnectorHuman(), "Human transcription factors");
        mouseTree = secondTree = new TreeBean(ObaProvider.getInstance().getConnectorMouse(), "Mouse transcription facotrs");
        try {
            String idString = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("tfclass");
            if (idString != null) {
                OboClass cls = ObaProvider.getInstance().getConnectorHuman().getCls(idString, null);
                if (cls == null) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "No valid result could be found for parameter " + idString, null));
                    return "";
                }
                searchedClass = cls;
                selectSearched();
                log.info("navigate to class {} by url parameter tfclass=", cls);
                return "";
            }
            String ext = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("ext");
            if (ext == null || ext.length() < 1) {
                ext = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("uniprot");
            }
            if (ext != null) {
                OboClassList resultList = ObaProvider.getInstance().getConnectorHuman().searchCls(ext);
                if (resultList == null || resultList.size() != 1) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "No valid result could be found for parameter " + ext, null));
                    return "";
                }
                OboClass cls = resultList.getEntities().get(0);
                if (cls == null) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "No valid result could be found for parameter " + ext, null));
                    return "";
                }
                searchedClass = cls;
                selectSearched();
                log.info("navigate to class {} specified in the url wiht ext parameter {}", cls, ext);
                return "";

            }
        } catch (NumberFormatException e) {
            log.error("The id '{}' parsed from the URL for the detail view is not valid");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "No valid result could be found for this parameter ", null));
        } catch (Exception e) {
            log.error("Error while parsing URL parameter " + e);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "No valid result could be found for this parameter ", null));
        }

        return "";
    }

    /**
     * Searches in the ontology mapped to the main tree for the search pattern.
     * A list of matching {@see OboClass} is returned. If the search returns no hits
     * <code>null</code> is returned.
     *<br />
     * This function is used for the autocomplete function, the search result is selected with {@see #selectSearched}
     * @param pattern The pattern to search
     * @return List of matching classes or <code>null</code>
     */
    public List<OboClass> search(String pattern) {
        String searchPattern = pattern;
        try {
            OboConnector connector = getFirstConnector();
            OboClassList searchResult = connector.searchCls(searchPattern, getFieldList());
            if (searchResult == null || searchResult.getEntities() == null) {
                return null;
            }
            return searchResult.getEntities();
        } catch (Exception ex) {
            log.warn("An error occured while searching in the ontology ", ex);
            return null;
        }
    }
    public void switchTrees(){
        TreeBean tmpTree = firstTree;
        firstTree = secondTree;
        secondTree = tmpTree;
    }
    
    public TreeBean getFirstTree() {
        return firstTree;
    }

    public void setFirstTree(TreeBean firstTree) {
        this.firstTree = firstTree;
    }

    public TreeBean getHumanTree() {
        return humanTree;
    }

    public void setHumanTree(TreeBean humanTree) {
        this.humanTree = humanTree;
    }

    public TreeBean getSecondTree() {
        return secondTree;
    }

    public void setSecondTree(TreeBean secondTree) {
        this.secondTree = secondTree;
    }

    public TreeBean getMouseTree() {
        return mouseTree;
    }

    public void setMouseTree(TreeBean mouseTree) {
        this.mouseTree = mouseTree;
    }

    public TreeNode getSelectedNode1() {
        return firstTree.getSelectedNode();
//        return selectedNode1;
    }
   
    public void setSelectedNode1(TreeNode selectedNode1) {
        firstTree.setSelectedNode(selectedNode1);
        OboClass selectedOba = (OboClass)selectedNode1.getData();
        if (selectedOba.getSubsets().equals("Genus") || selectedOba.getSubsets().equals("Factor species")) {
            secondTree.setSelectedNode(selectedOba.getName());
        }
        this.selectedNode1 = selectedNode1;
    }
    public TreeNode getSelectedNode2(){
        System.out.println("getting selected node 2" + secondTree.getSelectedNode());
        return secondTree.getSelectedNode();
    }
    public void setSelectedNode2(TreeNode node){
        secondTree.setSelectedNode(node);
    }

    /**
     * Get the Obo connector for the main tree.
     *
     * @return
     */
    private OboConnector getFirstConnector() {
        return ObaProvider.getInstance().getConnectorHuman();
    }

    private List<String> getFieldList() {
        if (fieldList == null) {
            fieldList = new LinkedList<String>();
            fieldList.add("className");
            fieldList.add("label");
            fieldList.add("xref");
        }
        return fieldList;
    }

   

//    public TreeNode getTfRoot2() {
//        return getTfTree2().getRoot();
//    }

    public List<NormalTissueCytomer> getExpressionTable() {
        TreeNode selected = humanTree.getSelectedNode();
        if (selected == null) {
            return null;
        }
        try {
            Set<JsonAnnotation> annotations = ((OboClass) selected.getData()).getAnnotations();
            for (JsonAnnotation a : annotations) {
                if (a.getName().equals("xref") && a.getValue().startsWith("ENSEMBL")) {
                    Pattern regex = Pattern.compile("ENSEMBL:(ENSG\\d{11})[^\\w]*\"?([\\w\\s]*).*$");
                    Matcher matcher = regex.matcher(a.getValue());
                    String ensid = matcher.replaceAll("$1");
                    if (!tissueMap.containsKey(ensid)) {
                        tissueMap.put(ensid, ntf.getWithEnsemblId(ensid));
                    }
                    return tissueMap.get(ensid);
                }
            }
        } catch (Exception e) {
            log.error("An error occured while getting the expression table {}", e.getMessage());
        }
        TabView tabView = ((TabView) FacesContext.getCurrentInstance().getViewRoot().findComponent("tfForm:tabView"));
        if (tabView.getChildren().get(tabView.getActiveIndex()).getId().equals(EXPRESSTABID)) {
            tabView.setActiveIndex(0);
        }
        return null;
    }

    public List<NormalTissueCytomer> getFilteredTissues() {
        return filteredTissues;
    }

    public void setFilteredTissues(List<NormalTissueCytomer> filteredTissues) {
        this.filteredTissues = filteredTissues;
    }

    public SelectItem[] getLevelOptions() {
        if (getExpressionTable() == null) {
            return null;
        }
        List<String> levels = new LinkedList<String>();
        for (NormalTissueCytomer t : getExpressionTable()) {
            if (!levels.contains(t.getLevel())) {
                levels.add(t.getLevel());
            }
        }
        Collections.sort(levels);
        SelectItem[] options = new SelectItem[levels.size() + 1];
        options[0] = new SelectItem("", "Select");
        for (int i = 0; i < levels.size(); i++) {
            options[i + 1] = new SelectItem(levels.get(i), levels.get(i));

        }
        return options;
    }


    public void selectSearched() {
        firstTree.collapseAll();
        TreeNode last = firstTree.getTfTree().expandNode(getSearchedClass());
        if (last == null) {
            System.out.println(last + " for " + getSearchedClass());
            return;
        }
        last.setSelected(true);
        firstTree.setSelectedNode (last);
    }

   

    public OboClass getSearchedClass() {
        return searchedClass;
    }

    public void setSearchedClass(OboClass searchedClass) {
        this.searchedClass = searchedClass;
    }

   

    private TfTree getTfTree2() {
        if (tfTree2 == null) {
            tfTree2 = new TfTree(ObaProvider.getInstance().getConnectorMouse());
        }
        return tfTree2;
    }

  

    public String getDefinition() {
        if (humanTree.getClass() == null) {
            return null;
        }
        String def = ((OboClass) humanTree.getSelectedNode().getData()).getDefinition();
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

    public String getAltID() {
        if (humanTree.getSelectedNode() == null) {
            return null;
        }
        Set<JsonAnnotation> annotations = ((OboClass) humanTree.getSelectedNode().getData()).getAnnotations();
        for (JsonAnnotation a : annotations) {
            if (a.getName().equals("alt_id")) {
                return a.getValue();
            }
        }
        return null;
    }

    public void jumpToAlternative(String id) {
        OboConnector connector = ObaProvider.getInstance().getConnectorHuman();
        OboClass cls = connector.getCls(id, null);
        setSearchedClass(cls);
        selectSearched();
    }

    public String getProteinAtlas() {
        if (humanTree.getSelectedNode() == null) {
            return null;
        }
        Set<JsonAnnotation> annotations = ((OboClass) humanTree.getSelectedNode().getData()).getAnnotations();
        for (JsonAnnotation a : annotations) {
            if (a.getName().equals("xref") && a.getValue().startsWith("ENSEMBL")) {
                return parseProteinAtlas(a.getValue());
            }
        }
        return null;
    }

    public String getBioGPS() {
        if (humanTree.getSelectedNode() == null) {
            return null;
        }
        Set<JsonAnnotation> annotations = ((OboClass) humanTree.getSelectedNode().getData()).getAnnotations();
        for (JsonAnnotation a : annotations) {
            if (a.getName().equals("xref") && a.getValue().startsWith("ENSEMBL")) {
                return parseBioGPS(a.getValue());
            }
        }
        return null;
    }

    public String getTransfac() {
        if (humanTree.getSelectedNode() == null) {
            return null;
        }
        Set<JsonAnnotation> annotations = ((OboClass) humanTree.getSelectedNode().getData()).getAnnotations();
        for (JsonAnnotation a : annotations) {
            if (a.getName().equals("xref") && a.getValue().startsWith("TRANSFAC")) {
                return parseTransfac(a.getValue());
            }
        }
        return null;
    }

    public String getUniprot() {
        if (humanTree.getSelectedNode() == null) {
            return null;
        }
        Set<JsonAnnotation> annotations = ((OboClass) humanTree.getSelectedNode().getData()).getAnnotations();
        for (JsonAnnotation a : annotations) {
            if (a.getName().equals("xref") && a.getValue().startsWith("UNIPROT")) {
                return parseUniprot(a.getValue());
            }
        }
        return null;
    }

    public String getLogoAdress() {
        if (humanTree.getSelectedNode() == null) {
            return null;
        }
        Set<JsonAnnotation> annotations = ((OboClass) humanTree.getSelectedNode().getData()).getAnnotations();
        for (JsonAnnotation a : annotations) {
            if (a.getName().equals("xref") && a.getValue().startsWith("LOGOPNGLINK")) {
                return a.getValue().replace("LOGOPNGLINK:http\\://www.edgar-wingender.de/logos", "");
            }
        }
        return null;
    }

    public String getLogoDescription() {
        if (humanTree.getSelectedNode() == null) {
            return null;
        }
        Set<JsonAnnotation> annotations = ((OboClass) humanTree.getSelectedNode().getData()).getAnnotations();
        for (JsonAnnotation a : annotations) {
            if (a.getName().equals("xref") && a.getValue().startsWith("LOGODESCRIPTIONLINK")) {
                return a.getValue().replace("LOGODESCRIPTIONLINK:http\\://www.edgar-wingender.de/library", "");
            }
        }
        TabView tabView = ((TabView) FacesContext.getCurrentInstance().getViewRoot().findComponent("tfForm:tabView"));
        if (tabView.getChildren().get(tabView.getActiveIndex()).getId().equals(LOGOTABID)) {
            tabView.setActiveIndex(0);
        }
        return null;
    }

    public void tabClose(TabChangeEvent event) {
        System.out.println("closed " + event);
    }

    public String getClassLink() {
        if (humanTree.getSelectedNode() == null) {
            return null;
        }
        Set<JsonAnnotation> annotations = ((OboClass) humanTree.getSelectedNode().getData()).getAnnotations();
        for (JsonAnnotation a : annotations) {
            if (a.getName().equals("xref") && a.getValue().startsWith("CLASSLINK")) {
                return a.getValue().substring(10).replace("\\", "");
            }
        }
        return null;
    }

 

    private String replacePubMed(String text) {
        Pattern regex = Pattern.compile("PMID (\\d{7})");
        Matcher matcher = regex.matcher(text);
        String output = matcher.replaceAll("<a href=\"http://www.ncbi.nlm.nih.gov/pubmed/$1\" target=\"_blank\" >PMID $1</a>");
        return output;
    }

    private String parseProteinAtlas(String annoValue) {
        Pattern regex = Pattern.compile("ENSEMBL:(ENSG\\d{11})[^\\w]*([\\w\\s]*)\"?");
        Matcher matcher = regex.matcher(annoValue);
        if (matcher.groupCount() > 1) {
            return matcher.replaceAll("<a href=\"http://www.proteinatlas.org/$1\" target=\"_blank\" >$1</a> ($2)");
        }
        return matcher.replaceAll("<a href=\"http://www.proteinatlas.org/$1\" target=\"_blank\" >$1</a> ($2)");

    }

    private String parseBioGPS(String annoValue) {
        Pattern regex = Pattern.compile("ENSEMBL:(ENSG\\d{11})[^\\w]*([\\w\\s]*)\"?");
        Matcher matcher = regex.matcher(annoValue);

        return matcher.replaceAll("<a href=\"http://biogps.org/#goto=genereport&id=$1\" target=\"_blank\" >$1</a>");

    }

    private String parseTransfac(String annoValue) {
        Pattern regex = Pattern.compile("TRANSFAC:([\\w\\d]{11}).*");
        Matcher matcher = regex.matcher(annoValue);
        return matcher.replaceAll("<a href=\"https://portal.biobase-international.com/cgi-bin/knowledgebase/pageview.cgi?view=LocusReport&protein_acc=$1\" target=\"_blank\" >$1</a>");
    }

    private String parseUniprot(String annoValue) {
        Pattern regex = Pattern.compile("UNIPROT:([\\w\\d]{6})");
        Matcher matcher = regex.matcher(annoValue);
        return matcher.replaceAll("<a href=\"http://www.uniprot.org/uniprot/$1\" target=\"_blank\" >$1</a>");
    }
}
