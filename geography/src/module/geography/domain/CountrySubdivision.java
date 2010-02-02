/*
 * @(#)CountrySubdivision.java
 *
 * Copyright 2009 Instituto Superior Tecnico
 * Founding Authors: João Figueiredo, Luis Cruz, Paulo Abrantes, Susana Fernandes
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the Geography Module for the MyOrg web application.
 *
 *   The Geography Module is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published
 *   by the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.*
 *
 *   The Geography Module is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with the Geography Module. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package module.geography.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import module.organization.domain.Unit;
import myorg.domain.exceptions.DomainException;

import org.joda.time.LocalDate;

import pt.utl.ist.fenix.tools.util.i18n.MultiLanguageString;

/**
 * A {@link Country} subdivision of some level of jurisdiction. Subdivisions are
 * assumed to be Trees, each division has only one parent division, or country
 * (if it's the top level).
 * 
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 */
public class CountrySubdivision extends CountrySubdivision_Base {
    protected CountrySubdivision() {
	super();
    }

    public CountrySubdivision(Country parent, String name, String acronym, String code) {
	this(parent.getUnit(), 1, name, acronym, code);
    }

    public CountrySubdivision(CountrySubdivision parent, String name, String acronym, String code) {
	this(parent.getUnit(), parent.getLevel() + 1, name, acronym, code);
    }

    private CountrySubdivision(Unit parent, Integer level, String name, String acronym, String code) {
	this();
	setUnit(Unit.create(parent, makeName(name, name), acronym, getPartyType("Subdivisão de País",
		COUNTRY_SUBDIVISION_PARTYTYPE_NAME), getOrCreateAccountabilityType(), new LocalDate(), null));
	setLevel(level);
	setCode(code);
    }

    @Override
    public MultiLanguageString getType() {
	return getLevelName();
    }

    public MultiLanguageString getLevelName() {
	return getCountry().getSubdivisionLevelName(getLevel());
    }

    public Country getCountry() {
	if (getLevel() == 1)
	    return (Country) getParentLocation();
	return getParentSubdivision().getCountry();
    }

    public CountrySubdivision getParentSubdivision() {
	if (getLevel() == 1)
	    throw new DomainException("error.geography.requesting-parent-subdivision-at-level-one");
	return (CountrySubdivision) getParentLocation();
    }

    public Collection<CountrySubdivision> getChildren() {
	Collection<Unit> units = getChildUnits();
	Collection<CountrySubdivision> children = new ArrayList<CountrySubdivision>();
	for (Unit unit : units) {
	    children.add((CountrySubdivision) unit.getGeographicLocation());
	}
	return children;
    }

    public CountrySubdivision getChildByAcronym(String acronym) {
	for (Unit unit : getChildUnits()) {
	    if (unit.getAcronym().equals(acronym)) {
		return (CountrySubdivision) unit.getGeographicLocation();
	    }
	}
	return null;
    }

    public CountrySubdivision getChildByCode(String... codes) {
	String code = codes[0];
	for (CountrySubdivision subdivision : getChildren()) {
	    if (subdivision.getCode().equals(code)) {
		if (codes.length > 1) {
		    return subdivision.getChildByCode(Arrays.asList(codes).subList(1, codes.length).toArray(new String[0]));
		} else {
		    return subdivision;
		}
	    }
	}
	return null;
    }

    public MultiLanguageString getFullName() {
	if (getLevel() == 1)
	    return getName();
	return getName().append(", ").append(getParentSubdivision().getFullName());
    }

    protected String getExtendedName() {
	return "[" + getCode() + "-" + getName().getContent() + "]";
    }

    @Override
    public String toString() {
	if (getLevel() == 1)
	    return getExtendedName();
	return getParentSubdivision().toString() + " " + getExtendedName();
    }
}
