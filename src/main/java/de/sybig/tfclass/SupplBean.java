package de.sybig.tfclass;

import java.io.File;
import java.util.HashMap;
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

    public String getProtLogoPlot(String id) {
        return getProtLogoPlotMap().get(id);
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
}
