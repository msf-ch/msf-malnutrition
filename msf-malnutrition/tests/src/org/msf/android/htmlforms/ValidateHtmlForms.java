/**
 * 
 */
package org.msf.android.htmlforms;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.msf.android.test.TestConstants;

import android.database.DatabaseUtils;
import android.test.AndroidTestCase;
import android.util.Log;
import au.com.bytecode.opencsv.CSVWriter;

/**
 * @author Nicholas Wilkie
 * 
 */
public class ValidateHtmlForms extends AndroidTestCase {

	public static final String FORMS_TO_TEST = "child_malnutrition/html";
	public static final String[] DEBUG_FILE_COLUMNS = {"Input ID", "Type", "Label", "Input ID", "Type", "Label"};

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testFormStructure() throws Exception {
		String[] testForms = getContext().getAssets().list(FORMS_TO_TEST);
		boolean allValid = true;
		for (String formName : testForms) {
			if (formName.endsWith(".html")) {
				allValid = allValid
						&& validateForm(FORMS_TO_TEST + "/" + formName);
			}
		}

		assertTrue(allValid);
	}

	private boolean validateForm(String formPath) throws Exception {
		InputStream is = getContext().getAssets().open(formPath);
		Document doc = Jsoup.parse(is, null, FORMS_TO_TEST);

		Elements inputElements = doc
				.select("input:not([type=radio]),fieldset[data-role=controlgroup],select");
		List<String> ids = new ArrayList<String>(inputElements.size());
		List<Element> labels = new ArrayList<Element>(inputElements.size());
		List<String> duplicateIds = new ArrayList<String>();
		
		File outputFile = new File(TestConstants.getDebugOutputDirectory(getContext()), "form_elements.csv" );
		CSVWriter csvWriter = new CSVWriter(new BufferedWriter(new FileWriter(outputFile)));
		csvWriter.writeNext(DEBUG_FILE_COLUMNS);

		Elements labelsForElement;
		String[] row = new String[3];
		String[] subRow = new String[6];
		for (Element e : inputElements) {
			Arrays.fill(row, "");
			Arrays.fill(subRow, "");
			
			if (ids.contains(e.id())) {
				duplicateIds.add(e.id());
			}
			ids.add(e.id());

			labelsForElement = getLabelForInput(e);
			if (labelsForElement.size() == 0) {
				labels.add(null);
			} else {
				labels.add(labelsForElement.get(0));
			}
			
			if (e.tagName().equalsIgnoreCase("input")) {
				
			} else if (e.tagName().equalsIgnoreCase("fieldset") && e.attr("data-role").equalsIgnoreCase("controlgroup")) {
				
			} else if (e.tagName().equalsIgnoreCase("select")) {
				
			}
		}

		String id;
		Element label;
		StringBuffer sb = new StringBuffer("Http form elements for \""
				+ formPath + "\":\n");
		
		for (int i = 0; i < inputElements.size(); i++) {
			id = ids.get(i);
			label = labels.get(i);

			sb.append("\t\"" + id + "\":");
			sb.append("\ttype: " + inputElements.get(i).attr("type"));
			sb.append("\tlabel: " + (label != null ? label.text() : "????"));
			sb.append("\n");

		}
		Log.v("ValidateHtmlForms", sb.toString());
		
		return duplicateIds.isEmpty();
	}

	private Elements getLabelForInput(Element e) {
		Elements siblings = e.siblingElements();
		Elements labels = e.ownerDocument().select("label[for=" + e.id() + "]");
		if (labels.size() == 0 && siblings.size() > 0) {
			Element elementToTest;
			String tagToTest;

			// iterate backwards to next relevant node. If it's a label, store
			// the label. If not, then store nothing. Skip unimportant elements
			// like <br/>.
			for (int i = e.elementSiblingIndex() - 1; i >= 0; i--) {
				elementToTest = siblings.get(i);
				tagToTest = elementToTest.tagName();

				if (tagToTest.equalsIgnoreCase("br")) {
					// ignore these
					continue;
				} else if (tagToTest.equalsIgnoreCase("label")) {
					labels.add(elementToTest);
				} else {
					break;
				}
			}
		}

		// OK, lets see if it's in a table and look THERE. This only looks in
		// the previous row or the previous column for a lone label. The label
		// should really just use the "for" attribute!
		if (labels.size() == 0) {
			String parentTag = e.parent().tagName();
			if (parentTag.equalsIgnoreCase("td") ) {
				Element previousRow = e.parent().previousElementSibling();
				if (previousRow != null && previousRow.tagName().equalsIgnoreCase("td") && previousRow.children().size() > 0) {
					Element previousRowChild = previousRow.child(0);
					if (previousRowChild.tagName().equalsIgnoreCase("label")) {
						labels.add(previousRowChild);
					}
				}
			}
		}
		
		return labels;
	}
}
