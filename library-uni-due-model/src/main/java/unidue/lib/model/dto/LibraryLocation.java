package unidue.lib.model.dto;

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

import com.fasterxml.jackson.annotation.JsonGetter;

import unidue.lib.model.auto._LibraryLocation;

import java.util.List;

public class LibraryLocation extends _LibraryLocation {

    private static final long serialVersionUID = 1L;

    @JsonGetter
    public Integer getId() {
        return (getObjectId() != null && !getObjectId().isTemporary())
                ? (Integer) getObjectId().getIdSnapshot().get(ID_PK_COLUMN)
                : null;
    }

    public boolean hasChildren() {
        List<LibraryLocation> childLocations = getChildLocations();
        return childLocations != null && !childLocations.isEmpty();
    }

    @JsonGetter
    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LibraryLocation))
            return false;

        LibraryLocation other = (LibraryLocation) obj;
        return objectId.equals(other.objectId);
    }

    @Override
    public int hashCode() {
        return objectId.hashCode();
    }

//    @Override
//    public void accept(CollectionVisitor visitor) {
//        visitor.visit(this);
//    }
}
