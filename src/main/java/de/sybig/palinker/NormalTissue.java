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

    public int getLevelAsInt() {
//        mysql> select level, count(level) as c from NormalTissue group by level order by c desc;
//+----------+--------+
//| level    | c      |
//+----------+--------+
//| Moderate | 194076 |  3
//| Negative | 189938 | -1
//| Weak     | 116155 |  2
//| Strong   |  95614 |  5
//| Medium   |  67031 |  4
//| None     |  57212 |  0
//| Low      |  41444 |  1
//| High     |  28549 |  6
//+----------+--------+


        if (level.equals("Moderate")) {
            return 3;
        }
        if (level.equals("Negative")) {
            return -1;
        }
        if (level.equals("Weak")) {
            return 2;
        }
        if (level.equals("Strong")) {
            return 5;
        }
        if (level.equals("Medium")) {
            return 4;
        }
        if (level.equals("None")) {
            return 0;
        }
        if (level.equals("Low")) {
            return 1;
        }
        if (level.equals("High")) {
            return 6;
        }

        return -2;
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

    public int getReliabilityAsInt() {
//        select reliability, count(reliability) as c from NormalTissue group by reliability order by c desc;
//+-------------+--------+
//| reliability | c      |
//+-------------+--------+
//| Uncertain   | 499257 | 2
//| Supportive  |  96526 | 6
//| High        |  66164 | 5
//| Medium      |  65313 | 4
//| Low         |  42641 | 3
//| Very low    |  20118 | 1
//+-------------+--------+
        if (reliability.equals("Uncertain")) {
            return 2;
        }
        if (reliability.equals("Supportive")) {
            return 6;
        }
        if (reliability.equals("High")) {
            return 5;
        }
        if (reliability.equals("Medium")) {
            return 4;
        }
        if (reliability.equals("Low")) {
            return 3;
        }
        if (reliability.equals("Very low")) {
            return 1;
        }
        return 0;
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
