/**
 * 
 */
package org.msf.mobilemrs.forms;

import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * @author Nicholas Wilkie
 *
 */
public class HtmlFormContext {
	private Document document;
	private List<Element> dataElements;
	
	public HtmlFormContext(Document document) {
		this.document = document;
	}
	
	public Element createElement(String tagName) {
		return document.createElement(tagName);
	}
	
	public Document getDocument() {
		return document;
	}
	
	public boolean addDataElement(Element dataElement) {
		return dataElements.add(dataElement);
	}
}
