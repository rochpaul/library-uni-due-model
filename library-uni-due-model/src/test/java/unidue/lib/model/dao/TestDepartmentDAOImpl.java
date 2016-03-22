package unidue.lib.model.dao;

import static org.testng.AssertJUnit.assertTrue;
import org.apache.cayenne.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import unidue.lib.model.DatabaseException;
import unidue.lib.model.DbTestUtils;

public class TestDepartmentDAOImpl {

    private static final Logger LOG = LoggerFactory.getLogger(TestDepartmentDAOImpl.class);

    private DbTestUtils dbTestUtils;

    @BeforeClass
    public void setup() throws Exception {

        try {

            dbTestUtils = new DbTestUtils();
            dbTestUtils.setupdb();

        } catch (DatabaseException | ValidationException e) {
            LOG.error("could not setup " + this.getClass().getSimpleName() + " tests: " + e.getMessage());
            throw e;
        }
    }

    @AfterClass
    public void shutdown() {

        dbTestUtils.shutdown();
    }

    @Test
    public void testDepartmentDAOImpl() {

        assertTrue(true);
    }
}
