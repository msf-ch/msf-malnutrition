/**
 * 
 */
package org.msf.mobilemrs.forms;

import org.jsoup.nodes.Element;

/**
 * @author Nicholas Wilkie
 * 
 * 
 */
public abstract class Template {
	public abstract boolean matchesTemplate(Element element);
	
	public abstract void applyTemplate(Element matchedElement, HtmlFormContext context);
}
