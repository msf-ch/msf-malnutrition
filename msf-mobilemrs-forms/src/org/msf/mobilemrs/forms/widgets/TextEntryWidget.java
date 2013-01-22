/**
 * 
 */
package org.msf.mobilemrs.forms.widgets;

import org.jsoup.nodes.Element;
import org.msf.mobilemrs.forms.HtmlFormContext;


/**
 * @author Nicholas Wilkie
 *
 */
public class TextEntryWidget {
	public Element getWidgetElement(HtmlFormContext context) {
		Element result = context.createElement("input");
		result.attr("type", "text");
		
		return result;
	}
}
