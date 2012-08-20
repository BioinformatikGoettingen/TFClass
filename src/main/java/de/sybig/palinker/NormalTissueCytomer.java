/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.sybig.palinker;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author jdo
 */
@XmlRootElement
public class NormalTissueCytomer extends NormalTissue {

    private String tissueAcc;
    private String tissueClass;
    private String cellAcc;
    private String cellClass;

    public String getTissueAcc() {
        return tissueAcc;
    }

    public void setTissueAcc(String tissueAcc) {
        this.tissueAcc = tissueAcc;
    }

    public String getTissueClass() {
        return tissueClass;
    }

    public void setTissueClass(String tissueClass) {
        this.tissueClass = tissueClass;
    }

    public String getCellAcc() {
        return cellAcc;
    }

    public void setCellAcc(String cellAcc) {
        this.cellAcc = cellAcc;
    }

    public String getCellClass() {
        return cellClass;
    }

    public void setCellClass(String cellClass) {
        this.cellClass = cellClass;
    }
}
