package unidue.lib.model.dao;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import unidue.lib.model.DbTestUtils;
import unidue.lib.model.dto.LibraryLocation;
import unidue.lib.model.exception.CommitException;
import unidue.lib.model.exception.DatabaseException;
import unidue.lib.model.exception.DeleteException;

public class TestLibraryLocationDAO extends Assert {

    private static final Logger LOG = LoggerFactory.getLogger(TestLibraryLocationDAO.class);

    private LibraryLocationDAO locationDAO = new LibraryLocationDAOImpl();

    private DbTestUtils dbTestUtils;
    private int parentLocationId;
    private int childLocationId;

    @BeforeClass
    public void setup() throws DatabaseException {
        LOG.info("running " + this.getClass().getName() + " tests");
        try {
            locationDAO = new LibraryLocationDAOImpl();
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
        LOG.info("Test of " + this.getClass().getName() + " done...");
    }

    @Test
    public void testCreate() {

        LibraryLocation location = new LibraryLocation();
        location.setName("parentLocation");
        try {
            locationDAO.create(location);
        } catch (CommitException e) {
            e.printStackTrace(); // To change body of catch statement use File |
                                 // Settings | File Templates.
        }

        assertNotNull(location.getObjectId());
        this.parentLocationId = Cayenne.intPKForObject(location);

        LibraryLocation childLocation = new LibraryLocation();
        childLocation.setName("childLocation");
        childLocation.setParentLocation(location);
        try {
            locationDAO.create(childLocation);
        } catch (CommitException e) {
            e.printStackTrace(); // To change body of catch statement use File |
                                 // Settings | File Templates.
        }

        assertNotNull(childLocation.getObjectId());
        this.childLocationId = Cayenne.intPKForObject(childLocation);
    }

    @Test(dependsOnMethods = {"testCreate"})
    public void testSelect() {

        LibraryLocation parentLocation = locationDAO.getLocationById(parentLocationId);
        assertNotNull(parentLocation);

        assertEquals(parentLocation.getChildLocations().size(), 1);

        LibraryLocation noLocation = locationDAO.getLocationById(-1);
        assertNull(noLocation);
    }

    @Test(dependsOnMethods = {"testCreate", "testUpdate", "testSelect"})
    public void testDelete() {
        LibraryLocation location = locationDAO.getLocationById(childLocationId);
        try {
            locationDAO.delete(location);

            location = locationDAO.getLocationById(childLocationId);
            assertNull(location);
        } catch (DeleteException e) {
            e.printStackTrace();
        }

    }

    @Test(dependsOnMethods = {"testCreate"})
    public void testUpdate() {
        String newName = "newName";
        LibraryLocation location = locationDAO.getLocationById(parentLocationId);
        location.setName(newName);
        try {
            locationDAO.update(location);
        } catch (CommitException e) {
            e.printStackTrace(); // To change body of catch statement use File |
                                 // Settings | File Templates.
        }

        LibraryLocation updatedLocation = locationDAO.getLocationById(parentLocationId);
        assertEquals(updatedLocation.getName(), newName);
    }
}
