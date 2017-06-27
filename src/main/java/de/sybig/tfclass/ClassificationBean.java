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

    private ClassificationTree classificationTree;
    private TreeNode selectedNode;
    private Map<String, List<OboClass>> downstreamOfSelected;
    private LinkedList<String> fieldList;
    private OboClass searchedClass;
    private Map<String, String> fastaMap;
    private Map<String, String> dbdFastaMap;
    private Map<String, String> svgMap;
    private Map<String, String> dbdSvgMap;

    public ClassificationBean() {
        super();
        this.connector = ObaProvider.getInstance().getConnector3();
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

    public String getSVGForSelected() {
        if (getSelectedNode() == null) {
            return null;
        }
        return getSVGMap().get(((OboClass) getSelectedNode().getData()).getName());
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

    private Map<String, String> getSVGMap() {
        if (svgMap == null) {
            svgMap = getFileMap("_mammalia_PhyML-iTOL.svg");
        }
        return svgMap;
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
                    links.add(parseTransfacLink(link));
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
        out.add("TRANSFAC");
        out.add("https://portal.biobase-international.com/cgi-bin/knowledgebase/pageview.cgi?view=LocusReport&protein_acc=" + id[0]);
        if (id.length > 1) {
            out.add(id[1]);
        } else {
            out.add(id[0]);
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
