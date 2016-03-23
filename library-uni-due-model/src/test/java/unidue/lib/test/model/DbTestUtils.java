package unidue.lib.test.model;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.cayenne.BaseContext;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.access.DataNode;
import org.apache.cayenne.access.dbsync.SchemaUpdateStrategy;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.h2.jdbcx.JdbcDataSource;
import org.osjava.sj.memory.MemoryContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import unidue.lib.model.exception.DatabaseException;

/**
 * An instance of <code>DbTestUtils</code> can be used to create the data
 * sources used by cayenne. Be sure to call {@link DbTestUtils#setupdb()} to
 * initialize all databases and bind them to the root jndi context. After all
 * databases where setup, they must be closed by {@link DbTestUtils#shutdown()}.
 * Take a look at the cayennes configuration to get the jndi names of all used
 * databases.
 *
 * @author Nils Verheyen - Last modified by Paul Rochowski on 22.03.2016
 */
public class DbTestUtils {

    private static final Logger LOG = LoggerFactory.getLogger(DbTestUtils.class);
    /**
     * see http://stackoverflow.com/questions/4099095/what-does-javacomp-env-do
     * or http://www.prozesse-und-systeme.de/jndiResourcen.html
     **/
    private static final String ROOT_JNDI_CONTEXT_NAME = "java:comp/env";

    private ServerRuntime runtime;
    private Context jndiContext;
    private Set<String> jndiBindings;

    /**
     * Creates a new h2 in-memory database with target db name. username and
     * password are <code>sa</code>
     *
     * @param dbName
     * @return a new {@link JdbcDataSource}
     */
    private static JdbcDataSource createH2MemoryDB(String dbName) {
        JdbcDataSource dataSource = new JdbcDataSource();

        /*
         * set ;DB_CLOSE_DELAY=-1 otherwise the db is lost after the first
         * connection is closed
         */
        dataSource.setURL("jdbc:h2:mem:" + dbName + ";DB_CLOSE_DELAY=-1");
        dataSource.setUser("sa");
        dataSource.setPassword("sa");
        return dataSource;
    }

    /**
     * Initializes all databases used inside unit tests.
     *
     * @throws DatabaseException
     *             thrown if the db could not been setup
     */
    public void setupdb() throws DatabaseException {

        this.jndiBindings = new HashSet<>();

        createJndiContext();

        createDatabase("miless", "jdbc/miless");
        createDatabase("reserve_collections", "jdbc/reserve_collections");
        // createDatabase("rc_access_log", "jdbc/rc_access_log");
        initCayenne();

        // initDAOs();
        // try {
        // writeDefaultConfiguration();
        //
        // } catch (ConfigurationException e) {
        // e.printStackTrace();
        // }
    }

    /**
     * Loads cayennes configuration, creates database schema and binds a new
     * {@link ObjectContext} to the current thread.
     *
     * @throws DatabaseException
     *             thrown if a schema of one {@link DataNode} could not be
     *             initialized.
     */
    private void initCayenne() throws DatabaseException {
        // initialize cayenne configuration
        runtime = new ServerRuntime("cayenne-reserve-collections.xml");

        Collection<DataNode> nodes = runtime.getDataDomain().getDataNodes();
        for (DataNode node : nodes) {
            // in memory db was just created, therefore the schema has to be
            // created.
            SchemaUpdateStrategy updateStrategy = new CreateSchemaStrategy();
            try {
                updateStrategy.updateSchema(node);
                LOG.debug("node " + node.getName() + " updated");
            } catch (SQLException e) {
                throw new DatabaseException("could not update schema of node" + node.getName(), e);
            }
        }

        /*
         * bind ObjectContext to current thread, so test are able to use
         * BaseContext.getThreadObjectContext() to retrieve context.
         */
        ObjectContext dc = runtime.getContext();
        BaseContext.bindThreadObjectContext(dc);

        LOG.info("object context bound to current thread");
    }

    /**
     * Creates a jndi {@link Context} where databases can be bound to.
     *
     * @throws DatabaseException
     *             thrown if the jndi context could not be found or created.
     */
    private void createJndiContext() throws DatabaseException {

        /*
         * set factory to use when creating new jndi naming context to in memory
         * context factory
         */
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, MemoryContextFactory.class.getName());

        /*
         * will put the in-memory JNDI implementation into a mode whereby all
         * InitialContext's share the same memory. By default this is not set,
         * so two separate InitialContext's do not share the same memory and
         * what is bound to one will not be viewable in the other.
         */
        System.setProperty("org.osjava.sj.jndi.shared", "true");

        // create root node of this jndi naming context
        try {
            jndiContext = new InitialContext();
        } catch (NamingException e) {
            throw new DatabaseException("could not create jndi context", e);
        }

        try {
            jndiContext.createSubcontext(ROOT_JNDI_CONTEXT_NAME);
            LOG.info("jndi context " + ROOT_JNDI_CONTEXT_NAME + " created");

        } catch (NamingException e) {
            throw new DatabaseException("could not create context " + ROOT_JNDI_CONTEXT_NAME, e);
        }
    }

    /**
     * Creates a database according to {@link #createH2MemoryDB(String)} with
     * target database name and binds it to target jndi name, if it not already
     * present.
     *
     * @param databaseName
     *            name of the database which should be created
     * @param jndiName
     *            jndi resource name with which the database can be addressed.
     */
    private void createDatabase(String databaseName, String jndiName) {

        // create in memory databases
        JdbcDataSource dataSource = createH2MemoryDB(databaseName);

        try {
            /*
             * bind created databases to jndi. inside
             * "cayenne-unidue-reserve-collections.xml" is defined which jndi
             * names are used by cayenne
             */
            jndiContext.bind(jndiName, dataSource);
            jndiBindings.add(jndiName);
            LOG.info("database \"" + databaseName + "\" bound to jndi name \"" + jndiName + "\"");
        } catch (NamingException e) {
            LOG.warn("could not bind database \"" + databaseName + "\" to jndi name \"" + jndiName + "\". rebinding ."
                    + "..");
            try {
                jndiContext.rebind(jndiName, dataSource);
            } catch (NamingException rebindException) {
                LOG.warn("could not rebind database \"" + databaseName + "\" to jndi name \"" + jndiName + "\". "
                        + rebindException.getMessage());
            }
        }
    }

    /**
     * Closes all resources initialized by {@link DbTestUtils#setupdb()}.
     */
    public void shutdown() {
        runtime.shutdown();
        try {
            for (String jndiBinding : jndiBindings) {

                LOG.info("unbinding " + jndiBinding);
                jndiContext.unbind(jndiBinding);
            }

            jndiContext.destroySubcontext(ROOT_JNDI_CONTEXT_NAME);
            jndiContext.close();
            LOG.info("jndi sub context " + ROOT_JNDI_CONTEXT_NAME + " successfully destroyed");
        } catch (NamingException e) {
            LOG.error("could not destroy jndi context " + ROOT_JNDI_CONTEXT_NAME);
        }
    }
}