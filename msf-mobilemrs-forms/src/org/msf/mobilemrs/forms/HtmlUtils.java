/**
 * 
 */
package org.msf.mobilemrs.forms;

import java.util.List;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

/**
 * @author Nicholas Wilkie
 *
 */
public final class HtmlUtils {
	public static void copyAttributes(Node source, Node dest, List<String> ignore) {
		Attributes attrs = source.attributes();
		List<Attribute> attrsList = attrs.asList();
		String key;
		for(Attribute attr : attrsList) {
			key = attr.getKey();
			if (ignore != null) {
				for (String ignoreTest : ignore) {
					if (ignoreTest.equalsIgnoreCase(key)) {
						continue;
					}
				}
			}
			dest.attr(attr.getKey(), attr.getValue());
		}
	}
	
	public static void replaceAndRetainChildren(Node oldElement, Element newElement) {
		for (Node sourceChild : oldElement.childNodes()) {
			newElement.appendChild(sourceChild);
		}
		
		oldElement.replaceWith(newElement);
	}
}
