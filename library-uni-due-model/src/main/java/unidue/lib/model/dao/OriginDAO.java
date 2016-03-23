package unidue.lib.model.dao;

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

import java.util.List;
import java.util.Locale;

import unidue.lib.model.miless.dao.MCRCategoryDAO;
import unidue.lib.model.miless.dto.MCRCategory;

/**
 * An {@link OriginDAO} is a convenience interface to load {@link MCRCategory}
 * objects that belong to the classification <code>ORIGIN</code>. To access all
 * {@link MCRCategory} objects use {@link MCRCategoryDAO} instead of this
 * interface.
 *
 * @author Nils Verheyen
 * @see OriginDAOImpl
 */
public interface OriginDAO {

    /**
     * Returns a {@link List} of all origins exept the root origin, that are
     * available inside the backend.
     *
     * @return a list of all origins that are available or an empty list.
     */
    List<MCRCategory> getOrigins();

    MCRCategory getOrigin(String id);

    String getOriginLabel(Locale locale, Integer originID);
}
