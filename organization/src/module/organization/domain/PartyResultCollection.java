/*
 * @(#)PartyResultCollection.java
 *
 * Copyright 2009 Instituto Superior Tecnico
 * Founding Authors: João Figueiredo, Luis Cruz, Paulo Abrantes, Susana Fernandes
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the Organization Module for the MyOrg web application.
 *
 *   The Organization Module is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published
 *   by the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.*
 *
 *   The Organization Module is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with the Organization Module. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package module.organization.domain;

import java.util.Collection;
import java.util.HashSet;

public class PartyResultCollection {
    private PartyPredicate predicate;
    private Collection<Party> result;

    PartyResultCollection(final PartyPredicate predicate) {
	this(new HashSet<Party>(), predicate);
    }

    PartyResultCollection(final Collection<Party> result, final PartyPredicate predicate) {
	this.predicate = predicate;
	this.result = result;
    }

    public boolean candAddParty(final Party party, final Accountability accountability) {
	return predicate.eval(party, accountability);
    }

    /**
     * Add Party to result if candAddParty is true and if this collection permit
     * duplicates and does not contains the specified element.
     * 
     */
    public boolean conditionalAddParty(final Party party, final Accountability accountability) {
	return candAddParty(party, accountability) && result.add(party);
    }

    @SuppressWarnings("unchecked")
    public <T extends Party> Collection<T> getResult() {
	return (Collection<T>) result;
    }
}
