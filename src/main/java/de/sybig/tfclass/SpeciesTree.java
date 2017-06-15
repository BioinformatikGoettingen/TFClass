package de.sybig.tfclass;

import de.sybig.oba.client.OboClass;
import de.sybig.oba.client.OboConnector;
import de.sybig.oba.client.OntologyClass;
import java.net.ConnectException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author juergen.doenitz@bioinf.med.uni-goettingen.de
 */
@ManagedBean
@SessionScoped
public class SpeciesTree {

    private OboConnector connector;

    private final Logger log = LoggerFactory.getLogger(SpeciesTree.class);
    private SpeciesTree.SfNode root;

    public SpeciesTree() {

    }

    private OboConnector getConnector() {
        if (connector == null) {
            connector = ObaProvider.getInstance().getConnector3();
        }
        return connector;
    }

    public TreeNode getRoot() {
        if (root == null) {
            try {
                OboClass rootCls = getConnector().getRoot();
                Set<OboClass> firstChildren = rootCls.getChildren();
                root = new SpeciesTree.SfNode(rootCls, null);
                for (OboClass child : firstChildren) {
                    if (child.getName().equals("species")) {
                        root = new SpeciesTree.SfNode(child, null);
                    }
                }
            } catch (ConnectException ex) {
                log.error("could not connect the OBA server to get the root node");
            }
        }

        return root;
    }

    private static class NodeComparator implements Comparator{


        @Override
        public int compare(Object o1, Object o2) {
            if (!(o1 instanceof OboClass && o2 instanceof OboClass)){
                return 0;
            }
            String name1 = ((OboClass)o1).getLabel();
            String name2 = ((OboClass)o2).getLabel();
            if (name1 == null){
                return 1;
            }
            if (name2 == null){
                return -1;
            }
            return name1.compareTo(name2);
        }
    }

    public class SfNode extends DefaultTreeNode {

        final Lock lock = new ReentrantLock();
        private final OboClass oc;
        private volatile LinkedList<TreeNode> childnodes = null;

        private SfNode(OboClass oc, TreeNode parent) {
            super(oc, parent);
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
                        Collections.sort(oboChildren, new SpeciesTree.NodeComparator());
                        for (OboClass child : oboChildren) {
//                                }
//                            }
                            new SpeciesTree.SfNode(child, this);
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
            final SfNode other = (SfNode) obj;
            if (this.oc != other.oc && (this.oc == null || !((OboClass) this.oc).getName().equals(((OboClass) other.oc).getName()))) {
                return false;
            }
            return true;
        }

    }
}
