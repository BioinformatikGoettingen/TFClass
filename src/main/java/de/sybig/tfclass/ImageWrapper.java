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
    ImageType type;
    String label;
    String legend;

    ImageWrapper(ImageType imageType, String url) {
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

    public ImageType getType() {
        return type;
    }

    public void setType(ImageType type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLegend() {
        return legend;
    }

    public void setLegend(String legend) {
        this.legend = legend;
    }

    public enum SpeciesSet{
        MAMMALIA, MAMMALIA_SLIM;
        
        public static SpeciesSet byName(String name){
            if (MAMMALIA.toString().equalsIgnoreCase(name)){
                return MAMMALIA;
            }
            if (MAMMALIA_SLIM.toString().equalsIgnoreCase(name.replace("-", "_"))){
                return MAMMALIA_SLIM;
            }
            return null;
        }
    }
}
