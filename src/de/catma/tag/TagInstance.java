/*   
 *   CATMA Computer Aided Text Markup and Analysis
 *   
 *   Copyright (C) 2009-2013  University Of Hamburg
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.catma.tag;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * An instance of a tag. The TagInstance has a {@link TagDefinition type}, a
 * set of user defined {@link Property properties} and a set of system properties.
 * 
 * @author marco.petris@web.de
 *
 */
public class TagInstance {

	private String uuid;
	private TagDefinition tagDefinition;
	private Map<String,Property> systemProperties;
	private Map<String,Property> userDefinedProperties;
	
	/**
	 * System properties get the {@link PropertyDefinition#getFirstValue() default} value set.
	 * @param uuid CATMA uuid of the instance (see {@link de.catma.util.IDGenerator}.
	 * @param tagDefinition the type of this instance
	 */
	public TagInstance(String uuid, TagDefinition tagDefinition) {
		this.uuid = uuid;
		this.tagDefinition = tagDefinition;

		systemProperties = new HashMap<String, Property>();
		setDefaultValues(
				systemProperties, 
				tagDefinition.getSystemPropertyDefinitions(), true);

		userDefinedProperties = new HashMap<String, Property>();
		setDefaultValues(
			userDefinedProperties, 
			tagDefinition.getUserDefinedPropertyDefinitions(), false);

	}
	
	private void setDefaultValues(
			Map<String,Property> properties,
			Collection<PropertyDefinition> propertyDefinitions, boolean useFirstValue) {
		for (PropertyDefinition pDef : propertyDefinitions) {
			properties.put(
			pDef.getUuid(), 
			new Property(pDef, 
					useFirstValue?new PropertyValueList(pDef.getFirstValue()):
						new PropertyValueList()));
		}
	}

	public TagDefinition getTagDefinition() {
		return tagDefinition;
	}

	public void addSystemProperty(Property property) {
		systemProperties.put(property.getPropertyDefinition().getUuid(), property);
	}
	
	public void addUserDefinedProperty(Property property) {
		userDefinedProperties.put(property.getPropertyDefinition().getUuid(), property);
	}
	
	@Override
	public String toString() {
		return "TAGINSTANCE[#"+uuid+","+tagDefinition+"]";
	}
	
	/**
	 * @return CATMA uuid of the instance (see {@link de.catma.util.IDGenerator}
	 */
	public String getUuid() {
		return uuid;
	}
	
	/**
	 * @param uuid CATMA uuid of the {@link PropertyDefinition}
	 * @return the {@link Property} that belongs to the given {@link PropertyDefinition}
	 * or <code>null</code> if there is no such property
	 */
	public Property getSystemProperty(String uuid) {
		return systemProperties.get(uuid);
	}
	
	/**
	 * @param uuid CATMA uuid of the {@link PropertyDefinition}
	 * @return the {@link Property} that belongs to the given {@link PropertyDefinition}
	 * or <code>null</code> if there is no such property
	 */
	public Property getUserDefinedProperty(String uuid) {
		return userDefinedProperties.get(uuid);
	}
	
	/**
	 * @return non modifiable collection of system properties 
	 * @see PropertyDefinition.SystemPropertyName
	 */
	public Collection<Property> getSystemProperties() {
		return Collections.unmodifiableCollection(systemProperties.values());
	}
	
	/**
	 * @return non modifiable list of user defined properties
	 */
	public Collection<Property> getUserDefinedProperties() {
		return Collections.unmodifiableCollection(userDefinedProperties.values());
	}
	
	/**
	 * Sychnronizes the properties of this instance which the attached 
	 * {@link PropertyDefinition}, property values don't get overridden 
	 */
	public void synchronizeProperties() {
		
		Iterator<Map.Entry<String, Property>> iterator = systemProperties.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, Property> entry = iterator.next();
			Property p = entry.getValue();
			if (getTagDefinition().getPropertyDefinition(entry.getKey())==null) {
				iterator.remove();
			}
			else {
				p.synchronize();
			}
		}
		
		// we do not update Property values, therefore we handle only ...
		
		// ... deletion and ...
		iterator = userDefinedProperties.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, Property> entry = iterator.next();
			
			if (getTagDefinition().getPropertyDefinition(entry.getKey())==null) {
				iterator.remove();
			}
		}
		
		// ... addition of properties
		for (PropertyDefinition pd : getTagDefinition().getUserDefinedPropertyDefinitions()) {
			if (!userDefinedProperties.containsKey(pd.getUuid())) {
				addUserDefinedProperty(new Property(pd, new PropertyValueList()));
			}
		}
	}

	/**
	 * @param uuid CATMA uuid of the {@link PropertyDefinition}
	 * @return the property with the given uuid
	 */
	public Property getProperty(String uuid) {
		if (systemProperties.containsKey(uuid)) {
			return getSystemProperty(uuid);
		}
		return getUserDefinedProperty(uuid);
	}
}
