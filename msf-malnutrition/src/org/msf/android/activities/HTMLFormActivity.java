package org.msf.android.activities;

import org.msf.android.R;
import org.msf.android.htmlforms.HtmlFormInterface;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

public class HTMLFormActivity extends Activity {
	private WebView wv;
	private static String URLForm = "file:///android_asset/cholera/html/formpages.html";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.html_form);
		wv = (WebView) findViewById(R.id.webFormView);
            
		initializeWebView();

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		wv.clearCache(true);
	}

	private void initializeWebView() {
		// fix weird recursive-loop bug in WebView
		final ProgressDialog dialog = new ProgressDialog(HTMLFormActivity.this);
        dialog.setMessage("Please wait while the form is loading...");
        dialog.show();

		wv.setWebViewClient(new WebViewClient() {
			@Override
			
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

	        public void onPageFinished(WebView view, String url) {
	        	dialog.dismiss();
	       }
		});
		
		wv.setWebChromeClient(new WebChromeClient() {
			public boolean onConsoleMessage(ConsoleMessage cm) {
			    Log.d("MyApplication", cm.message() + " -- From line "
			                         + cm.lineNumber() + " of "
			                         + cm.sourceId() );
			    return true;
			  }
		});

		// give ourselves a ton of permissions
		wv.getSettings().setJavaScriptEnabled(true);
		wv.getSettings().setGeolocationEnabled(true);
		wv.getSettings().setDomStorageEnabled(true);

		wv.clearCache(true);
		wv.clearHistory();
		
		// fix a bug with rendering
		wv.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		
		wv.addJavascriptInterface(new HtmlFormInterface(this), "MSF");

		wv.loadUrl(URLForm);			
	}
	
	
	@Override	
	public boolean onKeyUp(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_MENU) {
	    	AlertDialog.Builder adb = new AlertDialog.Builder(HTMLFormActivity.this);
            adb.setTitle("Confirmation");
	    	adb.setMessage("Are you sure you want to quit the form ? (all the data that are filled out will be unsaved)"); 
            adb.setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {  
                    public void onClick(DialogInterface dialog, int whichButton)
                        {
                    		HTMLFormActivity.this.finish();
                        }
                }); 
            adb.setNegativeButton("No",null);
            adb.show(); 
	    }
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	if(wv.getUrl().equals(URLForm)){
	    		AlertDialog.Builder adb = new AlertDialog.Builder(HTMLFormActivity.this);
            adb.setTitle("Confirmation");
	    	adb.setMessage("Are you sure you want to quit the form ? (all the data that are filled out will be unsaved)"); 
            adb.setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {  
                    public void onClick(DialogInterface dialog, int whichButton)
                        {
                    		HTMLFormActivity.this.finish();
                        }
                }); 
            adb.setNegativeButton("No",null);
            adb.show(); 
	    	}
	    	else
	    		wv.loadUrl("javascript:prev()");
	    }
	    return true;
	    
	}
}
