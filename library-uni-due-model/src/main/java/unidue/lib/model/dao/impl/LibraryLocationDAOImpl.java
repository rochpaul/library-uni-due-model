package unidue.lib.model.dao.impl;

/*
 * #%L
 * Semesterapparate
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2014 Universitaet Duisburg Essen
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.apache.cayenne.BaseContext;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.DeleteDenyException;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import unidue.lib.model.dao.LibraryLocationDAO;
import unidue.lib.model.dto.LibraryLocation;
import unidue.lib.model.exception.CommitException;
import unidue.lib.model.exception.DeleteException;

import java.util.Collections;
import java.util.List;

/**
 * Default implementation of {@link LibraryLocationDAO}.
 *
 * @author Nils Verheyen
 */
public class LibraryLocationDAOImpl implements LibraryLocationDAO {

    private static final Logger LOG = LoggerFactory.getLogger(LibraryLocationDAOImpl.class);

//    @Inject
//    private UserDAO userDAO;

    @Override
    public List<LibraryLocation> getRootLocations() {

        ObjectContext objectContext = BaseContext.getThreadObjectContext();

        // select root locations
        SelectQuery query = new SelectQuery(LibraryLocation.class);
        query.setQualifier(ExpressionFactory.matchExp(LibraryLocation.PARENT_LOCATION_PROPERTY, null));
        query.addPrefetch(LibraryLocation.CHILD_LOCATIONS_PROPERTY);

        @SuppressWarnings("unchecked")
        List<LibraryLocation> rootLocations = objectContext.performQuery(query);

        return rootLocations != null ? rootLocations : Collections.<LibraryLocation>emptyList();
    }

    @Override
    public LibraryLocation getLocationById(Integer id) {

        ObjectContext context = BaseContext.getThreadObjectContext();

        SelectQuery query = new SelectQuery(LibraryLocation.class);

        query.setQualifier(ExpressionFactory.matchExp(LibraryLocation.ID_PROPERTY, id));

        return (LibraryLocation) Cayenne.objectForQuery(context, query);
    }

    @Override
    public List<LibraryLocation> getLocations() {
        ObjectContext objectContext = BaseContext.getThreadObjectContext();

        // select root locations
        SelectQuery query = new SelectQuery(LibraryLocation.class);

        List<LibraryLocation> locations = objectContext.performQuery(query);

        return locations != null ? locations : Collections.EMPTY_LIST;
    }

    @Override
    public void create(LibraryLocation location) throws CommitException {

        // get current ObjectContext
        ObjectContext objectContext = BaseContext.getThreadObjectContext();

        // register new reserve collection so it is going to be persisted to db
        objectContext.registerNewObject(location);

        try {

            objectContext.commitChanges();
            LOG.info("new library location created: " + location);
        } catch (ValidationException e) {
            objectContext.rollbackChanges();
            throw new CommitException("could not commit location " + location, e);
        }
    }

    @Override
    public void delete(LibraryLocation location) throws DeleteException {

        if (location.hasChildren())
//                || (location.getReserveCollections() != null && !location.getReserveCollections().isEmpty()))
            throw new DeleteException("can't delete used location " + location.getName());

        // get current ObjectContext
        ObjectContext objectContext = BaseContext.getThreadObjectContext();

        try {

            // mark location as deleted
            objectContext.deleteObjects(location);

            objectContext.commitChanges();
            LOG.info("location deleted: " + location);
        } catch (DeleteDenyException e) {
            objectContext.rollbackChanges();
            throw new DeleteException("could not delete location " + location, e);
        }
    }

    @Override
    public void update(LibraryLocation location) throws CommitException {

        ObjectContext objectContext = location.getObjectContext(); // same as BaseContext.getThreadObjectContext()
        try {

            objectContext.commitChanges();
        } catch (ValidationException e) {
            LOG.error("could not update location: " + e.getMessage());
            objectContext.rollbackChanges();
            throw new CommitException("could not update location " + location, e);
        }
    }
}
