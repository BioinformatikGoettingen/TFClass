package de.sybig.tfclass.pageobjects;

import static de.sybig.tfclass.Configuration.baseURL;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 *
 * @author juergen.doenitz@bioinf.med.uni-goettingen.de
 */
public class Base {

    protected final WebDriver driver;

    public Base(WebDriver driver) {
        this.driver = driver;
    }

    public void visit(String url) {
        if (url.contains("http")) {
            driver.get(url);
        } else {
            driver.get(baseURL + url);
        }
    }

    public WebElement find(By locator) {
        return driver.findElement(locator);
    }

    public Boolean isDisplayed(By locator) {
        try {
            return find(locator).isDisplayed();
        } catch (org.openqa.selenium.NoSuchElementException exception) {
            return false;
        }
    }
}
