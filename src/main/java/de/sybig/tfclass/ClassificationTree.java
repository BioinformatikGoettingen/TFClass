package de.sybig.tfclass;

import com.sun.jersey.api.client.UniformInterfaceException;
import de.sybig.oba.client.Obo2DClassList;
import de.sybig.oba.client.OboClass;
import de.sybig.oba.client.OboConnector;
import de.sybig.oba.client.OntologyClass;
import de.sybig.oba.server.JsonAnnotation;
import de.sybig.oba.server.JsonCls;
import de.sybig.oba.server.JsonClsList;
import java.net.ConnectException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author juergen.doenitz@bioinf.med.uni-goettingen.de
 */
public class ClassificationTree {

    private final OboConnector connector;
    private final Logger log = LoggerFactory.getLogger(ClassificationTree.class);
    private CfNode root;
    private LinkedList<String> fieldList;

    public ClassificationTree(OboConnector connector) {
        super();
        this.connector = connector;
    }

    public TreeNode getRoot() {
        if (root == null) {
            try {
                OboClass rootCls = getConnector().getRoot();
                Set<OboClass> firstChildren = rootCls.getChildren();
                root = new ClassificationTree.CfNode(rootCls, null);
                for (OboClass child : firstChildren) {
                    if (child.getName().equals("TF_class")) {
                        root = new ClassificationTree.CfNode(child, null);
                    }
                }
            } catch (ConnectException ex) {
                log.error("could not connect the OBA server to get the root node");
            }
        }

        return root;
    }

    public TreeNode expandNode(OboClass searchedClass) {
        if (searchedClass == null) {
            log.warn("can not expand tree, searched class is null");
            return null;
        }
        Obo2DClassList paths = null;
        try {
            OboClass r = getConnector().getRoot();
            paths = getConnector().xDownstreamOfY(searchedClass, root.getOc());
        } catch (UniformInterfaceException ex) {
            log.warn("An error occured while getting the path to root for {}, {}", searchedClass, ex);
            return null;
        } catch (ConnectException ex) {
            log.error("could not connect to the OBA server");
        }
        if (paths == null || paths.getEntities() == null || paths.getEntities().size() < 1) {
            log.warn("no path to expand for {}", searchedClass);
            return null;
        }
        JsonClsList<JsonCls> path = paths.get(0);
        //System.out.println("path the expand "+ path);
        TreeNode lastExpanded = getRoot();       
        for (int i = path.getEntities().size() - 2; i > 0; i--) {
            JsonCls cls = path.getEntities().get(i);
            lastExpanded = expandChild(lastExpanded, cls);
            if (lastExpanded == null) {
                log.warn("could not expand path");
                return null;
            }
        }
//        System.out.println("6");
//        collapseTree(searchedClass);
//        System.out.println("7");
        return searchChildForObo(lastExpanded, path.get(0));

    }

    private TreeNode expandChild(TreeNode lastExpanded, JsonCls cls) {
        TreeNode child = searchChildForObo(lastExpanded, cls);
        if (child == null) {
            return null;
        }
        child.setExpanded(true);
        return child;
    }

    private TreeNode searchChildForObo(TreeNode parent, JsonCls cls) {
        for (TreeNode child : parent.getChildren()) {
            if (((OboClass) child.getData()).getName().equals(cls.getName())) {
                return child;
            }
        }
        return null;
    }

        private void collapseTree(OboClass oboClass) {
            System.out.println("___1 " + System.currentTimeMillis());
        TreeNode treeNode = searchTreeNode(getRoot(), oboClass);
        System.out.println("___2 " + System.currentTimeMillis());
        treeNode.setExpanded(false);
    }
    
    void collapseTree() {
        for (TreeNode child : getRoot().getChildren()) {
            collapseTree(child);
            child.setSelected(false);
        }
    }

    public void collapseTree(TreeNode n) {
        n.setSelected(false);
        n.setExpanded(false);
        for (TreeNode child : n.getChildren()) {
            if (child.isExpanded()) {
                collapseTree(child);
            }
        }
    }
  private TreeNode searchTreeNode(TreeNode parent, OboClass pattern) {
        for (TreeNode child : parent.getChildren()) {
            if (((OboClass) child.getData()).getName().equals(pattern.getName())) {
                return child;
            }
            TreeNode tn = searchTreeNode(child, pattern);
            if (tn != null) {
                return tn;
            }
        }
        return null;
    }
    private OboConnector getConnector() {
        return connector;
    }

    public class CfNode extends DefaultTreeNode {

        final Lock lock = new ReentrantLock();
        private final OboClass oc;
        private volatile LinkedList<TreeNode> childnodes = null;

        private CfNode(OboClass oc, TreeNode parent) {
            super(oc, parent);
            this.oc = oc;
        }

        public CfNode(String type, OboClass oc, TreeNode parent) {
            super(type, oc, parent);
            this.oc = oc;
        }

        @Override
        public synchronized List<TreeNode> getChildren() {

            if (childnodes == null) {
                if (childnodes == null) {
                    childnodes = new LinkedList<TreeNode>();

                    Set<OboClass> cs = (Set<OboClass>) oc
                            .getChildren();

                    if (cs != null) {
                        List<OboClass> oboChildren = new LinkedList<OboClass>(cs);
                        Collections.sort(oboChildren, new ClassificationTree.NodeComparator());
                        for (OboClass child : oboChildren) {
                            String type = "";
                            for (JsonAnnotation a : (Set<JsonAnnotation>) child.getAnnotations()) {
                                if (a.getName().equals("level")) {
                                    type = a.getValue();
                                }
                            }
                            new ClassificationTree.CfNode(type, child, this);
                            // the node is added to children by the super class
                            // do not add it here
                        }
                    } // if cs
                }
//		}

            }
            return childnodes;
        }

        @Override
        public int getChildCount() {
            if (getChildren() == null) {
                return 0;
            }
            return getChildren().size();
        }

        @Override
        public boolean isLeaf() {
            if (oc.isShell()) {
                oc.getChildren();
            }
            if (getChildren() != null && getChildCount() > 0) {
                return false;
            }
            return true;
        }

        public OntologyClass getOc() {
            return oc;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 17 * hash + (this.oc != null ? this.oc.hashCode() : 0);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final CfNode other = (CfNode) obj;
            if (this.oc != other.oc && (this.oc == null || !((OboClass) this.oc).getName().equals(((OboClass) other.oc).getName()))) {
                return false;
            }
            return true;
        }

    }

    public class NodeComparator implements Comparator<OboClass> {

        @Override
        public int compare(OboClass t, OboClass t1) {
            if (isStringID(t.getName()) && isStringID(t1.getName())) {
                String[] tnames = t.getName().split("\\.");
                String[] t1names = t1.getName().split("\\.");
                for (int i = 0; i < tnames.length; i++) {
                    if (t1names.length <= i) {
                        return -1;
                    }
                    int a = Integer.parseInt(tnames[i]);
                    int b = Integer.parseInt(t1names[i]);
                    if (a == 0) {
                        if (b == 0) {
                            continue;
                        }
                        return 1;
                    }
                    if (b == 0) {
                        return -1;
                    }
                    if (a == b) {
                        continue;
                    }
                    if (a > b) {
                        return 1;
                    }
                    return -1;
                }
                return 0;
            } else {
                return t.getName().compareTo(t1.getName());
            }
        }
    }

    private boolean isStringID(String s) {
        return s.matches("[0-9\\.]*");
    }

}
