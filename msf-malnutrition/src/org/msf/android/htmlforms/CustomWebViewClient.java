/**
 * 
 */
package org.msf.android.htmlforms;

import android.app.ProgressDialog;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * @author Nicholas Wilkie
 *
 */
public class CustomWebViewClient extends WebViewClient {
	private ProgressDialog progressDialog;

	@Inject
	public CustomWebViewClient() {
	}
	
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		view.loadUrl(url);
		return true;
	}
	
	@Override
	public void onPageFinished(WebView view, String url) {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}
	
	
	/**
	 * @return the progressDialog
	 */
	public ProgressDialog getProgressDialog() {
		return progressDialog;
	}

	/**
	 * @param progressDialog the progressDialog to set
	 */
	public void setProgressDialog(ProgressDialog progressDialog) {
		this.progressDialog = progressDialog;
	}
}
