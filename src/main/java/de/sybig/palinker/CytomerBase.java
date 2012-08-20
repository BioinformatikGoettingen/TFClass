/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.sybig.palinker;

import javax.persistence.Basic;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

/**
 *
 * @author jdo
 */
@MappedSuperclass
public abstract class CytomerBase {

    @Id
    @Basic(optional = false)
    @NotNull
    private Integer id;
    private String cyName;
    private String cyAcc;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCyName() {
        return cyName;
    }

    public void setCyName(String cyName) {
        this.cyName = cyName;
    }

    public String getCyAcc() {
        return cyAcc;
    }

    public void setCyAcc(String cyAcc) {
        this.cyAcc = cyAcc;
    }

}
