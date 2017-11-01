package de.sybig.tfclass;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 *
 * @author juergen.doenitz@bioinf.med.uni-goettingen.de
 */
public class SmokeTests {
    
    private WebDriver driver;
    
    @Before
    public void setUp() {
        System.setProperty("webdriver.gecko.driver", "lib/geckodriver");
        driver = new FirefoxDriver();
    }
    
    @Test
    public void testStartPage() {
        driver.get("http://tfclass.bioinf.med.uni-goettingen.de");
    }
    
}
