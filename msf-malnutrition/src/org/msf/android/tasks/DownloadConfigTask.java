package org.msf.android.tasks;

import java.net.URISyntaxException;
import java.sql.SQLException;

import org.msf.android.network.CheckNetworkAvailable;
import org.msf.android.rest.OpenMRSSyncTools;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

public class DownloadConfigTask extends AsyncTask<Object, Void, Void> {

	private ProgressDialog pd;
	private Activity activity;
	String resultMessage = "No network connection availaible. Can't download configuration. Check your settings !";

	public DownloadConfigTask(Activity activity){
		this.activity = activity;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		pd = ProgressDialog
				.show(activity,
						"Downloading config",
						"Loading configuration on the mobile phone. This may take few minutes...",
						true);
	}

	@Override
	protected Void doInBackground(Object... params) {
		try {
			if(new CheckNetworkAvailable(activity).isOnline()){
				downloadConfig();
				resultMessage = "Configuration downloaded successfully !";
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		}

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		pd.dismiss();
		Toast t = Toast.makeText(activity,
				resultMessage,
				Toast.LENGTH_SHORT);
		t.show();
	}
	
	public void downloadConfig() {

		try {
			OpenMRSSyncTools sync = new OpenMRSSyncTools();

			sync.syncMetaData();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		}
}


