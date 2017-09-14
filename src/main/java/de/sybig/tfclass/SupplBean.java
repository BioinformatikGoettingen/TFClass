package de.sybig.tfclass;

import de.sybig.tfclass.ImageWrapper.RegionType;
import de.sybig.tfclass.ImageWrapper.SpeciesSet;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
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

    private List<ImageWrapper> imageWrappers = new ArrayList<ImageWrapper>();
    private Map<String, String> protLogoPlotMap;
    private Map<String, String> fastaMap;
    private Map<String, String> dbdFastaMap;

    public SupplBean() {
        readSupplDir();
    }

    public String getFasta(String id) {
        return getFastaMap().get(id);
    }

    public String getDBDFasta(String id) {
        return getDBDFastaMap().get(id);
    }

//    public List<ImageWrapper> getDBDSVGs(String id) {
//
//        List<ImageWrapper> out = new ArrayList<ImageWrapper>();
//        if (getDBDPhyMLMap().containsKey(id)) {
//
//            out.add(new ImageWrapper("dbd", getDBDPhyMLMap().get(id)));
//        }
//        if (getDBDWebprankMap().containsKey(id)) {
//            out.add(new ImageWrapper("dbd", getDBDWebprankMap().get(id)));
//        }
//        if (getDBDPhyML2Map().containsKey(id)) {
//            out.add(new ImageWrapper("dbd", getDBDPhyML2Map().get(id)));
//        }
//        return out; 
//    }
     public List<ImageWrapper> getDBDSVGs(String id) {
        List<ImageWrapper> images = filteriTol(id, SpeciesSet.MAMMALIA, RegionType.DBD);
        return images;
    }

    public List<ImageWrapper> getProteinSVGs(String id) {
        return filteriTol(id, SpeciesSet.MAMMALIA, RegionType.PROT);
    }

    public List<ImageWrapper> getDBDSlimSVGs(String id) {

         return filteriTol(id, SpeciesSet.MAMMALIA_SLIM, RegionType.DBD);
       
    }

    public String getProtLogoPlot(String id) {
        return getProtLogoPlotMap().get(id);
    }

    public List<ImageWrapper> getProteinSlimSVGs(String id) {
          return filteriTol(id, SpeciesSet.MAMMALIA_SLIM, RegionType.PROT);
    }

    public List<ImageWrapper> getModuleSVGs(String id) {
        List<ImageWrapper> images = filteriTol(id, SpeciesSet.MAMMALIA, RegionType.DBD_MODULE);
        return images;
    }

    public List<ImageWrapper> getModuleSlimSVGs(String id) {
        List<ImageWrapper> images = filteriTol(id, SpeciesSet.MAMMALIA_SLIM, RegionType.DBD_MODULE);
        return images;
    }

    public ImageWrapper getImage(String fileName) {
        for (ImageWrapper iw : imageWrappers) {
            if (iw.getFileName().equalsIgnoreCase(fileName)) {
                return iw;
            }
        }
        return null;
    }

    private List<ImageWrapper> filteriTol(String id, SpeciesSet speciesSet, RegionType type) {
        ArrayList<ImageWrapper> out = new ArrayList<ImageWrapper>();
        for (ImageWrapper iw : imageWrappers) {
            if (!speciesSet.equals(iw.getSpeciesSet())) {
                continue;
            }
            if (!id.equals(iw.getId())) {
                continue;
            }
            if (!type.equals(iw.getType())) {
                continue;
            }
            if ("logoplot".equalsIgnoreCase(iw.getTool())){
                continue;
            }
             if ("fasta".equalsIgnoreCase(iw.getTool())){
                continue;
            }
            out.add(iw);
        }
        return out;
    }

    private Map<String, String> getFastaMap() {
        if (fastaMap == null) {
            fastaMap = getFileMap("_mammalia_prot_fasta.fasta");
        }
        return fastaMap;
    }

    private Map<String, String> getDBDFastaMap() {
        if (dbdFastaMap == null) {
            dbdFastaMap = getFileMap("mammalia_dbd_fasta.fasta");
        }
        return dbdFastaMap;
    }

    private Map<String, String> getProtLogoPlotMap() {
        if (protLogoPlotMap == null) {
            protLogoPlotMap = getFileMap("_mammalia_dbd_logoplot.png");
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
        String dir = System.getenv("static_suppl_dir");
        if (dir == null) {
            dir = System.getProperty("static_suppl_dir");
        }

        File dirf = new File(dir);
        File[] files = dirf.listFiles();
        for (File f : files) {
            ImageWrapper wrapper = new ImageWrapper();
            String completeName = f.getName();
            String extension = completeName.substring(completeName.lastIndexOf(".") + 1);
            String name = completeName.substring(0, completeName.lastIndexOf("."));
            String[] parts = name.split("_");
            wrapper.setFileName(completeName);
            try {
                wrapper.setId(parts[0]);
                wrapper.setSpeciesSet(ImageWrapper.SpeciesSet.byName(parts[1]));
                wrapper.setType(ImageWrapper.RegionType.byName(parts[2]));
                wrapper.setTool(parts[3]);
                wrapper.setFileType(extension);
            } catch (ArrayIndexOutOfBoundsException ex) {
                System.out.println("can not split " + completeName);
            }
            imageWrappers.add(wrapper);

//zmv '(*)mammalia_slim_dbd_logoplot(*)' '$1mammalia-slim_dbd_logoplot$2'
//zmv  '(*)_mammalia.fasta' '$1_mammalia_prot.fasta'
//zmv  '(*).fasta' '$1_fasta.fasta' 
        }
    }

}
