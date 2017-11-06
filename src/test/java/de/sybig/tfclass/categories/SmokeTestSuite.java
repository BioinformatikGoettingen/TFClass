package de.sybig.tfclass.categories;

import de.sybig.tfclass.SmokeTest;
import org.junit.experimental.categories.Categories;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 *
 * @author juergen.doenitz@bioinf.med.uni-goettingen.de
 */

@RunWith(Categories.class)
@Categories.IncludeCategory(SmokeTestGroup.class)
//Include multiple categories
//@Categories.IncludeCategory({PerformanceTests.class, RegressionTests.class})
@Suite.SuiteClasses({SmokeTest.class})
public class SmokeTestSuite {
    
}
