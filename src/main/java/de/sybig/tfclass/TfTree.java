package de.sybig.tfclass;

import com.sun.org.apache.bcel.internal.generic.LASTORE;
import de.sybig.oba.client.GenericConnector;
import de.sybig.oba.client.Obo2DClassList;
import de.sybig.oba.client.OboClass;
import de.sybig.oba.client.OboClassList;
import de.sybig.oba.client.OboConnector;
import de.sybig.oba.client.OntologyClass;
import de.sybig.oba.server.JsonCls;
import de.sybig.oba.server.JsonClsList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.ejb.Singleton;
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
        if (root == null){
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
        if (searchedClass == null){
            return null;
        }
        Obo2DClassList paths = getConnector().xDownstreamOfY(searchedClass, getConnector().getRoot());
        if (paths == null || paths.getEntities() == null || paths.getEntities().size() < 1){
            log.warn("no path to expand for {}" , searchedClass);
            return null;
        }
        JsonClsList<JsonCls> path = paths.get(0);
        TreeNode lastExpanded = getRoot();
        for (int i = path.getEntities().size()-2; i > 0 ; i--){
            JsonCls cls = path.getEntities().get(i);
            lastExpanded = expandChild(lastExpanded, cls);
            if (lastExpanded == null){
                log.error("could not expand path");
                return null;
            }
        }
        return searchChildForObo(lastExpanded, path.get(0));

    }

    private TreeNode expandChild(TreeNode lastExpanded, JsonCls cls) {
        TreeNode child = searchChildForObo(lastExpanded, cls);
        if (child == null){
            return null;
        }
        child.setExpanded(true);
        return child;
    }
    private TreeNode searchChildForObo(TreeNode parent, JsonCls cls){
        for (TreeNode child: parent.getChildren()){
            if (((OboClass)child.getData()).getName().contains(cls.getName())){
                return child;
            }
        }
        return null;
    }

    void expandToSubSet(String subset) {
        LinkedList<String> fields = new LinkedList<String>();
        fields.add("subset");
        OboClassList result = getConnector().searchCls(subset, fields);
        if (result == null || result.getEntities() == null){
            return;
        }
        for (OboClass cls : result.getEntities()){
            expandNode(cls);
        }
    }
    public class TfTreeNode extends DefaultTreeNode {

        final Lock lock = new ReentrantLock();
        private final OntologyClass oc;
        private volatile LinkedList<TreeNode> childnodes = null;

        public TfTreeNode(OntologyClass oc, TreeNode parent) {
            super(oc, parent);
            this.oc = oc;
        }

        @Override
        public synchronized List<TreeNode> getChildren() {
            //
            if (childnodes == null) {
//		synchronized (this) {
                if (childnodes == null) {
                    childnodes = new LinkedList<TreeNode>();
                    Set<OntologyClass> cs = (Set<OntologyClass>) oc
                            .getChildren();
                    if (cs != null) {
                        for (OntologyClass child : cs) {
                            new TfTreeNode(child, this);
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
}
