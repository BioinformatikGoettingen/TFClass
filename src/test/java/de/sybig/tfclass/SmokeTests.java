package de.sybig.tfclass;

import de.sybig.tfclass.pageobjects.StartPage;
import org.junit.After;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

/**
 *
 * @author juergen.doenitz@bioinf.med.uni-goettingen.de
 */
public class SmokeTests {

    private WebDriver driver;
    private StartPage page;

    @Before
    public void setUp() {
//        System.out.println("os name " + System.getProperty("os.name")); //  Mac OS X
//        System.out.println("arch " + System.getProperty("os.arch")); // x86_64
        String system = System.getProperty("os.name").replaceAll(" ", "_") + "-" + System.getProperty("os.arch");
        System.out.println("system " + system);
        System.setProperty("webdriver.gecko.driver", "/data/deploy/selenium/"+system + "/geckodriver");
        System.setProperty("phantomjs.binary.path", "/data/deploy/selenium/"+system + "/phantomjs");
//        driver = new FirefoxDriver();
        driver = new PhantomJSDriver();
        page = new StartPage(driver);
    }

    @After
    public void shutDown() {
        driver.quit();
    }

    @Test
    public void testStartPage() {
        assertTrue("Class 0 on start page is not displayed", page.lastClassNodePresent());
    }

    @Test
    public void testDBDSketchForJunB() {
        assertTrue("Image for DBD sketch could not be loaded", page.uniProtSketchPresent());
    }

    @Test
    public void testURLsForSketchIdentical() {
        assertTrue("The URLs for the DBD sketch image and its link differ", page.urlsInImgeForSketchIdentical());
    }
}
