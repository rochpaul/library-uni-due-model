package unidue.lib.model.testenv;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.cayenne.BaseContext;
import org.apache.cayenne.DataChannel;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.access.DataDomain;
import org.apache.cayenne.access.DataNode;
import org.apache.cayenne.access.dbsync.SchemaUpdateStrategy;
import org.apache.cayenne.configuration.CayenneRuntime;
import org.apache.cayenne.configuration.server.ServerModule;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.configuration.web.WebUtil;
import org.apache.cayenne.di.DIBootstrap;
import org.apache.cayenne.di.Injector;
import org.apache.cayenne.di.Module;
import org.h2.jdbcx.JdbcDataSource;
import org.osjava.sj.memory.MemoryContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caucho.hessian.server.HessianServlet;

import unidue.lib.model.exception.DatabaseException;

public class CayenneTestServlet extends HessianServlet implements CayenneTestService {

    /**
     * 
     */
    private static final long serialVersionUID = -5385517599329987067L;

    private static final String ROOT_JNDI_CONTEXT_NAME = "java:comp/env";

    private static final Logger LOG = LoggerFactory.getLogger(CayenneTestServlet.class);

    private Context jndiContext;
    
    private Set<String> jndiBindings;

    private ServerRuntime runtime;
    
    @Override
    public void setupdb(Map<String, String> dbproperties) throws DatabaseException {

        this.jndiBindings = new HashSet<>();
        
        createJndiContext();
        
        
        dbproperties.forEach((databaseName, jndiName) 
                -> createDatabase(databaseName, jndiName));
        
        initCayenne();
        
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
//        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, MemoryContextFactory.class.getName());

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

            /*
             * see
             * http://stackoverflow.com/questions/4099095/what-does-javacomp-env
             * -do or http://www.prozesse-und-systeme.de/jndiResourcen.html
             */
            jndiContext.lookup(ROOT_JNDI_CONTEXT_NAME);

            LOG.info("jndi context " + ROOT_JNDI_CONTEXT_NAME + " found");
        } catch (NamingException e) {
            try {
                jndiContext.createSubcontext(ROOT_JNDI_CONTEXT_NAME);
                LOG.info("jndi context " + ROOT_JNDI_CONTEXT_NAME + " created");
            } catch (NamingException createException) {
                throw new DatabaseException("could not create context " + ROOT_JNDI_CONTEXT_NAME, e);
            }
        }
    }
    
    /**
     * Creates a database according to {@link #createH2MemoryDB(String)} with target database name and binds it to
     * target jndi name, if it not already present.
     *
     * @param databaseName name of the database which should be created
     * @param jndiName     jndi resource name with which the database can be addressed.
     */
    private void createDatabase(String databaseName, String jndiName) {

        // create in memory databases
        JdbcDataSource dataSource = createH2MemoryDB(databaseName);

        try {
            /*
             * bind created databases to jndi. inside
             * "cayenne-unidue-reserve-collections.xml" is defined which jndi names
             * are used by cayenne
             */
            jndiContext.bind(jndiName, dataSource);
            jndiBindings.add(jndiName);
            LOG.info("database \"" + databaseName + "\" bound to jndi name \"" + jndiName + "\"");
        } catch (NamingException e) {
            LOG.warn("could not bind database \"" + databaseName + "\" to jndi name \"" + jndiName + "\". rebinding ." + "..");
            try {
                jndiContext.rebind(jndiName, dataSource);
            } catch (NamingException rebindException) {
                LOG.warn("could not rebind database \"" + databaseName + "\" to jndi name \"" + jndiName + "\". " +
                        rebindException.getMessage());
            }
        }
    }
    
    /**
     * Creates a new h2 in-memory database with target db name. username and password are <code>sa</code>
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
     * Loads cayennes configuration, creates database schema and binds a new {@link ObjectContext} to the current
     * thread.
     *
     * @throws DatabaseException thrown if a schema of one {@link DataNode} could not be initialized.
     */
    private void initCayenne() throws DatabaseException {
        
//        CayenneRuntime runtime = WebUtil.getCayenneRuntime(servletContext);
        
        CayenneRuntime runtime = WebUtil.getCayenneRuntime(getServletContext());
        
//        FileConfiguration conf = new FileConfiguration("my-cayenne.xml");
        
        
//        runtime.getContext(parentChannel)
        
//        Module[] modules = runtime.getModules();
//        
//        ArrayList<Module> listModules = new ArrayList<>();
//        
//        listModules.add(modules[0]);
//        listModules.add(modules[1]);
//        listModules.add(new ServerModule("rc_test"));
//        
//        Module[] modulesFromList = new Module[listModules.size()];
//        modulesFromList = listModules.toArray(modulesFromList);
        
        
//        Arrays.stream(modules)
//        .filter(module -> module instanceof ServerModule);
        
//        ServerModule testModule = new ServerModule("rc_test");
        
//        CayenneRuntime.bindThreadInjector(DIBootstrap.createInjector(modulesFromList));
//        
//        
//        DataChannel channel = runtime.getChannel();
        
        

//        DataDomain domain = new DataDomain("rc_test");
        
//        Configuration configuration;
        
        ObjectContext objectContext = runtime.getContext();
        
        
        
//        Injector inj = ServerRuntime.getThreadInjector();
//        
//        ObjectContext objectContext = BaseContext.getThreadObjectContext();
        
        // initialize cayenne configuration
//        runtime = new ServerRuntime("rc_test.xml");
//
//        Collection<DataNode> nodes = runtime.getDataDomain().getDataNodes();
//                
//        for (DataNode node : nodes) {
//            // in memory db was just created, therefore the schema has to be created.
//            SchemaUpdateStrategy updateStrategy = new CreateSchemaStrategy();
//            try {
//                updateStrategy.updateSchema(node);
//                LOG.debug("node " + node.getName() + " updated");
//            } catch (SQLException e) {
//                throw new DatabaseException("could not update schema of node" + node.getName(), e);
//            }
//        }
        
        
        

        /*
         * bind ObjectContext to current thread, so test are able to use BaseContext.getThreadObjectContext()
         * to retrieve context.
         */
        
//        Object object = runtime.getContext();
//        
//        ObjectContext dc = runtime.getContext();
//        ObjectContext o = BaseContext.getThreadObjectContext();
//
//        LOG.info("object context bound to current thread");
    }
    
}
