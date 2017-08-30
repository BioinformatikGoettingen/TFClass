package de.sybig.tfclass;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author juergen.doenitz@bioinf.med.uni-goettingen.de
 */
@ManagedBean
@ApplicationScoped
public class SupplBean {

    private Map<String, String> protLogoPlotMap;
    private Map<String, String> fastaMap;
    private Map<String, String> dbdFastaMap;
    private Map<String, String> dbdPhyMLMap;
    private Map<String, String> dbdWebprankMap;
    private Map<String, String> dbdPhyML2Map;
    private Map<String, String> proteinPhyMLMap;
    private Map<String, String> proteinWebprankMap;
    private Map<String, String> proteinPhyML2Map;
    private Map<String, String> dbdSlimPhyML2Map;
    private Map<String, String> dbdSlimWebprankMap;
    private Map<String, String> dbdSlimPhyMLMap;
    private Map<String, String> proteinSlimPhyML2Map;
    private Map<String, String> proteinSlimWebprankMap;
    private Map<String, String> proteinSlimPhyMLMap;

    
    public SupplBean(){
        readSupplDir();
    }
    public String getFasta(String id) {

        return getFastaMap().get(id);
    }

    public String getDBDFasta(String id) {

        return getDBDFastaMap().get(id);
    }

    public List<ImageWrapper> getDBDSVGs(String id) {
        
        List<ImageWrapper> out = new ArrayList<ImageWrapper>();
        if (getDBDPhyMLMap().containsKey(id)) {

            out.add(new ImageWrapper(ImageType.DBD, getDBDPhyMLMap().get(id)));
        }
        if (getDBDWebprankMap().containsKey(id)) {
            out.add(new ImageWrapper(ImageType.DBD, getDBDWebprankMap().get(id)));
        }
        if (getDBDPhyML2Map().containsKey(id)) {
            out.add(new ImageWrapper(ImageType.DBD, getDBDPhyML2Map().get(id)));
        }
        return out;
    }

    public List<String> getProteinSVGs(String id) {

        List<String> out = new LinkedList<String>();
        if (getProteinPhyMLMap().containsKey(id)) {
            out.add(getProteinPhyMLMap().get(id));
        }
        if (getProteinWebprankMap().containsKey(id)) {
            out.add(getProteinWebprankMap().get(id));
        }
        if (getProteinPhyML2Map().containsKey(id)) {
            out.add(getProteinPhyML2Map().get(id));
        }
        return out;
    }

    public List<String> getDBDSlimSVGs(String id) {

        List<String> out = new LinkedList<String>();
        if (getDBDSlimPhyMLMap().containsKey(id)) {
            out.add(getDBDSlimPhyMLMap().get(id));
        }
        if (getDBDSlimWebprankMap().containsKey(id)) {
            out.add(getDBDSlimWebprankMap().get(id));
        }
        if (getDBDSlimPhyML2Map().containsKey(id)) {
            out.add(getDBDSlimPhyML2Map().get(id));
        }
        return out;
    }

    public String getProtLogoPlot(String id) {
        return getProtLogoPlotMap().get(id);
    }

    public List<String> getProteinSlimSVGs(String id) {

        List<String> out = new LinkedList<String>();
        if (getProteinPhyMLslimMap().containsKey(id)) {
            out.add(getProteinPhyMLslimMap().get(id));
        }
        if (getProteinWebprankslimMap().containsKey(id)) {
            out.add(getProteinWebprankslimMap().get(id));
        }
        if (getProteinPhyML2slimMap().containsKey(id)) {
            out.add(getProteinPhyML2slimMap().get(id));
        }
        return out;
    }

    private Map<String, String> getFastaMap() {
        if (fastaMap == null) {
            fastaMap = getFileMap("_mammalia.fasta");
        }
        return fastaMap;
    }

    private Map<String, String> getDBDFastaMap() {
        if (dbdFastaMap == null) {
            dbdFastaMap = getFileMap("mammalia_dbd.fasta");
        }
        return dbdFastaMap;
    }
    /// DBD

    private Map<String, String> getDBDPhyMLMap() {
        if (dbdPhyMLMap == null) {
            dbdPhyMLMap = getFileMap("_mammalia_dbd_PhyML-iTOL.svg");
        }
        return dbdPhyMLMap;
    }

    private Map<String, String> getDBDWebprankMap() {
        if (dbdWebprankMap == null) {
            dbdWebprankMap = getFileMap("_mammalia_dbd_webprank-iTOL.svg");
        }
        return dbdWebprankMap;
    }

    private Map<String, String> getDBDPhyML2Map() {
        if (dbdPhyML2Map == null) {
            dbdPhyML2Map = getFileMap("_mammalia_dbd_PhyML2-iTOL.svg");
        }
        return dbdPhyML2Map;
    }

    private Map<String, String> getProteinPhyMLMap() {
        if (proteinPhyMLMap == null) {
            proteinPhyMLMap = getFileMap("_mammalia_PhyML-iTOL.svg");
        }
        return proteinPhyMLMap;
    }

    private Map<String, String> getProteinWebprankMap() {
        if (proteinWebprankMap == null) {
            proteinWebprankMap = getFileMap("_mammalia_webprank-iTOL.svg");
        }
        return proteinWebprankMap;
    }

    private Map<String, String> getProteinPhyML2Map() {
        if (proteinPhyML2Map == null) {
            proteinPhyML2Map = getFileMap("_mammalia_PhyML2-iTOL.svg");
        }
        return proteinPhyML2Map;
    }

    /// DBD slim
    private Map<String, String> getDBDSlimPhyMLMap() {
        if (dbdSlimPhyMLMap == null) {
            dbdSlimPhyMLMap = getFileMap("_mammalia-slim_dbd_PhyML-iTOL.svg");
        }
        return dbdSlimPhyMLMap;
    }

    private Map<String, String> getDBDSlimWebprankMap() {
        if (dbdSlimWebprankMap == null) {
            dbdSlimWebprankMap = getFileMap("_mammalia-slim_dbd_webprank-iTOL.svg");
        }
        return dbdSlimWebprankMap;
    }

    private Map<String, String> getDBDSlimPhyML2Map() {
        if (dbdSlimPhyML2Map == null) {
            dbdSlimPhyML2Map = getFileMap("_mammalia-slim_dbd_PhyML2-iTOL.svg");
        }
        return dbdSlimPhyML2Map;
    }

    private Map<String, String> getProteinPhyMLslimMap() {
        if (proteinSlimPhyMLMap == null) {
            proteinSlimPhyMLMap = getFileMap("_mammalia-slim_PhyML-iTOL.svg");
        }
        return proteinSlimPhyMLMap;
    }

    private Map<String, String> getProteinWebprankslimMap() {
        if (proteinSlimWebprankMap == null) {
            proteinSlimWebprankMap = getFileMap("_mammalia-slim_webprank-iTOL.svg");
        }
        return proteinSlimWebprankMap;
    }

    private Map<String, String> getProteinPhyML2slimMap() {
        if (proteinSlimPhyML2Map == null) {
            proteinSlimPhyML2Map = getFileMap("_mammalia-slim_PhyML2-iTOL.svg");
        }
        return proteinSlimPhyML2Map;
    }

    private Map<String, String> getProtLogoPlotMap() {
        if (protLogoPlotMap == null) {
            protLogoPlotMap = getFileMap("_mammalia.logoplot.png");
        }
        return protLogoPlotMap;
    }

    private Map<String, String> getFileMap(String pattern) {
        Map<String, String> fileMap = new HashMap<String, String>();
        String dir = System.getenv("static_suppl_dir");
        if (dir == null) {
            dir = System.getProperty("static_suppl_dir");
        }
        if (dir == null) {
            return fileMap;
        }
        File dirf = new File(dir);
        File[] files = dirf.listFiles();
        for (File f : files) {
            String name = f.getName();
            if (name.endsWith(pattern)) {
                fileMap.put(name.substring(0, name.indexOf("_")), name);
            }
        }
        return fileMap;
    }

    private void readSupplDir() {
        if (true){
            return;
        }
        String dir = System.getenv("static_suppl_dir");
        if (dir == null) {
            dir = System.getProperty("static_suppl_dir");
        }

        File dirf = new File(dir);
        File[] files = dirf.listFiles();
        for (File f : files) {
            ImageWrapper wrapper = new ImageWrapper();
            String completeName = f.getName();
            completeName = completeName.replace(".logoplot", "_logoplot");
            String extension = completeName.substring(completeName.lastIndexOf(".")+1) ;
            String name = completeName.substring(0,completeName.lastIndexOf("."));
            String[] parts = name.split("_");
            
            wrapper.setId(parts[0]);
            wrapper.setSpeciesSet(ImageWrapper.SpeciesSet.byName(parts[1]));
           
            wrapper.setFileType(extension);
            if (parts.length < 3){
                continue;
            }
            if ("dbd".equals(parts[3])){
               // wrapper.setPortion(parts[3]);
            }
            System.out.println("3 " + parts[2]);
            if (parts.length < 3){
                System.out.println("--- " + completeName);
                continue;
            }
            //System.out.println(wrapper.getSpeciesSet());
           
            
           
            //System.out.println("2x. " + parts[1]);
//            if (name.endsWith(pattern)) {
//                fileMap.put(name.substring(0, name.indexOf("_")), name);
//            }
        }
    }
}
