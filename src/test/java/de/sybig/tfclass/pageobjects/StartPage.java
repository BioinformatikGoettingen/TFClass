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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 *
 * @author juergen.doenitz@bioinf.med.uni-goettingen.de
 */
public class StartPage extends Base {

    By lastNodeLocator = By.xpath("//*[@id=\"classificationForm:cfTree:9\"]/span/span[3]");
    By mousePanelLocator = By.xpath("//*[@id=\"classificationForm:s10090_header\"]/span/../..");
    By humanPanelLocator = By.xpath("//*[@id=\"classificationForm:s9606_header\"]/span/../..");
    By humanSeedLinkLocator = By.xpath("//*[@id=\"classificationForm:seedLinkPanel\"]/a[1]");

    public StartPage(WebDriver driver) {
        super(driver);
        visit("/");
    }

    public StartPage(WebDriver driver, String queryparam) {
        super(driver);
        visit("/?" + queryparam);
    }

    public Boolean lastClassNodePresent() {
        return isDisplayed(lastNodeLocator);
    }

    public Boolean uniProtSketchPresent() {

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

    public Boolean urlsInImgeForSketchIdentical() {
        WebElement mousePanel = find(mousePanelLocator);
        WebElement isoformsPanel = getIsoFormPanelInSpeciesPanel(mousePanel);
        WebElement image = isoformsPanel.findElement(By.tagName("img"));
        return compareSourcesOfLinkWithImage(isoformsPanel.findElement(By.tagName("a")));
    }
 public boolean expressionTableIsFilled() {

        WebElement humanPanel = find(humanPanelLocator);
        WebElement linkPanel = getLinkPanelInSpeciesPanel(humanPanel);
        WebElement link = linkPanel.findElement(By.cssSelector("span > a"));
        By expressionTableDlgTitle = By.id("classificationForm:expressionTableDlg_title");

        WebElement dialogTitel = driver.findElement(expressionTableDlgTitle);
        if (dialogTitel.isDisplayed()) {
            return false;
        }
        link.click();
        try {
            WebDriverWait wait = new WebDriverWait(driver, 10);
            WebElement element = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(expressionTableDlgTitle));
        } catch (org.openqa.selenium.TimeoutException ex) {
            return false;
        }
        By firstCell = By.cssSelector("#classificationForm\\3a expressionTable_data > tr:nth-child(1) > td:nth-child(1)");
        return find(firstCell).isDisplayed();
    }
 
    public String getTitleOfLinkedSeedPage(){
        String link = find(humanSeedLinkLocator).getAttribute("href");
        visit(link);
        WebElement header = find(By.cssSelector("body > table > tbody > tr > td > h3"));
        return header.getText();
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

    private WebElement getLinkPanelInSpeciesPanel(WebElement parent) {
        return parent.findElement(By.cssSelector("span[style*=\"float:left\"]"));
    }

   
}
