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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * A set of {@link TagDefinition}s.
 * 
 * @author marco.petris@web.de
 *
 */
public class TagsetDefinition implements Versionable, Iterable<TagDefinition> {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	private Integer id;
	private String uuid;
	private String name;
	private Version version;
	private Map<String,TagDefinition> tagDefinitions;
	private Map<String,Set<String>> tagDefinitionChildren;
	
	/**
	 * @param id a repository dependent identifier
	 * @param uuid the CATMA uuid, see {@link de.catma.util.IDGenerator}
	 * @param tagsetName the name of the tagset
	 * @param version the version of the tagset
	 */
	public TagsetDefinition(
			Integer id, String uuid, String tagsetName, Version version) {
		this.id = id;
		this.uuid = uuid;
		this.name = tagsetName;
		this.version = version;
		this.tagDefinitions = new HashMap<String, TagDefinition>();
		this.tagDefinitionChildren = new HashMap<String, Set<String>>();
	}

	/**
	 * Copy constructor
	 * @param toCopy
	 */
	public TagsetDefinition(TagsetDefinition toCopy) {
		this (null, toCopy.uuid, toCopy.name, new Version(toCopy.version));
		for (TagDefinition tagDefinition : toCopy) {
			addTagDefinition(new TagDefinition(tagDefinition));
		}
	}

	public Version getVersion() {
		return version;
	}
	
	@Override
	public String toString() {
		return "TAGSET_DEF["+name+",#"+uuid+","+version+"]";
	}

	public void addTagDefinition(TagDefinition tagDef) {
		tagDefinitions.put(tagDef.getUuid(),tagDef);
		if (!tagDefinitionChildren.containsKey(tagDef.getParentUuid())) {
			tagDefinitionChildren.put(
					tagDef.getParentUuid(), new HashSet<String>());
		}
		tagDefinitionChildren.get(
				tagDef.getParentUuid()).add(tagDef.getUuid());
	}
	
	public String getUuid() {
		return uuid;
	}
	
	/**
	 * @param tagDefID CATMA uuid of the {@link TagDefinition}, see {@link de.catma.util.IDGenerator}
	 * @return <code>true</code> if this tagset def contains the corresponding tag def
	 */
	public boolean hasTagDefinition(String tagDefID) {
		return tagDefinitions.containsKey(tagDefID);
	}
	
	/**
	 * @param tagDefinitionID CATMA uuid of the {@link TagDefinition}, see {@link de.catma.util.IDGenerator}
	 * @return the corresponding TagDefinition or <code>null</code> if there is no such definition in this
	 * tagset def
	 */
	public TagDefinition getTagDefinition(String tagDefinitionID) {
		return tagDefinitions.get(tagDefinitionID);
	}
	
	public Iterator<TagDefinition> iterator() {
		return Collections.unmodifiableCollection(tagDefinitions.values()).iterator();
	}
	
	public String getName() {
		return name;
	}

	public boolean contains(TagDefinition tagDefinition) {
		return tagDefinitions.values().contains(tagDefinition);
	}

	public List<TagDefinition> getDirectChildren(TagDefinition tagDefinition) {
		List<TagDefinition> children = new ArrayList<TagDefinition>();
		Set<String> directChildrenIDs = 
				tagDefinitionChildren.get(tagDefinition.getUuid());
		
		if (directChildrenIDs == null) {
			return Collections.emptyList();
			
		}
		
		for (String childID : directChildrenIDs) {
			TagDefinition child = getTagDefinition(childID); 
			children.add(child);
		}
		
		return Collections.unmodifiableList(children);
	}
	
	/**
	 * @param tagDefinition
	 * @return an unmodifiable list of all child TagDefinitions of the given
	 * TagDefinition (deep list)
	 */
	public List<TagDefinition> getChildren(TagDefinition tagDefinition) {
		List<TagDefinition> children = new ArrayList<TagDefinition>();
		Set<String> directChildrenIDs = 
				tagDefinitionChildren.get(tagDefinition.getUuid());
		
		if (directChildrenIDs == null) {
			return Collections.emptyList();
			
		}
		
		for (String childID : directChildrenIDs) {
			TagDefinition child = getTagDefinition(childID); 
			children.add(child);
			children.addAll(getChildren(child));
		}

		return Collections.unmodifiableList(children);
	}

	/**
	 * @param tagDefinition
	 * @return a set of the uuids of the child TagDefinitions of the given
	 * TagDefinition
	 */
	Set<String> getChildIDs(TagDefinition tagDefinition) {
		Set<String> childIDs = new HashSet<String>();
		Set<String> directChildrenIDs = 
				tagDefinitionChildren.get(tagDefinition.getUuid());
		
		if (directChildrenIDs == null) {
			return Collections.emptySet();
			
		}
		
		for (String childID : directChildrenIDs) {
			TagDefinition child = getTagDefinition(childID); 
			childIDs.add(child.getUuid());
			childIDs.addAll(getChildIDs(child));
		}

		return Collections.unmodifiableSet(childIDs);	
	}

	void setName(String name) {
		this.name = name;
	}

	public void remove(TagDefinition tagDefinition) {
		for (TagDefinition child : getChildren(tagDefinition)) {
			remove(child);
		}
		this.tagDefinitions.remove(tagDefinition.getUuid());
		removeFromChildrenCache(tagDefinition);
	}
	
	private void removeFromChildrenCache(TagDefinition tagDefinition) {
		Set<String> childrenOfParent = this.tagDefinitionChildren.get(
				tagDefinition.getParentUuid());
		if (childrenOfParent != null) {
			childrenOfParent.remove(tagDefinition.getUuid());
		}
		this.tagDefinitionChildren.remove(tagDefinition.getUuid());
	}

	/**
	 * @param tagDefinition
	 * @return the path from the top level TagDefinition down to the given TagDefintion
	 */
	public String getTagPath(TagDefinition tagDefinition) {
		
		StringBuilder builder = new StringBuilder();
		builder.append("/");
		builder.append(tagDefinition.getName());
		String baseID = tagDefinition.getParentUuid();
		
		while (!baseID.isEmpty()) {
			TagDefinition parentDef = getTagDefinition(baseID);
			builder.insert(0, parentDef.getName());
			builder.insert(0, "/");
			
			baseID = parentDef.getParentUuid();
		}
		
		return builder.toString();
	}

	/**
	 * @return repository dependent identifier
	 */
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	
	/**
	 * Synchronizes this definition with the given definition. Deletions resulting
	 * from this synch can be retrieved via {@link #getDeletedTagDefinitions()}.
	 * @param tagsetDefinition
	 * @throws IllegalArgumentException if the {@link #getUuid() uuids} of the
	 * definitions are not equal
	 */
	void synchronizeWith(
			TagsetDefinition tagsetDefinition) throws IllegalArgumentException {
		if (!this.getUuid().equals(tagsetDefinition.getUuid())) {
			throw new IllegalArgumentException(
				"can only synch between different versions of the same uuid, this! uuid #" 
				+ this.getUuid() + " incoming uuid #" + tagsetDefinition.getUuid());
		}
		
		if (!tagsetDefinition.getVersion().equals(this.getVersion())) {
			this.setName(tagsetDefinition.getName());
			this.version = new Version(tagsetDefinition.getVersion());
		}
		
		Iterator<Map.Entry<String,TagDefinition>> iterator = 
				tagDefinitions.entrySet().iterator();
		
		while(iterator.hasNext()) {
			TagDefinition td = iterator.next().getValue();
			
			if (tagsetDefinition.hasTagDefinition(td.getUuid())) {
				TagDefinition other = 
						tagsetDefinition.getTagDefinition(td.getUuid());
				if (!td.getVersion().equals(other.getVersion())) {
					logger.info("synching " + td + " with " + other);
					td.synchronizeWith(other, this);
				}
			}
			else {
				logger.info("marking " + td + " in " + this + " as deleted");
				iterator.remove();
				removeFromChildrenCache(td);
			}
			
		}
		for (TagDefinition td : tagsetDefinition) {
			if (!this.hasTagDefinition(td.getUuid())) {
				logger.info("adding " + td + " to " + this + " because of synch");
				addTagDefinition(new TagDefinition(td));
			}
		}
	}
	
	/**
	 * @param tagsetDefinition
	 * @return true if this definition and the given definition are in 
	 * {@link #synchronizeWith(TagsetDefinition) synch}.
	 */
	public boolean isSynchronized(TagsetDefinition tagsetDefinition) {
		
		if (this.getVersion().equals(tagsetDefinition.getVersion())) {
			for (TagDefinition td : this) {
				if (tagsetDefinition.hasTagDefinition(td.getUuid())) {
					if (!td.getVersion().equals(
							tagsetDefinition.getTagDefinition(
									td.getUuid()).getVersion())) {
						return false;
					}
				}
				else {
					return false;
				}
			}
			
			for (TagDefinition td : tagsetDefinition) {
				if (!this.hasTagDefinition(td.getUuid())) {
					return false; 
				}
			}
			
			return true;
		}
		return false;
	}
	
	void setVersion() {
		this.version = new Version();
	}
	
	public boolean isEmpty() {
		return tagDefinitions.isEmpty();
	}
}
