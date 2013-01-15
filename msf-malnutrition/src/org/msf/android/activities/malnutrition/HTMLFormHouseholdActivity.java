package org.msf.android.activities.malnutrition;

import org.msf.android.R;
import org.msf.android.htmlforms.malnutrition.HouseholdBridge;
import org.msf.android.managers.malnutrition.MalnutritionWorkflowManager;

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

public class HTMLFormHouseholdActivity extends Activity{
	private WebView wv;
	
	private static String URLForm = "file:///android_asset/child_malnutrition/html/formpages_household.html";
	
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
		final ProgressDialog dialog = new ProgressDialog(HTMLFormHouseholdActivity.this);
        dialog.setMessage(getString(R.string.form_loading));
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
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
			
			public void onProgressChanged(WebView view, int progress) {            
				dialog.setProgress(progress);
            }
			
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
		
		wv.addJavascriptInterface(new HouseholdBridge(new MalnutritionWorkflowManager(null)), "MSF");

		wv.loadUrl(URLForm);			
	}
	
	
	@Override	
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		
	    if (keyCode == KeyEvent.KEYCODE_MENU) {
	    	AlertDialog.Builder adb = new AlertDialog.Builder(HTMLFormHouseholdActivity.this);
            adb.setTitle("Confirmation");
	    	adb.setMessage("Are you sure you want to quit the form ? (all the data that are filled out will be unsaved)"); 
            adb.setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {  
                    public void onClick(DialogInterface dialog, int whichButton)
                        {
                    		HTMLFormHouseholdActivity.this.finish();
                        }
                }); 
            adb.setNegativeButton("No",null);
            adb.show(); 
	    }
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	if(wv.getUrl().equals(URLForm)){
	    		AlertDialog.Builder adb = new AlertDialog.Builder(HTMLFormHouseholdActivity.this);
            adb.setTitle("Confirmation");
	    	adb.setMessage("Are you sure you want to quit the form ? (all the data that are filled out will be unsaved)"); 
            adb.setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {  
                    public void onClick(DialogInterface dialog, int whichButton)
                        {
                    		HTMLFormHouseholdActivity.this.finish();
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
