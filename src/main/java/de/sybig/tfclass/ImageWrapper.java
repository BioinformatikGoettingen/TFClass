package de.sybig.tfclass;

/**
 *
 * @author juergen.doenitz@bioinf.med.uni-goettingen.de
 */
public class ImageWrapper {

    String id;
    String fileName;
    SpeciesSet speciesSet;
    String fileType;
    String type;
    String tool;
    String label;
    String legend;

    ImageWrapper(String imageType, String url) {
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
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
            return "Online PhyML and iTOL";
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
}
