package de.sybig.tfclass;

import de.sybig.oba.client.OboClass;
import de.sybig.oba.client.OboClassList;
import de.sybig.oba.client.OboConnector;
import de.sybig.oba.server.JsonAnnotation;
import de.sybig.oba.server.JsonObjectPropertyExpression;
import de.sybig.palinker.NormalTissueCytomer;
import java.io.BufferedReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
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
public class ClassificationBean {

    @EJB
    private NormaltissueFacadeREST ntf;
    @ManagedProperty(value="#{speciesBean}")
    private SpeciesBean speciesBean;
    private final OboConnector connector;
    private static final Logger log = LoggerFactory.getLogger(ClassificationBean.class);

    private ClassificationTree classificationTree;
    private TreeNode selectedNode;
    private Map<String, List<OboClass>> downstreamOfSelected;
    private LinkedList<String> fieldList;
    private OboClass searchedClass;
    private Map<String, String> fastaMap;
    private Map<String, String> dbdFastaMap;

    private Map<String, String> proteinPhyMLMap;
    private Map<String, String> proteinWebprankMap;
    private Map<String, String> proteinPhyML2Map;

    private Map<String, String> proteinSlimPhyMLMap;
    private Map<String, String> proteinSlimWebprankMap;
    private Map<String, String> proteinSlimPhyML2Map;

    private Map<String, String> dbdPhyMLMap;
    private Map<String, String> dbdWebprankMap;
    private Map<String, String> dbdPhyML2Map;

    private Map<String, String> dbdSlimPhyMLMap;
    private Map<String, String> dbdSlimWebprankMap;
    private Map<String, String> dbdSlimPhyML2Map;
    
    private Map<String, String> protLogoPlotMap;
    
    private Map<String, List<NormalTissueCytomer>> tissueMap = new HashMap<String, List<NormalTissueCytomer>>();
    private static final String HUMAN = "9606";
    private List<NormalTissueCytomer> filteredTissues;

    public ClassificationBean() {
        super();
        this.connector = ObaProvider.getInstance().getConnector3();
    }

    public String init() {
        // used on the top of the page to init the bean

        try {
            String idString = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("tfclass");
            if (idString != null) {
                OboClass cls = ObaProvider.getInstance().getConnector3().getCls(idString, null);
                if (cls == null) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "No valid result could be found for parameter " + idString, null));
                    return "";
                }
                searchedClass = cls;
                selectSearched();
                log.info("navigate to class {} by url parameter tfclass={}", cls, idString);
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

    public ClassificationTree getTfTree() {
        if (classificationTree == null) {
            classificationTree = new ClassificationTree(connector);
        }
        return classificationTree;
    }

    public Set<String> getSpeciesOfSelectedNode() {
        if (selectedNode == null) {
            return null;
        }
        return getDownstreamOfSelected().keySet();
    }

    public boolean isSpeciesInSelectedNode(String taxon) {
        if (selectedNode == null || !selectedNode.getType().equals("Genus")) {
            return false;
        }
        return (getDownstreamOfSelected().keySet().contains(taxon));
    }

    public List<OboClass> search(String pattern) {
        String searchPattern = pattern;
        try {
            OboClassList searchResult = connector.searchCls(searchPattern + "*", getFieldList());
            if (searchResult == null || searchResult.getEntities() == null) {
                return null;
            }
            Set<OboClass> resultSet = new HashSet<OboClass>();
            for (OboClass resultCls : searchResult.getEntities()) {
                resultSet.add(getClassificationClass(resultCls));
            }
            ArrayList<OboClass> resultList = new ArrayList<OboClass>(resultSet);
            Collections.sort(resultList);
            return resultList;
        } catch (Exception ex) {
            log.warn("An error occured while searching in the ontology ", ex);
            return null;
        }
    }

    private OboClass getClassificationClass(OboClass origCls) throws ConnectException {
        OboClass cls2 = connector.getCls(origCls); // This is a workaround as long as the classes in a search result have the properties not set.
        if (cls2.getProperties() == null) {
            return cls2;
        }
        for (JsonObjectPropertyExpression prop : (Set<JsonObjectPropertyExpression>) cls2.getProperties()) {
            if (prop.getProperty().getName().equals("belongs_to")) {
                return (OboClass) prop.getTarget();
            }
        }
        return cls2;
    }

    public OboClass getSearchedClass() {
        return searchedClass;
    }

    public void setSearchedClass(OboClass searchedClass) {
        this.searchedClass = searchedClass;
    }

    public TreeNode selectSearched() {
        collapseAll();
        TreeNode last = classificationTree.expandNode(getSearchedClass());
        if (last == null) {
            return null;
        }
        last.setSelected(true);
        setSelectedNode(last);
        return last;
    }

    public void onNodeCollapse(NodeCollapseEvent event) {
        getTfTree().collapseTree(event.getTreeNode());
    }

    public void collapseAll() {
        selectedNode = null;
        getTfTree().collapseTree();
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

    private Map<String, List<OboClass>> getDownstreamOfSelected() {
        if (selectedNode == null) {
            return null;
        }
        if (!selectedNode.getType().equals("Genus")) {
            return null;
        }
        if (downstreamOfSelected == null) {
            downstreamOfSelected = new HashMap<String, List<OboClass>>();
            OboClass oc = (OboClass) selectedNode.getData();
            if (oc == null || oc.getProperties() == null) {
                return downstreamOfSelected;
            }
            downstreamOfSelected = new HashMap<String, List<OboClass>>();
            for (JsonObjectPropertyExpression props : (Set<JsonObjectPropertyExpression>) oc.getProperties()) {
                if ("contains".equals(props.getProperty().getName())) { //todo use restriction object?
                    OboClass cls = (OboClass) props.getTarget();
                    OboClass parent = (OboClass) cls.getParents().iterator().next();
                    String taxon = ((JsonAnnotation) parent.getAnnotationValues("ncbi_taxon").iterator().next()).getValue();
                    if (!downstreamOfSelected.containsKey(taxon)) {
                        downstreamOfSelected.put(taxon, new LinkedList<OboClass>());
                    }
                    downstreamOfSelected.get(taxon).add(cls);
                }
            }
        }
        return downstreamOfSelected;
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
        downstreamOfSelected = null;
        this.selectedNode = selectedNode;
    }

    public List<NormalTissueCytomer> getExpressionTable(String taxonID) {
        if (!HUMAN.equals(taxonID)) {
            return null;
        }
        Map<String, List<OboClass>> downstream = getDownstreamOfSelected();
        if (downstream == null) {
            return null;
        }
        List<OboClass> humanNodes = downstream.get(HUMAN);
        if (humanNodes == null) {
            return null;
        }
        try {
            OboClass humanNode = humanNodes.get(0);
            Set<JsonAnnotation> annotations = humanNode.getAnnotations();
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
        return null;
    }

    public List<NormalTissueCytomer> getFilteredTissues() {
        return filteredTissues;
    }

    public void setFilteredTissues(List<NormalTissueCytomer> filteredTissues) {
        this.filteredTissues = filteredTissues;
    }

    public SelectItem[] getLevelOptions() {
        if (getExpressionTable(HUMAN) == null) {
            return null;
        }
        List<String> levels = new LinkedList<String>();
        for (NormalTissueCytomer t : getExpressionTable(HUMAN)) {
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

    public String getFastaForSelected() {
        if (getSelectedNode() == null) {
            return null;
        }
        return getFastaMap().get(((OboClass) getSelectedNode().getData()).getName());
    }

    public String getDBDFastaForSelected() {
        if (getSelectedNode() == null) {
            return null;
        }
        return getDBDFastaMap().get(((OboClass) getSelectedNode().getData()).getName());
    }

    public List<String> getProteinSVGsForSelected() {
        if (getSelectedNode() == null) {
            return null;
        }
        String id = ((OboClass) getSelectedNode().getData()).getName();
        List<String> out = new LinkedList<String>();
        if (getProteinPhyMLMap().containsKey(id)) {
            out.add(getProteinPhyMLMap().get(id));
        }
        if (getProteinWebprankMap().containsKey(id)) {
            out.add(getProteinWebprankMap().get(id));
        }
        if (getProteinPhyML2Map().containsKey(id)) {
            out.add(getProteinPhyML2Map().get(id));
        }
        return out;
    }

    public List<String> getProteinSlimSVGsForSelected() {
        if (getSelectedNode() == null) {
            return null;
        }
        String id = ((OboClass) getSelectedNode().getData()).getName();
        List<String> out = new LinkedList<String>();
        if (getProteinPhyMLslimMap().containsKey(id)) {
            out.add(getProteinPhyMLslimMap().get(id));
        }
        if (getProteinWebprankslimMap().containsKey(id)) {
            out.add(getProteinWebprankslimMap().get(id));
        }
        if (getProteinPhyML2slimMap().containsKey(id)) {
            out.add(getProteinPhyML2slimMap().get(id));
        }
        return out;
    }

    public List<String> getDBDSVGsForSelected() {
        if (getSelectedNode() == null) {
            return null;
        }
        String id = ((OboClass) getSelectedNode().getData()).getName();
        List<String> out = new LinkedList<String>();
        if (getDBDPhyMLMap().containsKey(id)) {
            out.add(getDBDPhyMLMap().get(id));
        }
        if (getDBDWebprankMap().containsKey(id)) {
            out.add(getDBDWebprankMap().get(id));
        }
        if (getDBDPhyML2Map().containsKey(id)) {
            out.add(getDBDPhyML2Map().get(id));
        }
        return out;
    }

    public List<String> getDBDSlimSVGsForSelected() {
        if (getSelectedNode() == null) {
            return null;
        }
        String id = ((OboClass) getSelectedNode().getData()).getName();
        List<String> out = new LinkedList<String>();
        if (getDBDSlimPhyMLMap().containsKey(id)) {
            out.add(getDBDSlimPhyMLMap().get(id));
        }
        if (getDBDSlimWebprankMap().containsKey(id)) {
            out.add(getDBDSlimWebprankMap().get(id));
        }
        if (getDBDSlimPhyML2Map().containsKey(id)) {
            out.add(getDBDSlimPhyML2Map().get(id));
        }
        return out;
    }

    private Map<String, String> getFastaMap() {
        if (fastaMap == null) {
            fastaMap = getFileMap("_mammalia.fasta");
        }
        return fastaMap;
    }

    private Map<String, String> getDBDFastaMap() {
        if (dbdFastaMap == null) {
            dbdFastaMap = getFileMap("mammalia_dbd.fasta");
        }
        return dbdFastaMap;
    }

    private Map<String, String> getProteinPhyMLMap() {
        if (proteinPhyMLMap == null) {
            proteinPhyMLMap = getFileMap("_mammalia_PhyML-iTOL.svg");
        }
        return proteinPhyMLMap;
    }

    private Map<String, String> getProteinWebprankMap() {
        if (proteinWebprankMap == null) {
            proteinWebprankMap = getFileMap("_mammalia_webprank-iTOL.svg");
        }
        return proteinWebprankMap;
    }

    private Map<String, String> getProteinPhyML2Map() {
        if (proteinPhyML2Map == null) {
            proteinPhyML2Map = getFileMap("_mammalia_PhyML2-iTOL.svg");
        }
        return proteinPhyML2Map;
    }

    private Map<String, String> getProteinPhyMLslimMap() {
        if (proteinSlimPhyMLMap == null) {
            proteinSlimPhyMLMap = getFileMap("_mammalia-slim_PhyML-iTOL.svg");
        }
        return proteinSlimPhyMLMap;
    }

    private Map<String, String> getProteinWebprankslimMap() {
        if (proteinSlimWebprankMap == null) {
            proteinSlimWebprankMap = getFileMap("_mammalia-slim_webprank-iTOL.svg");
        }
        return proteinSlimWebprankMap;
    }

    private Map<String, String> getProteinPhyML2slimMap() {
        if (proteinSlimPhyML2Map == null) {
            proteinSlimPhyML2Map = getFileMap("_mammalia-slim_PhyML2-iTOL.svg");
        }
        return proteinSlimPhyML2Map;
    }

    /// DBD
    private Map<String, String> getDBDPhyMLMap() {
        if (dbdPhyMLMap == null) {
            dbdPhyMLMap = getFileMap("_mammalia_dbd_PhyML-iTOL.svg");
        }
        return dbdPhyMLMap;
    }

    private Map<String, String> getDBDWebprankMap() {
        if (dbdWebprankMap == null) {
            dbdWebprankMap = getFileMap("_mammalia_dbd_webprank-iTOL.svg");
        }
        return dbdWebprankMap;
    }

    private Map<String, String> getDBDPhyML2Map() {
        if (dbdPhyML2Map == null) {
            dbdPhyML2Map = getFileMap("_mammalia_dbd_PhyML2-iTOL.svg");
        }
        return dbdPhyML2Map;
    }

    /// DBD slim
    private Map<String, String> getDBDSlimPhyMLMap() {
        if (dbdSlimPhyMLMap == null) {
            dbdSlimPhyMLMap = getFileMap("_mammalia-slim_dbd_PhyML-iTOL.svg");
        }
        return dbdSlimPhyMLMap;
    }

    private Map<String, String> getDBDSlimWebprankMap() {
        if (dbdSlimWebprankMap == null) {
            dbdSlimWebprankMap = getFileMap("_mammalia-slim_dbd_webprank-iTOL.svg");
        }
        return dbdSlimWebprankMap;
    }

    private Map<String, String> getDBDSlimPhyML2Map() {
        if (dbdSlimPhyML2Map == null) {
            dbdSlimPhyML2Map = getFileMap("_mammalia-slim_dbd_PhyML2-iTOL.svg");
        }
        return dbdSlimPhyML2Map;
    }

   
    @Deprecated
    private Map<String, String> getFileMap(String pattern) {
        Map<String, String> fileMap = new HashMap<String, String>();
        String dir = System.getenv("static_suppl_dir");
        if (dir == null) {
            dir = System.getProperty("static_suppl_dir");
        }
        if (dir == null) {
            return fileMap;
        }
        File dirf = new File(dir);
        File[] files = dirf.listFiles();
        for (File f : files) {
            String name = f.getName();
            if (name.endsWith(pattern)) {
                fileMap.put(name.substring(0, name.indexOf("_")), name);
            }
        }
        return fileMap;
    }

    /// species specific
    public String getSingleAnnotationForSpecies(String taxon, String annotation) {
        if (!selectedNode.getType().equals("Genus")) {
            return null;
        }
        List<OboClass> paralogs = getDownstreamOfSelected().get(taxon);
        if (paralogs == null) {
            return null;
        }
        for (OboClass prot : paralogs) {
            Set<JsonAnnotation> annot = prot.getAnnotationValues(annotation);
            if (annot == null || annot.size() < 1) {
                return null;
            }
            return annot.iterator().next().getValue();
        }
        return null;
    }

    public List<List<String>> getXref(String taxon) {
        if (!selectedNode.getType().equals("Genus")) {
            return null;
        }
        List<List<String>> links = new LinkedList<List<String>>();
        List<OboClass> paralogs = getDownstreamOfSelected().get(taxon);
        if (paralogs == null) {
            return null;
        }
        for (OboClass prot : paralogs) {
            Set<JsonAnnotation> xref = prot.getAnnotationValues("xref");
            for (JsonAnnotation annotation : xref) {
                String link = annotation.getValue();
                if (link.startsWith("ENSEMBL_GeneID:EN")) {
                    links.add(parseEnsembleGeneLink(link));
                } else if (link.startsWith("ENSEMBL:EN")) {
                    links.add(parseEnsembleLink(link));
                } else if (link.startsWith("TRANSFAC")) {
                    List<String> tflink = parseTransfacLink(link);
                    if (tflink != null) {
                        links.add(tflink);
                    }
                } else if (link.startsWith("UNIPROT")) {
                    links.add(parseUniprotLink(link));
                }
            }
        }
        return links;
    }

    public String getUniProt(String taxon) {
        if (!selectedNode.getType().equals("Genus")) {
            return null;
        }
        List<List<String>> links = new LinkedList<List<String>>();
        List<OboClass> paralogs = getDownstreamOfSelected().get(taxon);
        if (paralogs == null) {
            return null;
        }
        for (OboClass prot : paralogs) {
            Set<JsonAnnotation> xref = prot.getAnnotationValues("xref");
            for (JsonAnnotation annotation : xref) {
                String link = annotation.getValue();
                if (link.startsWith("UNIPROT")) {
                    link = link.replace("UNIPROT:", "");
                    if ("NA".equals(link)) {
                        return null;
                    }
                    return link;
                }
            }
        }
        return null;
    }

    public String getDBDSequence(String taxon) {
        InputStream fis = null;
        
        String species = speciesBean.getScientificName(taxon);
//        if (!HUMAN.equals(taxon)) {
//            return null;
//        }
        String out = "";
        String parentId = ((OboClass) selectedNode.getParent().getData()).getName();
        String nodeLabel = ((OboClass) selectedNode.getData()).getLabel();
        String fastaFileName = getDBDFastaMap().get(parentId);

        if (fastaFileName == null) {
            return null;
        }
        String dir = System.getenv("static_suppl_dir");
        if (dir == null) {
            dir = System.getProperty("static_suppl_dir");
        }
        String line;
        try {
            fis = new FileInputStream(dir + "/" + fastaFileName);
            InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                line = line.toLowerCase().replaceAll("-", "");
                if (line.startsWith(String.format(">%s_%s", species.toLowerCase().replaceAll(" ", "_"), nodeLabel.toLowerCase().replaceAll("-", "")))) {
                    out = "/" + br.readLine();//+"?color="+colorForSuperClass(((OboClass)selectedNode.getData()).getName());
                }
            }
        } catch (FileNotFoundException ex) {
            java.util.logging.Logger.getLogger(ClassificationBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(ClassificationBean.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(ClassificationBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return out;
    }

    private List<String> parseEnsembleGeneLink(String link) {
        LinkedList<String> out = new LinkedList<String>();
        String id = link.replace("ENSEMBL_GeneID:", "");
        out.add("Ensembl gene");
        out.add("http://www.ensembl.org/id/" + id);
        out.add(id);
        return out;
    }

    private List<String> parseEnsembleLink(String link) {
        LinkedList<String> out = new LinkedList<String>();
        String id = link.replace("ENSEMBL:", "");
        out.add("Ensembl");
        out.add("http://www.ensembl.org/id/" + id);
        out.add(id);
        return out;
    }

    private List<String> parseTransfacLink(String link) {
        LinkedList<String> out = new LinkedList<String>();
        String[] id = link.replace("TANSFAC:", "").split(",");
        if (id.length > 1) {
            out.add("TRANSFAC");
            out.add("http://factor.genexplain.com/cgi-bin/transfac_factor/getTF.cgi?AC=" + id[1]);
            out.add(id[1]);
        } else {
            return null;
        }
        return out;
    }

    private List<String> parseUniprotLink(String link) {
        LinkedList<String> out = new LinkedList<String>();
        String id = link.replace("UNIPROT:", "");
        out.add("UniProt");
        out.add("http://www.uniprot.org/uniprot/" + id);
        out.add(id);
        return out;
    }
    private String colorForSuperClass(String name) {
        char firstLetter = name.charAt(0);
        if (firstLetter == '0'){
            return "FF33CC";
        }
        if (firstLetter == '1'){
            return "00B0F0";
        }
        if (firstLetter == '2'){
            return "FFC000";
        }
        if (firstLetter == '3'){
            return "00B050";
        }
        if (firstLetter == '4'){
            return "FFFF00";
        }
        if (firstLetter == '5'){
            return "5B9BD5";
        }
        if (firstLetter == '6'){
            return "FF0000";
        }
        if (firstLetter == '7'){
            return "99FF33";
        }
        if (firstLetter == '8'){
            return "000000";
        }
        if (firstLetter == '9'){
            return "FFFFFF";
        }
        return "AAAAAA";
    }
    public void setNtf(NormaltissueFacadeREST ntf) {
        this.ntf = ntf;
    }

    public SpeciesBean getSpeciesBean() {
        return speciesBean;
    }

    public void setSpeciesBean(SpeciesBean speciesBean) {
        this.speciesBean = speciesBean;
    }


    
}
