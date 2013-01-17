/**
 * 
 */
package org.msf.android.htmlforms;

import android.app.ProgressDialog;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.google.inject.Inject;

/**
 * @author Nicholas Wilkie
 *
 */
public class CustomWebChromeClient extends WebChromeClient {

	private ProgressDialog progressDialog;
	
	@Inject
	public CustomWebChromeClient() {
	}
	
	public void onProgressChanged(WebView view, int progress) {
		if (progressDialog != null) {
			progressDialog.setProgress(progress);
		}
	}

	public boolean onConsoleMessage(ConsoleMessage cm) {
		Log.d("MyApplication",
				cm.message() + " -- From line " + cm.lineNumber()
						+ " of " + cm.sourceId());
		return true;
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
