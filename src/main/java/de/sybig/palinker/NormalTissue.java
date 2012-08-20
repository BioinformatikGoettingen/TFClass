/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.sybig.palinker;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author jdo
 */
@Entity
@Table(name = "NormalTissue")
@XmlRootElement
//@NamedQueries({
//    @NamedQuery(name = "Normaltissue.findAll", query = "SELECT n FROM Normaltissue n"),
//    @NamedQuery(name = "Normaltissue.findById", query = "SELECT n FROM Normaltissue n WHERE n.id = :id"),
//    @NamedQuery(name = "Normaltissue.findByEnsemble", query = "SELECT n FROM Normaltissue n WHERE n.ensemble = :ensemble"),
//    @NamedQuery(name = "Normaltissue.findByTissue", query = "SELECT n FROM Normaltissue n WHERE n.tissue = :tissue"),
//    @NamedQuery(name = "Normaltissue.findByCell", query = "SELECT n FROM Normaltissue n WHERE n.cell = :cell"),
//    @NamedQuery(name = "Normaltissue.findByLevel", query = "SELECT n FROM Normaltissue n WHERE n.level = :level"),
//    @NamedQuery(name = "Normaltissue.findByType", query = "SELECT n FROM Normaltissue n WHERE n.type = :type"),
//    @NamedQuery(name = "Normaltissue.findByReliability", query = "SELECT n FROM Normaltissue n WHERE n.reliability = :reliability")})
public class NormalTissue implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    private Integer id;
    @Size(max = 16)
    private String ensembl;
    @Size(max = 50)
    private String tissue;
    @Size(max = 50)
    private String cell;
    @Size(max = 20)
    private String level;
    @Size(max = 20)
    private String type;
    @Size(max = 20)
    private String reliability;

    public NormalTissue() {
    }

    public NormalTissue(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEnsembl() {
        return ensembl;
    }

    public void setEnsembl(String ensembl) {
        this.ensembl = ensembl;
    }

    public String getTissue() {
        return tissue;
    }

    public void setTissue(String tissue) {
        this.tissue = tissue;
    }

    public String getCell() {
        return cell;
    }

    public void setCell(String cell) {
        this.cell = cell;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReliability() {
        return reliability;
    }

    public void setReliability(String reliability) {
        this.reliability = reliability;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof NormalTissue)) {
            return false;
        }
        NormalTissue other = (NormalTissue) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "de.sybig.palinker.Normaltissue[ id=" + id + " ]";
    }

}
