/*
 * @(#)Party.java
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
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import module.organization.domain.predicates.PartyPredicate;
import module.organization.domain.predicates.PartyResultCollection;
import module.organization.domain.predicates.PartyPredicate.PartyByAccountabilityType;
import module.organization.domain.predicates.PartyPredicate.PartyByClassType;
import module.organization.domain.predicates.PartyPredicate.PartyByPartyType;
import module.organization.domain.predicates.PartyPredicate.TruePartyPredicate;
import myorg.domain.MyOrg;
import myorg.domain.exceptions.DomainException;

import org.joda.time.LocalDate;

import pt.ist.fenixWebFramework.services.Service;
import pt.ist.fenixframework.pstm.Transaction;

abstract public class Party extends Party_Base {

    static public final Comparator<Party> COMPARATOR_BY_NAME = new Comparator<Party>() {
	@Override
	public int compare(Party o1, Party o2) {
	    int res = o1.getPartyName().compareTo(o2.getPartyName());
	    return res != 0 ? res : (o1.getOID() < o2.getOID() ? -1 : (o1.getOID() == o2.getOID() ? 0 : 1));
	}
    };

    protected Party() {
	super();
	setMyOrg(MyOrg.getInstance());
    }

    protected void check(final Object obj, final String message) {
	if (obj == null) {
	    throw new DomainException(message);
	}
    }

    public Collection<Party> getParents() {
	return getParents(new TruePartyPredicate());
    }

    public Collection<Party> getParents(final AccountabilityType type) {
	return getParents(new PartyByAccountabilityType(type));
    }

    public Collection<Party> getParents(final PartyType type) {
	return getParents(new PartyByPartyType(type));
    }

    public Collection<Unit> getParentUnits() {
	return getParents(new PartyByClassType(Unit.class));
    }

    public Collection<Unit> getParentUnits(final AccountabilityType type) {
	return getParents(new PartyByAccountabilityType(Unit.class, type));
    }

    public Collection<Unit> getParentUnits(final PartyType type) {
	return getParents(new PartyByPartyType(Unit.class, type));
    }

    @SuppressWarnings("unchecked")
    protected <T extends Party> Collection<T> getParents(final PartyPredicate predicate) {
	final Collection<Party> result = new LinkedList<Party>();
	for (final Accountability accountability : getParentAccountabilities()) {
	    if (predicate.eval(accountability.getParent(), accountability)) {
		result.add(accountability.getParent());
	    }
	}
	return (List<T>) result;
    }

    public Collection<Party> getChildren() {
	return getChildren(new TruePartyPredicate());
    }

    public Collection<Party> getChildren(final AccountabilityType type) {
	return getChildren(new PartyByAccountabilityType(type));
    }

    public Collection<Party> getChildren(final PartyType type) {
	return getChildren(new PartyByPartyType(type));
    }

    public Collection<Unit> getChildUnits() {
	return getChildren(new PartyByClassType(Unit.class));
    }

    public Collection<Unit> getChildUnits(final AccountabilityType type) {
	return getChildren(new PartyByAccountabilityType(Unit.class, type));
    }

    public Collection<Unit> getChildUnits(final PartyType type) {
	return getChildren(new PartyByPartyType(Unit.class, type));
    }

    public Collection<Person> getChildPersons() {
	return getChildren(new PartyByClassType(Person.class));
    }

    public Collection<Person> getChildPersons(final AccountabilityType type) {
	return getChildren(new PartyByAccountabilityType(Person.class, type));
    }

    public Collection<Person> getChildPersons(final PartyType type) {
	return getChildren(new PartyByPartyType(Person.class, type));
    }

    @SuppressWarnings("unchecked")
    protected <T extends Party> Collection<T> getChildren(final PartyPredicate predicate) {
	final Collection<Party> result = new LinkedList<Party>();
	for (final Accountability accountability : getChildAccountabilities()) {
	    if (predicate.eval(accountability.getChild(), accountability)) {
		result.add(accountability.getChild());
	    }
	}
	return (List<T>) result;
    }

    public Collection<Party> getAncestors() {
	final PartyResultCollection result = new PartyResultCollection(new TruePartyPredicate());
	getAncestors(result);
	return result.getResult();
    }

    public Collection<Party> getAncestors(final AccountabilityType type) {
	final PartyResultCollection result = new PartyResultCollection(new PartyByAccountabilityType(type));
	getAncestors(result);
	return result.getResult();
    }

    public Collection<Party> getAncestors(final PartyType type) {
	final PartyResultCollection result = new PartyResultCollection(new PartyByPartyType(type));
	getAncestors(result);
	return result.getResult();
    }

    public Collection<Unit> getAncestorUnits() {
	final PartyResultCollection result = new PartyResultCollection(new PartyByClassType(Unit.class));
	getAncestors(result);
	return result.getResult();
    }

    public Collection<Unit> getAncestorUnits(final AccountabilityType type) {
	final PartyResultCollection result = new PartyResultCollection(new PartyByAccountabilityType(Unit.class, type));
	getAncestors(result);
	return result.getResult();
    }

    public Collection<Unit> getAncestorUnits(final PartyType type) {
	final PartyResultCollection result = new PartyResultCollection(new PartyByPartyType(Unit.class, type));
	getAncestors(result);
	return result.getResult();
    }

    protected void getAncestors(final PartyResultCollection result) {
	for (final Accountability accountability : getParentAccountabilities()) {
	    result.conditionalAddParty(accountability.getParent(), accountability);
	    accountability.getParent().getAncestors(result);
	}
    }

    public boolean ancestorsInclude(final Party party, final AccountabilityType type) {
	for (final Accountability accountability : getParentAccountabilities()) {
	    if (accountability.hasAccountabilityType(type)) {
		if (accountability.getParent().equals(party)) {
		    return true;
		}
		if (accountability.getParent().ancestorsInclude(party, type)) {
		    return true;
		}
	    }
	}

	return false;
    }

    public Collection<Party> getDescendents() {
	final PartyResultCollection result = new PartyResultCollection(new TruePartyPredicate());
	getDescendents(result);
	return result.getResult();
    }

    public Collection<Party> getDescendents(final AccountabilityType type) {
	final PartyResultCollection result = new PartyResultCollection(new PartyByAccountabilityType(type));
	getDescendents(result);
	return result.getResult();
    }

    public Collection<Party> getDescendents(final PartyType type) {
	final PartyResultCollection result = new PartyResultCollection(new PartyByPartyType(type));
	getDescendents(result);
	return result.getResult();
    }

    public Collection<Unit> getDescendentUnits() {
	final PartyResultCollection result = new PartyResultCollection(new PartyByClassType(Unit.class));
	getDescendents(result);
	return result.getResult();
    }

    public Collection<Unit> getDescendentUnits(final AccountabilityType type) {
	final PartyResultCollection result = new PartyResultCollection(new PartyByAccountabilityType(Unit.class, type));
	getDescendents(result);
	return result.getResult();
    }

    public Collection<Unit> getDescendentUnits(final PartyType type) {
	final PartyResultCollection result = new PartyResultCollection(new PartyByPartyType(Unit.class, type));
	getDescendents(result);
	return result.getResult();
    }

    public Collection<Person> getDescendentPersons() {
	final PartyResultCollection result = new PartyResultCollection(new PartyByClassType(Person.class));
	getDescendents(result);
	return result.getResult();
    }

    public Collection<Person> getDescendentPersons(final AccountabilityType type) {
	final PartyResultCollection result = new PartyResultCollection(new PartyByAccountabilityType(Person.class, type));
	getDescendents(result);
	return result.getResult();
    }

    public Collection<Person> getDescendentPersons(final PartyType type) {
	final PartyResultCollection result = new PartyResultCollection(new PartyByPartyType(Person.class, type));
	getDescendents(result);
	return result.getResult();
    }

    protected void getDescendents(final PartyResultCollection result) {
	for (final Accountability accountability : getChildAccountabilities()) {
	    result.conditionalAddParty(accountability.getChild(), accountability);
	    accountability.getChild().getDescendents(result);
	}
    }

    public Collection<Party> getSiblings() {
	final Collection<Party> result = new LinkedList<Party>();
	for (final Accountability accountability : getParentAccountabilities()) {
	    result.addAll(accountability.getParent().getChildren());
	}
	result.remove(this);
	return result;
    }

    public boolean isUnit() {
	return false;
    }

    public boolean isPerson() {
	return false;
    }

    @Service
    public void delete() {
	canDelete();
	disconnect();
	Transaction.deleteObject(this);
    }

    protected void canDelete() {
	if (hasAnyChildAccountabilities()) {
	    throw new DomainException("error.Party.delete.has.child.accountabilities");
	}
    }

    protected void disconnect() {
	while (hasAnyParentAccountabilities()) {
	    getParentAccountabilities().get(0).delete();
	}
	getPartyTypes().clear();
	removeMyOrg();
    }

    @Service
    public void addParent(final Party parent, final AccountabilityType type, final LocalDate begin, final LocalDate end) {
	Accountability.create(parent, this, type, begin, end);
    }

    @Service
    public void addChild(final Party child, final AccountabilityType type, final LocalDate begin, final LocalDate end) {
	Accountability.create(this, child, type, begin, end);
    }

    @Service
    public void removeParent(final Accountability accountability) {
	if (hasParentAccountabilities(accountability)) {
	    if (getParentAccountabilitiesCount() == 1) {
		throw new DomainException("error.Party.cannot.remove.parent.accountability");
	    }
	    accountability.delete();
	}
    }

    @Service
    public void editPartyTypes(final List<PartyType> partyTypes) {
	getPartyTypes().retainAll(partyTypes);
	getPartyTypes().addAll(partyTypes);

	if (getPartyTypesSet().isEmpty()) {
	    throw new DomainException("error.Party.must.have.at.least.one.party.type");
	}
	if (!accountabilitiesStillValid()) {
	    throw new DomainException("error.Party.invalid.party.types.accountability.rules.are.not.correct");
	}

    }

    protected boolean accountabilitiesStillValid() {
	for (final Accountability accountability : getParentAccountabilitiesSet()) {
	    if (!accountability.isValid()) {
		return false;
	    }
	}
	return true;
    }

}
