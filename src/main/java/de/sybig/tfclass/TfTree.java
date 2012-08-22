package de.sybig.tfclass;

import com.sun.org.apache.xml.internal.dtm.ref.DTMDefaultBaseIterators;
import de.sybig.oba.client.Obo2DClassList;
import de.sybig.oba.client.OboClass;
import de.sybig.oba.client.OboClassList;
import de.sybig.oba.client.OboConnector;
import de.sybig.oba.client.OntologyClass;
import de.sybig.oba.server.JsonCls;
import de.sybig.oba.server.JsonClsList;
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
 * @author juergen.deontiz@bioinf.med.uni-goettingen.de
 */
//@Singleton
public class TfTree {

    private static final Logger log = LoggerFactory.getLogger(TfTree.class);
    private OboConnector connector;
    private TfTreeNode root;

    public TreeNode getRoot() {
        if (root == null) {
            OboClass rootCls = getConnector().getRoot();
            root = new TfTreeNode(rootCls, null);
        }
        return root;

    }

    private OboConnector getConnector() {
        if (connector == null) {
            connector = ObaProvider.getInstance().getConnector();
        }
        return connector;
    }

    public TreeNode expandNode(OboClass searchedClass) {
        if (searchedClass == null) {
            return null;
        }
        Obo2DClassList paths = getConnector().xDownstreamOfY(searchedClass, getConnector().getRoot());
        if (paths == null || paths.getEntities() == null || paths.getEntities().size() < 1) {
            log.warn("no path to expand for {}", searchedClass);
            return null;
        }
        JsonClsList<JsonCls> path = paths.get(0);
        TreeNode lastExpanded = getRoot();
        for (int i = path.getEntities().size() - 2; i > 0; i--) {
            JsonCls cls = path.getEntities().get(i);
            lastExpanded = expandChild(lastExpanded, cls);
            if (lastExpanded == null) {
                log.error("could not expand path");
                return null;
            }
        }
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
            if (((OboClass) child.getData()).getName().contains(cls.getName())) {
                return child;
            }
        }
        return null;
    }

    void expandTree() {
        expandTree(getRoot());
    }

    void collapseTree() {
//        collapseTree(getRoot());
        for (TreeNode child :  getRoot().getChildren() ){
            collapseTree(child);
        }
    }

    public void expandTree(TreeNode n) {
        n.setExpanded(true);
        for (TreeNode child : n.getChildren()) {
            expandTree(child);
        }
    }

    public void collapseTree(TreeNode n) {
        if (!n.isExpanded()) {
            return;
        }
        n.setExpanded(false);
        for (TreeNode child : n.getChildren()) {
            collapseTree(child);
        }
    }

    void expandToSubSet(String subset) {
        System.out.println("expanding to subset " + subset);
        LinkedList<String> fields = new LinkedList<String>();
        fields.add("subset");
        OboClassList result = getConnector().searchCls(subset, fields);
        if (result == null || result.getEntities() == null) {
            return;
        }
        for (OboClass cls : result.getEntities()) {
            expandNode(cls);
        }
    }

    public class TfTreeNode extends DefaultTreeNode {

        final Lock lock = new ReentrantLock();
        private final OboClass oc;
        private volatile LinkedList<TreeNode> childnodes = null;

        public TfTreeNode(OboClass oc, TreeNode parent) {
            super(oc, parent);
            this.oc = oc;
        }

        public TfTreeNode(String type, OboClass oc, TreeNode parent) {
            super(type, oc, parent);
            this.oc = oc;
        }

        @Override
        public synchronized List<TreeNode> getChildren() {
            //
            if (childnodes == null) {
//		synchronized (this) {
                if (childnodes == null) {
                    childnodes = new LinkedList<TreeNode>();


                    Set<OboClass> cs = (Set<OboClass>) oc
                            .getChildren();
                    if (cs != null) {
                        List<OboClass> oboChildren = new LinkedList<OboClass>(cs);
                        Collections.sort(oboChildren, new NodeComparator());
                        for (OboClass child : oboChildren) {
                            new TfTreeNode(child.getSubsets(), child, this);
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
    }

    public class NodeComparator implements Comparator<OboClass> {

        @Override
        public int compare(OboClass t, OboClass t1) {
            if (t.getName().equals("0")) {
                return 1;
            }
            return t.getName().compareTo(t1.getName());
        }
    }
}
