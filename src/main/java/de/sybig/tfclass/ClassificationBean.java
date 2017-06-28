package de.sybig.tfclass;

import de.sybig.oba.client.OboClass;
import de.sybig.oba.client.OboClassList;
import de.sybig.oba.client.OboConnector;
import de.sybig.oba.server.JsonAnnotation;
import de.sybig.oba.server.JsonObjectPropertyExpression;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.io.File;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
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

    private ClassificationTree classificationTree;
    private TreeNode selectedNode;
    private Map<String, List<OboClass>> downstreamOfSelected;
    private LinkedList<String> fieldList;
    private OboClass searchedClass;
    private Map<String, String> fastaMap;
    private Map<String, String> dbdFastaMap;
    private Map<String, String> proteinPhyMLMap;
    private Map<String, String> dbdSvgMap;
    private Map<String, String> proteinWebprankMap;
    private Map<String, String> proteinPhyML2Map;

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
        if (selectedNode == null) {
            return false;
        }
        return (getDownstreamOfSelected().keySet().contains(taxon));
    }

    public List<OboClass> search(String pattern) {
        String searchPattern = pattern;
        try {
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
            System.out.println(last + " for " + getSearchedClass());
            return null;
        }
        last.setSelected(true);
        setSelectedNode(last);
        return last;
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
            System.out.println("No T ID for " + link);
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
}
