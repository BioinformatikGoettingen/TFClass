package de.sybig.tfclass;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author juergen.doenitz@bioinf.med.uni-goettingen.de
 */
public class ImageWrapper {

    private static final Logger logger = LoggerFactory.getLogger(ImageWrapper.class);
    String id;
    String fileName;
    SpeciesSet speciesSet;
    String fileType;
    RegionType type;
    String tool;
    String label;
    String legend;

    ImageWrapper(RegionType imageType, String url) {
        super();
        type = imageType;
        this.fileName = url;
    }

    ImageWrapper() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public SpeciesSet getSpeciesSet() {
        return speciesSet;
    }

    public void setSpeciesSet(SpeciesSet speciesSet) {
        this.speciesSet = speciesSet;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public RegionType getType() {
        return type;
    }

    public void setType(RegionType type) {
        this.type = type;
    }

    public String getTool() {
        return tool;
    }

    public void setTool(String tool) {
        this.tool = tool;
    }

    public String getLabel() {
        if (label == null) {
            return String.format("%s", getToolName(tool));
        }
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    private static String getToolName(String tool) {
        if ("webprank-itol".equalsIgnoreCase(tool)) {
            return "webPRANK and iTOL";
        }
        if ("phyml-itol".equalsIgnoreCase(tool)) {
            return "Phylogeny.fr and iTOL";
        }
        if ("phyml2-itol".equalsIgnoreCase(tool)) {
            return "PhyML and iTOL";
        }
        return "";
    }

    public String getLegend() {
        return legend;
    }

    public void setLegend(String legend) {
        this.legend = legend;
    }

    public enum SpeciesSet {
        MAMMALIA, MAMMALIA_SLIM;

        public static SpeciesSet byName(String name) {
            if (MAMMALIA.toString().equalsIgnoreCase(name)) {
                return MAMMALIA;
            }
            if (MAMMALIA_SLIM.toString().equalsIgnoreCase(name.replace("-", "_"))) {
                return MAMMALIA_SLIM;
            }
            return null;
        }
    }

    public enum RegionType {
        PROT, DBD, DBD_MODULE;
        

        public static RegionType byName(String name) {
            if ("prot".equalsIgnoreCase(name)) {
                return PROT;
            }
            if ("dbd".equalsIgnoreCase(name)) {
                return DBD;
            }
            if ("dbd-module".equalsIgnoreCase(name)) {
                return DBD_MODULE;
            }
            logger.error("No region type matched for {}" , name);
            return null;
        }

        @Override
        public String toString() {
            switch (this) {
                case PROT:
                    return "whole protein";
                case DBD:
                    return "DNA binding domain";
                case DBD_MODULE:
                    return "modules of the DNA binding domain";
            }
            return "no hit";
        }
    }
}
