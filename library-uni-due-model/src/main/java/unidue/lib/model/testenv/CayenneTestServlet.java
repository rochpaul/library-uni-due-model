package unidue.lib.model.testenv;

import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

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

    @Override
    public void setupdb(Map<String, String> dbproperties) throws DatabaseException {

        createJndiContext();
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
}
