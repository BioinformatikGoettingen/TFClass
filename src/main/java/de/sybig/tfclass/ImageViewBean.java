package de.sybig.tfclass;

import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author juergen.doenitz@bioinf.med.uni-goettingen.de
 */
@ManagedBean
@ViewScoped
public class ImageViewBean {

    @ManagedProperty(value = "#{supplBean}")
    private SupplBean supplBean;

    private ImageType type;
    private String id;
    private String fileName;
    private ImageWrapper image;
    
    public void init(){
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        setType(params.get("type"));
        setId(params.get("class"));
        setFileName(params.get("file"));
    }
    public ImageWrapper getImageWrapper() {
        if (image == null) {
            switch(type){
                case DBD:
                    System.out.println("id " + id);
                    image = supplBean.getDBDSVGs(id).get(0);
                    break;
            }
        }
        return image;
    }
    
   
       

    public ImageType getType() {
        return type;
    }

    public void setType(ImageType type) {
        this.type = type;
    }

    public void setType(String type) {
        this.type = ImageType.DBD;
    }

    public String getId() {
        if (fileName == null){
            return null;
        }
        return fileName.split("_")[0];
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

    public SupplBean getSupplBean() {
        return supplBean;
    }

    public void setSupplBean(SupplBean supplBean) {
        this.supplBean = supplBean;
    }

}
