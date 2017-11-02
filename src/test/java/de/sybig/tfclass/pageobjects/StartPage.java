package de.sybig.tfclass.pageobjects;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 *
 * @author juergen.doenitz@bioinf.med.uni-goettingen.de
 */
public class StartPage extends Base {

    By lastNodeLocator = By.xpath("//*[@id=\"classificationForm:cfTree:9\"]/span/span[3]");
    By mousePanelLocator = By.xpath("//*[@id=\"classificationForm:s10090_header\"]/span/../..");

    public StartPage(WebDriver driver) {
        super(driver);
        visit("/");
    }

    public Boolean lastClassNodePresent() {
        return isDisplayed(lastNodeLocator);
    }

    public Boolean uniProtSketchPresent() {
        visit("/?tfclass=1.1.1.1.2");
        WebElement mousePanel = find(mousePanelLocator);
        WebElement isoformsPanel = getIsoFormPanelInSpeciesPanel(mousePanel);
        WebElement image = isoformsPanel.findElement(By.tagName("img"));
        String source = image.getAttribute("src");

        if (!source.startsWith("http")) {
            source = driver.getCurrentUrl() + source;
        }
        try {
            BufferedImage img = ImageIO.read(new URL(source));
            return true;

        } catch (MalformedURLException ex) {
            Logger.getLogger(StartPage.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(StartPage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public Boolean urlsInImgeForSketchIdentical(){
          visit("/?tfclass=1.1.1.1.2");
        WebElement mousePanel = find(mousePanelLocator);
        WebElement isoformsPanel = getIsoFormPanelInSpeciesPanel(mousePanel);
        WebElement image = isoformsPanel.findElement(By.tagName("img"));
        return compareSourcesOfLinkWithImage(isoformsPanel.findElement(By.tagName("a")));
    }
    
    private Boolean compareSourcesOfLinkWithImage(WebElement link) {
        String hrefSource = link.getAttribute("href");
        WebElement image = link.findElement(By.tagName("img"));
        String source = image.getAttribute("src");
        return source.equals(hrefSource);
    }

    private WebElement getIsoFormPanelInSpeciesPanel(WebElement parent) {
        return parent.findElement(By.cssSelector("span[style*=\"float:right\"]"));
    }
}
