package unidue.lib.model.miless.dao;

import unidue.lib.model.miless.dto.MCRCategory;

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


/**
 * An instance of {@link MCRCategoryDAO} can be used to load {@link MCRCategory}
 * objects from backend.
 *
 * @author Nils Verheyen
 * @see MCRCategoryDAOImpl
 */
public interface MCRCategoryDAO {

    /**
     * Returns the {@link MCRCategory} with target id if one is available.
     *
     * @param id id of the category
     * @return a {@link MCRCategory} if it could be found, <code>null</code>
     * otherwise.
     */
    MCRCategory getCategoryById(Integer id);
}
