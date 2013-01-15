package org.msf.android.activities;

import java.sql.SQLException;

import org.msf.android.R;
import org.msf.android.database.ClinicAdapter;
import org.msf.android.database.ClinicAdapterManager;
import org.msf.android.htmlforms.HtmlFormEncounterInterface;
import org.msf.android.htmlforms.HtmlFormInterface;
import org.msf.android.openmrs.Encounter;
import org.msf.android.openmrs.Patient;

import com.j256.ormlite.dao.Dao;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

public class HTMLFormEncounterActivity extends Activity {
	private WebView wv;
	private Patient mySelectedPatient;
	private Encounter mySelectedEncounter;
	private static String URLForm = "file:///android_asset/cholera/html/formpages_encounter.html";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.html_form);
		wv = (WebView) findViewById(R.id.webFormView);
            
		Intent i = this.getIntent();
		i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		// getting the identifier of the patient selected
		Long _id = i.getLongExtra("_id", Long.MIN_VALUE);
		mySelectedPatient = this.getPatient(_id);
		
		Long _idE = i.getLongExtra("_idE", Long.MIN_VALUE);
		mySelectedEncounter = this.getEncounter(_idE);
		
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
		final ProgressDialog dialog = new ProgressDialog(HTMLFormEncounterActivity.this);
        dialog.setMessage("Please wait while the form is loading...");
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
		
		wv.addJavascriptInterface(new HtmlFormEncounterInterface(this, mySelectedPatient, mySelectedEncounter), "MSF");

		wv.loadUrl(URLForm);			
	}
	
	
	@Override	
	public boolean onKeyUp(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_MENU) {
	    	AlertDialog.Builder adb = new AlertDialog.Builder(HTMLFormEncounterActivity.this);
            adb.setTitle("Confirmation");
	    	adb.setMessage("Are you sure you want to quit the form ? (all the data that are filled out will be unsaved)"); 
            adb.setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {  
                    public void onClick(DialogInterface dialog, int whichButton)
                        {
                    		HTMLFormEncounterActivity.this.finish();
                        }
                }); 
            adb.setNegativeButton("No",null);
            adb.show(); 
	    }
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	if(wv.getUrl().equals(URLForm)){
	    		AlertDialog.Builder adb = new AlertDialog.Builder(HTMLFormEncounterActivity.this);
            adb.setTitle("Confirmation");
	    	adb.setMessage("Are you sure you want to quit the form ? (all the data that are filled out will be unsaved)"); 
            adb.setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {  
                    public void onClick(DialogInterface dialog, int whichButton)
                        {
                    		HTMLFormEncounterActivity.this.finish();
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
	
	private Patient getPatient(Long _id) {
		Patient patient = new Patient();
		ClinicAdapter ca = null;
		try {
			ca = ClinicAdapterManager.lockAndRetrieveClinicAdapter();
			Dao<Patient, Long> pDao = ca.getPatientDao();
			patient = pDao.queryForId(_id);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			ClinicAdapterManager.unlock();
		}
		return patient;
	}
	
	private Encounter getEncounter(Long _id) {
		Encounter encounter = new Encounter();
		ClinicAdapter ca = null;
		try {
			ca = ClinicAdapterManager.lockAndRetrieveClinicAdapter();
			Dao<Encounter, Long> eDao = ca.getEncounterDao();
			encounter = eDao.queryForId(_id);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			ClinicAdapterManager.unlock();
		}
		return encounter;
	}
}
