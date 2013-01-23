/**
 * 
 */
package org.msf.android.htmlforms;

import java.io.File;
import java.io.FileOutputStream;

import org.msf.android.activities.malnutrition.MalnutritionWorkflowActivity;
import org.msf.android.app.MSFClinicApp;
import org.msf.android.fragments.malnutrition.MalnutritionFormFragment;
import org.msf.android.managers.malnutrition.MalnutritionWorkflowManager;

import roboguice.RoboGuice;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.os.Environment;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Stage;

/**
 * @author Nicholas Wilkie
 * 
 */
public class ImageHtmlForms extends
		ActivityInstrumentationTestCase2<MalnutritionWorkflowActivity> {

	public static final int WAIT_BEFORE_CAPTURE_PICTURE = 3000;
	public static final int TIMEOUT_FOR_MAIN_THREAD_WAIT = 1500000;
	
	public static final String MALNUTRITION_HOUSEHOLD_LABEL = "malnutrition-household";
	public static final String MALNUTRITION_CHILD_LABEL = "malnutrition-child";
	

	MalnutritionWorkflowActivity mwActivity;
	private int page = 0;
	private boolean doneTestingForms = false;
	
	private String currentStage = "malnutrition-household";

	/**
	 * 
	 */
	public ImageHtmlForms() {
		super(MalnutritionWorkflowActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		// put in the PictureCaptureModule so that we take pictures of all the
		// pages.
		RoboGuice
				.setBaseApplicationInjector(
						MSFClinicApp.getApplication(),
						Stage.DEVELOPMENT,
						new Module[] {
								RoboGuice.newDefaultRoboModule(MSFClinicApp
										.getApplication()),
								new PictureCaptureModule(), new WorkflowHijackModule() });

		File filesDir = new File(
				Environment
						.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_PICTURES),
				"malnutrition-forms/");
		filesDir.setReadable(true, false);
		filesDir.mkdirs();
		for (File f : filesDir.listFiles()) {
			f.delete();
		}
	}

	public void testIterateThroughPages() throws Exception {
		mwActivity = getActivity();
		MalnutritionFormFragment frag = (MalnutritionFormFragment) mwActivity
				.getSupportFragmentManager()
				.findFragmentByTag(
						MalnutritionWorkflowManager.TAG_HOUSEHOLD_ENTRY_FRAGMENT);

		long time = System.currentTimeMillis();
		while (!doneTestingForms && System.currentTimeMillis() - time <= TIMEOUT_FOR_MAIN_THREAD_WAIT) {
			Thread.sleep(1000);
		}
	}

	// Override the FormModule with this
	public class PictureCaptureModule extends AbstractModule {

		@Override
		protected void configure() {
		}

		@Provides
		public WebChromeClient provideWebChromeClient() {
			WebChromeClient result = new WebChromeClient() {
				@Override
				public void onProgressChanged(final WebView view,
						int newProgress) {
					super.onProgressChanged(view, newProgress);

					// if (newProgress == 100) {
					// view.postDelayed(new Runnable() {
					//
					// @Override
					// public void run() {
					// takePicture(view);
					// }
					// }, WAIT_BEFORE_CAPTURE_PICTURE);
					// }
				}
			};

			return result;
		}

		@Provides
		public WebViewClient provideWebViewClient() {
			CustomWebViewClient result = new CustomWebViewClient() {
				/*
				 * (non-Javadoc)
				 * 
				 * @see
				 * org.msf.android.htmlforms.CustomWebViewClient#onPageFinished
				 * (android.webkit.WebView, java.lang.String)
				 */
				@Override
				public void onPageFinished(final WebView view, String url) {
					super.onPageFinished(view, url);
					
					fillValues(view);
					view.postDelayed(new Runnable() {

						@Override
						public void run() {
							if (view.getHeight() > 0) {
								takePicture(view);
							}
						}
					}, WAIT_BEFORE_CAPTURE_PICTURE);
				}
			};
			return result;
		}
		
		public void fillValues(WebView view) {
			if (currentStage.equals(MALNUTRITION_HOUSEHOLD_LABEL)) {
				assignStringValue(view, "suividat", "01/23/2013");
				assignStringValue(view, "zone", "West Marin");
				assignStringValue(view, "enqnom", "02");
				assignStringValue(view, "nozone", "04");
				assignStringValue(view, "menagn", "1234");
				assignStringValue(view, "villagen", "Mill Valley");
				assignStringValue(view, "novillage", "23");
				assignStringValue(view, "nenfmen", "2");
				assignStringValue(view, "mennom", "Robert Wilkie");
			} else if (currentStage.equals(MALNUTRITION_CHILD_LABEL)) {
				assignStringValue(view, "enfantid", "1");
				assignStringValue(view, "enfnom", "Wilkie");
				assignStringValue(view, "enfprenom", "Nicholas");
				assignStringValue(view, "lbl_enfage", "20");
				assignStringValue(view, "enfsex", "male");
			}
		}
		
		public void assignStringValue(WebView view, String id, String value) {
			view.loadUrl("javascript:$(\"#" + id +"\").val('"+ value + "');");
		}
		
		public void assignSelectValue(WebView view, String selectId, String idOfSelectedOption) {
			view.loadUrl("javascript:{var e = $(\"#" + selectId +"\");" +
					"e.val('" + idOfSelectedOption + "').attr('selected', true).siblings('option').removeAttr('selected');" +
							"e.selectmenu(\"refresh\", true);");
		}

		public void takePicture(WebView view) {

			Picture picture = view.capturePicture();

			Bitmap b = Bitmap.createBitmap(picture.getWidth(),
					picture.getHeight(), Bitmap.Config.ARGB_8888);
			Canvas c = new Canvas(b);

			picture.draw(c);
			FileOutputStream fos = null;
			try {

				File filesDir = new File(
						Environment
								.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_PICTURES),
						"malnutrition-forms/");
				File outputFile = new File(filesDir, currentStage + ++page + ".jpg");
				fos = new FileOutputStream(outputFile);
				if (fos != null) {
					b.compress(Bitmap.CompressFormat.JPEG, 100, fos);

					fos.close();
					outputFile.setReadable(true, false);
				}

			} catch (Exception e) {
				Log.e("PictureCaptureModule", "Could not save image of page", e);
			}

			// and go to the next page
			view.loadUrl("javascript:forceNextPage()");

		}
	}

	public class WorkflowHijackModule extends AbstractModule {

		@Override
		protected void configure() {
		}

		@Provides
		public MalnutritionWorkflowManager provideMalnutritionWorkflowManager() {
			MalnutritionWorkflowManager result = new MalnutritionWorkflowManager() {
				@Override
				public void startHouseholdForm() {
					currentStage = MALNUTRITION_HOUSEHOLD_LABEL;
					page = 0;
					
					super.startHouseholdForm();
				}
				
				@Override
				public void finishHouseholdForm() {
					super.finishHouseholdForm();
					
					startNewChildForm();
				}
				
				@Override
				public void startNewChildForm() {
					currentStage = MALNUTRITION_CHILD_LABEL;
					page = 0;
					
					super.startNewChildForm();
				}

				@Override
				public void finishNewChildForm() {
					super.finishNewChildForm();

					//startReview();
					//finishReview();
					
					//Signal to listener thread that we're ready to be done
					//doneTestingForms = true;
				}
			};
			return result;
		}
	}
}
