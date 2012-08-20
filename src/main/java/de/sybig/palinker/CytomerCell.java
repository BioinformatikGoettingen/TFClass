/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.sybig.palinker;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author jdo
 */
@Entity
@XmlRootElement
@Table(name="CytomerCell")
public class CytomerCell extends CytomerBase implements  Serializable{

  private String paCell;

    public String getPaCell() {
        return paCell;
    }

    public void setPaCell(String paCell) {
        this.paCell = paCell;
    }
  

}
