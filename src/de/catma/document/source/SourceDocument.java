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
package de.catma.document.source;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.catma.document.Range;
import de.catma.document.source.contenthandler.SourceContentHandler;
import de.catma.document.standoffmarkup.staticmarkup.StaticMarkupCollectionReference;
import de.catma.document.standoffmarkup.staticmarkup.StaticMarkupInstance;
import de.catma.document.standoffmarkup.usermarkup.UserMarkupCollectionReference;

/**
 * A source document is a sequence of text loaded and managed by a {@link SourceContentHandler}.
 * Markup can be attached to the source document.
 * 
 * @author marco.petris@web.de
 * @see SourceContentHandler
 * @see SourceDocumentHandler
 * @see de.catma.document.standoffmarkup.usermarkup.UserMarkupCollection
 * @see de.catma.document.standoffmarkup.staticmarkup.StaticMarkupCollection
 */
/**
 * @author marco.petris@web.de
 *
 */
public class SourceDocument {

	private String id;
	private SourceContentHandler sourceContentHandler;
	private List<StaticMarkupCollectionReference> staticMarkupCollectionRefs;
	private List<UserMarkupCollectionReference> userMarkupCollectionRefs;
	private Integer length = null;
	
	/**
	 * @param id identifier for this document
	 * @param handler the appropriate content handler
	 * @see SourceDocumentHandler
	 */
	SourceDocument(String id, SourceContentHandler handler) {
		this.id = id;
		this.sourceContentHandler = handler;
		this.staticMarkupCollectionRefs = new ArrayList<StaticMarkupCollectionReference>();
		this.userMarkupCollectionRefs = new ArrayList<UserMarkupCollectionReference>();
	}

	/**
	 * displays title or id
	 */
	@Override
	public String toString() {
		String title = 
				sourceContentHandler.getSourceDocumentInfo().getContentInfoSet().getTitle();
		return ((title == null) || (title.isEmpty()))? id : title;
	}

	/**
	 * @param range
	 * @return the part of the content specified by range
	 * @throws IOException error while accessing the content
	 */
	public String getContent( Range range ) throws IOException {
		int length = getContent().length();
		return getContent().substring(
				Math.min(range.getStartPoint(), length), 
				Math.min(range.getEndPoint(), length));
	}
	
	/**
	 * @return the full text of this document
	 * @throws IOException error while accessing the content
	 */
	public String getContent() throws IOException {
		return sourceContentHandler.getContent();
	}

	/**
	 * Attaches a collection of static markup to this document.
	 * @param staticMarkupCollRef static markup
	 */
	public void addStaticMarkupCollectionReference(
			StaticMarkupCollectionReference staticMarkupCollRef) {
		staticMarkupCollectionRefs.add(staticMarkupCollRef);
	}

	/**
	 * Attaches a collection of user defined markup to this document.
	 * @param userMarkupCollRef user markup
	 */
	public void addUserMarkupCollectionReference(
			UserMarkupCollectionReference userMarkupCollRef) {
		userMarkupCollectionRefs.add(userMarkupCollRef);
	}

	/**
	 * @return the identifier of this document, depending on the underlying repository
	 */
	public String getID() {
		return id;
	}
	
	/**
	 * @return all static markup attached
	 */
	public List<StaticMarkupCollectionReference> getStaticMarkupCollectionRefs() {
		return Collections.unmodifiableList(staticMarkupCollectionRefs);
	}
	
	/**
	 * @return all user defined markup attached
	 */
	public List<UserMarkupCollectionReference> getUserMarkupCollectionRefs() {
		return Collections.unmodifiableList(userMarkupCollectionRefs);
	}
	
	/**
	 * @param id the identifier of the {@link UserMarkupCollection}
	 * @return the reference to the user markup collection or <code>null</code> if
	 * there is no such collection
	 */
	public UserMarkupCollectionReference getUserMarkupCollectionReference(String id) {
		for (UserMarkupCollectionReference ref : userMarkupCollectionRefs) {
			if (ref.getId().equals(id)) {
				return ref;
			}
		}
		return null;
	}
	
	/**
	 * @param uRef to be removed
	 * @return true if the uRef had been attached before
	 */
	public boolean removeUserMarkupCollectionReference(
			UserMarkupCollectionReference uRef) {
		return this.userMarkupCollectionRefs.remove(uRef);
	}
	
	/**
	 * @return the content handler of this document
	 */
	public SourceContentHandler getSourceContentHandler() {
		return sourceContentHandler;
	}

	/**
	 * @return length of the content of this document
	 * @throws IOException error accessing the content
	 */
	public int getLength() throws IOException {
		if (length == null) {
			length = getContent().length();
		}
		return length;
	}
	
	/**
	 * Unloads the content.
	 */
	public void unload() {
		sourceContentHandler.unload();
	}

	/**
	 * @return <code>true</code> if the content is loaded
	 */
	public boolean isLoaded() {
		return sourceContentHandler.isLoaded();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SourceDocument other = (SourceDocument) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

}