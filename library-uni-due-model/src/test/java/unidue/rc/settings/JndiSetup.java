package unidue.rc.settings;

import javax.naming.InitialContext;
import javax.sql.DataSource;

public class JndiSetup { 
    /**
     * Setup the Data Source
     */
    public static void doSetup(String ds_name) {
        try {
            InitialContext ctxt = new InitialContext();
            
            
            DataSource ds = (DataSource) ctxt.lookup("jdbc."+ds_name);
            // rebind for alias if needed
            ctxt.rebind("jdbc/"+ds_name, ds);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
