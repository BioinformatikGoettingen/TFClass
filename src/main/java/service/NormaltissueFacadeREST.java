/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import de.sybig.palinker.CytomerCell;
import de.sybig.palinker.CytomerTissue;
import de.sybig.palinker.NormalTissue;
import de.sybig.palinker.NormalTissueCytomer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 *
 * @author jdo
 */
@Stateless
@Path("normaltissue")
public class NormaltissueFacadeREST extends AbstractFacade<NormalTissue> {

    @PersistenceContext(unitName = "de.sybig_paLinker_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    public NormaltissueFacadeREST() {
        super(NormalTissue.class);
    }

    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    public NormalTissue find(@PathParam("id") Integer id) {
        return super.find(id);
    }

//    @GET
//    @Override
//    @Produces({"application/xml", "application/json"})
//    public List<Normaltissue> findAll() {
//        return super.findAll();
//    }
    @GET
    @Path("{from}/{to}")
    @Produces({"application/xml", "application/json"})
    public List<NormalTissue> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[]{from, to});
    }

    @GET
    @Path("count")
    @Produces("text/plain")
    public String countREST() {
        return String.valueOf(super.count());
    }

    @GET
    @Path("ensembl/{ensid}")
    @Produces({"application/xml", "application/json"})
    public List<NormalTissueCytomer> getWithEnsemblId(@PathParam("ensid") String ensemblId) {
        TypedQuery<NormalTissue> query = em.createQuery("SELECT nt FROM NormalTissue nt WHERE nt.ensembl = :eid", NormalTissue.class);
        query.setParameter("eid", ensemblId);

        return createCytomerTissues(query.getResultList());
    }

    @GET
    @Path("ensembl/{ensid}")
    @Produces("text/html")
    public String getHtmlWithEnsemblId(@PathParam("ensid") String ensemblId) {
        List<NormalTissueCytomer> tissues = getWithEnsemblId(ensemblId);
        Collections.sort(tissues, new TissueComparator());
        StringBuilder out = new StringBuilder();
        out.append("<html><head></head><body>");
        out.append("<table><thead><td>Tissue</td><td>Cell type</td><td>Expression level</td><td>Type</td><td>Reliability</td></thead>");
        for (NormalTissueCytomer tissue : tissues) {
            out.append(String.format("<tr><td><a href=\"\">%s</a></td><td><a href=\"\">%s</td><td>%s</td><td>%s</td><td>%s</td></tr>",
                    tissue.getTissueClass().replaceAll("_", " "), tissue.getCellClass().replaceAll("_", " "),
                    tissue.getLevel(), tissue.getType(), tissue.getReliability()));
        }
        out.append("</table>");
        out.append("</body></html>");
        return out.toString();
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    private List<NormalTissueCytomer> createCytomerTissues(final List<NormalTissue> origList) {
        List<NormalTissueCytomer> out = new ArrayList<NormalTissueCytomer>(origList.size());
        for (NormalTissue nt : origList) {
            out.add(createCytomerTissue(nt));
        }
        return out;
    }

    private NormalTissueCytomer createCytomerTissue(final NormalTissue orig) {
        NormalTissueCytomer ct = new NormalTissueCytomer();
        ct.setCell(orig.getCell());
        ct.setEnsembl(orig.getEnsembl());
        ct.setId(orig.getId());
        ct.setLevel(orig.getLevel());
        ct.setReliability(orig.getReliability());
        ct.setTissue(orig.getTissue());
        ct.setType(orig.getType());
        fillCytomerCell(orig, ct);
        fillCytomerTissue(orig, ct);

        return ct;
    }

    private void fillCytomerCell(NormalTissue orig, NormalTissueCytomer ct) {

        TypedQuery<CytomerCell> query = em.createQuery("SELECT cc FROM CytomerCell cc WHERE cc.paCell = :paCell", CytomerCell.class);
        query.setParameter("paCell", ct.getCell());
        try {
            CytomerCell cc = query.getSingleResult();
            ct.setCellClass(cc.getCyName());
            ct.setCellAcc(cc.getCyAcc());
        } catch (NoResultException e1) {
            // ok
        } catch (NonUniqueResultException e2) {
            System.err.append("got more than one result from CytomerCell for cell " + ct.getCell());
        }
    }

    private void fillCytomerTissue(NormalTissue orig, NormalTissueCytomer ct) {

        TypedQuery<CytomerTissue> query = em.createQuery("SELECT ct FROM CytomerTissue ct WHERE ct.paTissue = :paTissue", CytomerTissue.class);
        query.setParameter("paTissue", ct.getTissue());
        try {
            CytomerTissue cc = query.getSingleResult();
            ct.setTissueClass(cc.getCyName());
            ct.setTissueAcc(cc.getCyAcc());
        } catch (NoResultException e1) {
            // ok
        } catch (NonUniqueResultException e2) {
            System.err.append("got more than one result from CytomerCell for cell " + ct.getCell());
        }
    }

    class TissueComparator implements Comparator<NormalTissueCytomer> {

        @Override
        public int compare(NormalTissueCytomer t, NormalTissueCytomer t1) {
            int comp = t.getTissueClass().compareTo(t1.getTissueClass());
            if (comp != 0){
                return comp;
            }
            return (t.getCellClass().compareTo(t1.getCellClass()));
        }
    }
}
