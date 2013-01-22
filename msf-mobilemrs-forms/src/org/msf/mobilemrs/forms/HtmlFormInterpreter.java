/**
 * 
 */
package org.msf.mobilemrs.forms;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.NodeVisitor;

/**
 * @author Nicholas Wilkie
 * 
 */
public class HtmlFormInterpreter {
	HtmlFormContext context;
	Stage[] stages;
	Map<Stage, List<Template>> templatesByStage;

	public HtmlFormInterpreter(InputStream in) throws IOException {
		read(in);

		stages = Stage.values();
		Arrays.sort(stages, new Comparator<Stage>() {
			@Override
			public int compare(Stage lhs, Stage rhs) {
				return ((Integer) lhs.getIndex()).compareTo(rhs.getIndex());
			}
		});

		templatesByStage = new HashMap<Stage, List<Template>>();
		for (Stage s : stages) {
			templatesByStage.put(s, new ArrayList<Template>());
		}
	}

	private void read(InputStream in) throws IOException {
		Document doc = Jsoup.parse(in, null, "");
		context = new HtmlFormContext(doc);
	}

	public void applyTemplates() {
		for (Stage s : stages) {
			List<Template> templates = templatesByStage.get(s);
			context.getDocument().traverse(new ApplyTemplateTraverser(templates));
		}
	}

	public class ApplyTemplateTraverser implements NodeVisitor {
		List<Template> templates;
		
		public ApplyTemplateTraverser(List<Template> templates) {
			ApplyTemplateTraverser.this.templates = templates;
		}
		
		@Override
		public void head(Node node, int depth) {

		}

		@Override
		public void tail(Node node, int depth) {
			if (node instanceof Element) {
				Element element = (Element) node;
				for (Template t : templates) {
					if (t.matchesTemplate(element)) {
						t.applyTemplate(element, context);
					}
				}
			}
		}
	}
}
