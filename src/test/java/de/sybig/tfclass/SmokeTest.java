package de.sybig.tfclass;

import de.sybig.tfclass.pageobjects.StartPage;
import org.junit.After;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

/**
 *
 * @author juergen.doenitz@bioinf.med.uni-goettingen.de
 */
public class SmokeTest {

    private WebDriver driver;

    @Before
    public void setUp() {
        String system = System.getProperty("os.name").replaceAll(" ", "_") + "-" + System.getProperty("os.arch");
        // will be Linux-amd64	or Mac_OS_X-x86_64
       
        System.setProperty("webdriver.gecko.driver", "/data/deploy/selenium/" + system + "/geckodriver");
        System.setProperty("phantomjs.binary.path", "/data/deploy/selenium/" + system + "/phantomjs");
//        driver = new FirefoxDriver();
        driver = new PhantomJSDriver();

    }

    @After
    public void shutDown() {
        driver.quit();
    }

    @Test
    public void testStartPage() {
        StartPage page = new StartPage(driver);
        assertTrue("Class 0 on start page is not displayed", page.lastClassNodePresent());
    }

    @Test
//    @Ignore
    public void testDBDSketchForJunB() {
        StartPage page = new StartPage(driver,  "tfclass=1.1.1.1.2");
        assertTrue("Image for DBD sketch could not be loaded", page.uniProtSketchPresent());
    }

    @Test
    public void testURLsForSketchIdentical() {
        StartPage page = new StartPage(driver, "tfclass=1.1.1.1.2");
        assertTrue("The URLs for the DBD sketch image and its link differ", page.urlsInImgeForSketchIdentical());
    }
    
    @Test
    public void testProteinExpressionTable(){
        StartPage page = new StartPage(driver,"tfclass=5.1.1.1.3");
        assertTrue("Expression table is not displayed after click or before click.",page.expressionTableIsFilled());
    }
    
}
