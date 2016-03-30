package unidue.lib.model.testenv;


import java.util.Map;

import unidue.lib.model.exception.DatabaseException;


public interface CayenneTestService {

    public void setupdb(Map<String, String> dbproperties) throws DatabaseException;
}
