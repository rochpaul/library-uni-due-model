package unidue.lib.model.dao;

import unidue.lib.model.dao.impl.LibraryLocationDAOImpl;

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

import unidue.lib.model.dto.LibraryLocation;
import unidue.lib.model.exception.CommitException;
import unidue.lib.model.exception.DeleteException;

import java.util.List;

/**
 * A <code>LibraryLocationDAO</code> should be used as default access object to load, update and delete {@link
 * LibraryLocation} objects from backend.
 *
 * @author Nils Verheyen
 * @see LibraryLocationDAOImpl
 */
public interface LibraryLocationDAO {

    /**
     * Returns all {@link LibraryLocation} objects that have no parent.
     *
     * @return the list with all locations or an empty list.
     */
    List<LibraryLocation> getRootLocations();

    /**
     * Returns the {@link LibraryLocation} with target id, <code>null</code> if it does not exist.
     *
     * @param id id of the location
     * @return the location if one could be found, <code>null</code> otherwise
     */
    LibraryLocation getLocationById(Integer id);

    /**
     * Stores target {@link LibraryLocation} in backend.
     *
     * @param location location to create
     * @throws CommitException thrown if any that inside the {@link LibraryLocation} is invalid. take a look at the
     *                         modeler to see required values.
     */
    void create(LibraryLocation location) throws CommitException;

    /**
     * Deletes target {@link LibraryLocation} in backend.
     *
     * @param location {@link LibraryLocation} which should be deleted.
     * @throws DeleteException thrown if a error occured during delete of target {@link LibraryLocation}
     */
    void delete(LibraryLocation location) throws DeleteException;

    /**
     * Updates target {@link LibraryLocation} in backend.
     *
     * @param location {@link LibraryLocation} which should be updated.
     * @throws CommitException thrown if any that inside the {@link LibraryLocation} is invalid. take a look at the
     *                         modeler to see required values.
     */
    void update(LibraryLocation location) throws CommitException;

    /**
     * Returns a list of all available {@link LibraryLocation} objects.
     *
     * @return a list with all locations or an empty list.
     */
    List<LibraryLocation> getLocations();
}
