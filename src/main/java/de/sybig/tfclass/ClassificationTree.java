package de.sybig.tfclass;

import de.sybig.oba.client.OboClass;
import de.sybig.oba.client.OboConnector;
import de.sybig.oba.client.OntologyClass;
import de.sybig.oba.server.JsonAnnotation;
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
                for (OboClass child : firstChildren){
                    if (child.getName().equals("TF_class")){
                        root = new ClassificationTree.CfNode(child, null);
                    }
                }
            } catch (ConnectException ex) {
                log.error("could not connect the OBA server to get the root node");
            }
        }
       
        return root;
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
             System.out.println("type " + type);
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
                        Collections.sort(oboChildren, new ClassificationTree.NodeComparator());
                        for (OboClass child : oboChildren) {
                            String type = "";
                            for (JsonAnnotation a : (Set<JsonAnnotation>)child.getAnnotations()){
                                System.out.println("a " + a.getName());
                                if (a.getName().equals("level")){
                                   type = a.getValue();
                                }
                            }
                            new ClassificationTree.CfNode(type, child, this);
//                             new TfTreeNode( child, this);
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
//            System.out.println(t.getName() + " compare to " + t1.getName());
            if (stringStartWithNumber(t.getName()) && stringStartWithNumber(t1.getName())) {
                String[] tnames = t.getName().split("\\.");
                String[] t1names = t1.getName().split("\\.");
                for (int i = 0; i < tnames.length; i++) {
                    if (t1names.length < i) {
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

    private boolean stringStartWithNumber(String s) {
        try {
            Integer.parseInt(s.substring(0, 1));
        } catch (NumberFormatException ex) {
            return false;
        }
        return true;
    }

}
