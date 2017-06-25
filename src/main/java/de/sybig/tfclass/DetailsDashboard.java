package de.sybig.tfclass;

import de.sybig.oba.client.OboClass;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.DashboardReorderEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.DashboardColumn;
import org.primefaces.model.DashboardModel;
import org.primefaces.model.DefaultDashboardColumn;
import org.primefaces.model.DefaultDashboardModel;
import org.primefaces.model.TreeNode;

/**
 *
 * @author juergen.doenitz@bioinf.med.uni-goettingen.de
 */
@ManagedBean(name="dashboard")
@SessionScoped
public class DetailsDashboard implements Serializable {

    private DashboardModel model;
    @ManagedProperty(value="#{speciesTree}")
    SpeciesTree speciesTree;

    public void setSpeciesTree(SpeciesTree speciesTree) {
        this.speciesTree = speciesTree;
    }

    @PostConstruct
    public void init() {
        model = new DefaultDashboardModel();
        DashboardColumn column1 = new DefaultDashboardColumn();
        for (TreeNode child: speciesTree.getRoot().getChildren()){
            OboClass cls = (OboClass) child.getData();
            column1.addWidget("s"+ cls.getName());
        }
//        column1.addWidget("s9544");
//        column1.addWidget("s9606");
        model.addColumn(column1);
    }

    public void setSelectedSpecies(List<OboClass> selected){
        //System.out.println("previous enabled widgets " + model.getColumn(0).getWidgetCount());
        List<String> newNames = new ArrayList<String>();
        for (OboClass cls : selected){
            newNames.add("s"+cls.getName());
        }
        List<String> current = model.getColumn(0).getWidgets();
        for (String item : current){
            if (!newNames.contains(item)){
                model.getColumn(0).removeWidget(item);
            }
        }
        for (String item : newNames){
            if (!current.contains(item)){
                model.getColumn(0).addWidget(item);
            }
        }
       // System.out.println("post enabled widgets " + model.getColumn(0).getWidgetCount());
    }
    
    public void handleReorder(DashboardReorderEvent event) {
        FacesMessage message = new FacesMessage();
        message.setSeverity(FacesMessage.SEVERITY_INFO);
        message.setSummary("Reordered: " + event.getWidgetId());
        message.setDetail("Item index: " + event.getItemIndex() + ", Column index: " + event.getColumnIndex() + ", Sender index: " + event.getSenderColumnIndex());

        addMessage(message); 
    }

    public void handleClose(CloseEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Panel Closed", "Closed panel id:'" + event.getComponent().getId() + "'");

        addMessage(message);
    }

    public void handleToggle(ToggleEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, event.getComponent().getId() + " toggled", "Status:" + event.getVisibility().name());

        addMessage(message);
    }

    private void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public DashboardModel getModel() {
        return model;
    }
}
