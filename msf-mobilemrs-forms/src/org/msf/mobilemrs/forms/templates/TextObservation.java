/**
 * 
 */
package org.msf.mobilemrs.forms.templates;

import org.jsoup.nodes.Element;
import org.msf.mobilemrs.forms.HtmlFormContext;
import org.msf.mobilemrs.forms.HtmlUtils;
import org.msf.mobilemrs.forms.Template;
import org.msf.mobilemrs.forms.widgets.TextEntryWidget;


/**
 * @author Nicholas Wilkie
 *
 */
public class TextObservation extends Template {
	
	public static final String TEXT_OBS_TAG = "textobs";
	TextEntryWidget textEntryWidget = new TextEntryWidget();
	
	@Override
	public boolean matchesTemplate(Element element) {
		return element.tagName().equalsIgnoreCase(TEXT_OBS_TAG);
	}

	@Override
	public void applyTemplate(Element matchedElement, HtmlFormContext context) {
		Element newElement = textEntryWidget.getWidgetElement(context);
		HtmlUtils.copyAttributes(matchedElement, newElement, null);
		matchedElement.replaceWith(newElement);
	}
	
	
}
