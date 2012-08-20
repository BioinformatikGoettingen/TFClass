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
@Table(name="CytomerTissue")
public class CytomerTissue extends CytomerBase implements  Serializable{
   private static final long serialVersionUID = 20120814L;

    private String paTissue;

    public String getPaTissue() {
        return paTissue;
    }

    public void setPaTissue(String paTissue) {
        this.paTissue = paTissue;
    }
}
