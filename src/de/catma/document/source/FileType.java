/*   
 *   CATMA Computer Aided Text Markup and Analysis
 *   
 *   Copyright (C) 2009  University Of Hamburg
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * The type of a file: pdf, html, text...
 *
 * @author marco.petris@web.de
 *
 */
public enum FileType {
	/**
	 * MS-Word DOCs.
	 */
	DOC("application/msword"), 
	/**
	 * PDFs.
	 */
	PDF("application/pdf"), 
	XPDF("application/x-pdf"),
	/**
	 * HTML-pages.
	 */
	HTML("text/html"),
    /**
     * HTM(L)-pages.
     */
    HTM("text/html"),
    /**
     * RTF-docs.
     */
    RTF("application/rtf"),
	/**
	 * everything which is not one of the other possibilities
	 */
	TEXT("text/plain"),
	/**
	 * XML files.
	 */
	XML("application/xml"), 
	TEI("application/tei+xml", false), // not active since support would require a proper way to display structural elements and their customizations
	/**
	 * MS-Word DOCX files.
	 */
	DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
	/**
	 * ZIP files.
	 */
	ZIP("application/zip")
	;
	
	private String mimeType;
	private boolean active;

	private FileType(String mimeType) {
		this(mimeType, true);
	}	
	
	private FileType(String mimeType, boolean active) {
		this.mimeType = mimeType;
		this.active = active;
	}
	
	/**
	 * @return the mime type of this file type.
	 */
	public String getMimeType() {
		return mimeType;
	}
	
	/**
	 * Tries to guess the file type by analyzing the file extension i. e. the 
	 * characters after the last dot.
	 * @param file the file to analyze
	 * @return the type of the file
	 */
	static FileType getFileType( File file ) {
		return getFileTypeFromName(file.getName());
	}
	
	/**
	 * Tries to guess the file type by analyzing the file extension i. e. the 
	 * characters after the last dot.
	 * @param fileName the filename to analyze
	 * @return the type of the file
	 */
	public static FileType getFileTypeFromName( String fileName ) {
		int indexOflastDot = fileName.lastIndexOf( '.' );

		if ((indexOflastDot != -1) && (indexOflastDot<(fileName.length()-1))){
			String extension = 
					fileName.substring( indexOflastDot+1 ).toUpperCase();
			for (FileType type : values()) {
                if( extension.equals( type.name() ) ) {
                    return type;
                }
            }
        }		
		return TEXT;
	}
	
	/**
	 * @param mimeType
	 * @return the file type that corresponds to the given mime type or, if no specific
	 * file type can be found, {@link #TEXT}.
	 */
	public static FileType getFileType(String mimeType) {
		for (FileType type : values()) {
			if (type.mimeType.equals(mimeType)) {
				return type;
			}
		}
		return TEXT;
	}
	
	public static List<FileType> getActiveFileTypes() {
		List<FileType> result = new ArrayList<FileType>();
		for (FileType type : values()) {
			if (type.active) {
				result.add(type);
			}
		}
		return result;
	}
}
