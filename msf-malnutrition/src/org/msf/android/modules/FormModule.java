/**
 * 
 */
package org.msf.android.modules;

import org.msf.android.htmlforms.CustomWebChromeClient;
import org.msf.android.htmlforms.CustomWebViewClient;

import android.webkit.WebChromeClient;
import android.webkit.WebViewClient;

import com.google.inject.AbstractModule;

/**
 * @author Nicholas Wilkie
 *
 */
public class FormModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(WebViewClient.class).to(CustomWebViewClient.class);
		bind(WebChromeClient.class).to(CustomWebChromeClient.class);
	}
}
