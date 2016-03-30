package unidue.lib.model.testenv;

import java.util.Map;

import com.caucho.hessian.server.HessianServlet;




public class CayenneTestServlet extends HessianServlet implements CayenneTestService {

    /**
     * 
     */
    private static final long serialVersionUID = -5385517599329987067L;

    @Override
    public void setupdb(Map<String, String> dbproperties) {
        // TODO Auto-generated method stub
        
    }
}
