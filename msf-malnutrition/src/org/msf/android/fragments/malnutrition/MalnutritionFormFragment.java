package org.msf.android.fragments.malnutrition;

import org.msf.android.R;
import org.msf.android.htmlforms.CustomWebChromeClient;
import org.msf.android.htmlforms.CustomWebViewClient;

import roboguice.RoboGuice;
import roboguice.inject.InjectView;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class MalnutritionFormFragment extends Fragment implements
		View.OnClickListener {

	public static final boolean HIDE_NATIVE_NAVIGATION_BUTTONS = true;

	@InjectView(R.id.malnutrition_html_left_button)
	private ImageButton leftButton;
	@InjectView(R.id.malnutrition_html_right_button)
	private ImageButton rightButton;
	@InjectView(R.id.malnutrition_form_webview_container)
	private ViewGroup webViewContainer;
	private WebView webView;
	
	private String LEFT_BUTTON_TAG = "LEFT_BUTTON_TAG";
	private String RIGHT_BUTTON_TAG = "RIGHT_BUTTON_TAG";

	private String urlToInitialize;
	private Object javascriptInterfaceToInitialize;

	private ProgressDialog dialog;
	
	@Inject
	private Provider<WebViewClient> webViewClientProvider;
	@Inject
	private Provider<WebChromeClient> webChromeClientProvider;

	public MalnutritionFormFragment() {
		super();
	}

	/* Lifecycle methods */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    RoboGuice.getInjector(getActivity()).injectMembersWithoutViews(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View result = inflater.inflate(R.layout.malnutrition_form_fragment,
				null);

		if (HIDE_NATIVE_NAVIGATION_BUTTONS) {
			result.findViewById(R.id.browserButtons).setVisibility(View.GONE);
		}

//		ViewGroup browserButtons = (ViewGroup) result
//				.findViewById(R.id.browserButtons);
//		leftButton = (ImageButton) browserButtons
//				.findViewById(R.id.malnutrition_html_left_button);
//		rightButton = (ImageButton) browserButtons
//				.findViewById(R.id.malnutrition_html_right_button);
//
//		webViewContainer = (ViewGroup) result
//				.findViewById(R.id.malnutrition_form_webview_container);

		return result;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	    RoboGuice.getInjector(getActivity()).injectViewMembers(this); 

		leftButton.setTag(LEFT_BUTTON_TAG);
		rightButton.setTag(RIGHT_BUTTON_TAG);

		leftButton.setOnClickListener(this);
		rightButton.setOnClickListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();

		if (webViewContainer != null && webView != null) {
			webViewContainer.removeView(webView);
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		if (webView == null) {
			webView = new WebView(getActivity());
			webView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT));
		}

		webViewContainer.addView(webView);

		if (urlToInitialize != null) {
			initializeWebView(urlToInitialize, javascriptInterfaceToInitialize);
		}
	}

	/* Initialization */

	private void showProgressBar() {
		dialog = new ProgressDialog(getActivity());
		dialog.setMessage(getString(R.string.form_loading));
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setCancelable(false);
		dialog.show();
	}

	public void initializeWebView(String url, Object javascriptInterface) {
		if (getActivity() == null) {
			urlToInitialize = url;
			javascriptInterfaceToInitialize = javascriptInterface;
		} else {
			urlToInitialize = null;
			javascriptInterfaceToInitialize = null;

			initializeWebviewComponents(javascriptInterface);
			// getWebView().clearCache(true);
			// getWebView().clearHistory();
			getWebView().loadUrl(url);
		}
	}

	private void initializeWebviewComponents(Object javascriptInterface) {
		showProgressBar();
		// fix weird recursive-loop bug in WebView
		WebViewClient webViewClient = webViewClientProvider.get();
		if (webViewClient instanceof CustomWebViewClient) {
			((CustomWebViewClient)webViewClient).setProgressDialog(dialog);
		}
		getWebView().setWebViewClient(webViewClient);

		WebChromeClient webChromeClient = webChromeClientProvider.get();
		if (webChromeClient instanceof CustomWebChromeClient) {
			((CustomWebChromeClient)webChromeClient).setProgressDialog(dialog);
		}
		getWebView().setWebChromeClient(webChromeClient);

		// give ourselves a ton of permissions
		getWebView().getSettings().setJavaScriptEnabled(true);
		getWebView().getSettings().setGeolocationEnabled(true);
		getWebView().getSettings().setDomStorageEnabled(true);

		// fix a bug with rendering
		getWebView().setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

		if (javascriptInterface != null) {
			getWebView().addJavascriptInterface(javascriptInterface, "MSF");
		}
	}

	/* Navigation */
	@Override
	public void onClick(View v) {
		Object tag = v.getTag();

		if (tag == null) {
			return;
		}

		if (tag.equals(RIGHT_BUTTON_TAG)) {
			// Forwards
			rightButtonClicked();
		} else if (tag.equals(LEFT_BUTTON_TAG)) {
			// Backwards
			leftButtonClicked();
		}
	}

	public void rightButtonClicked() {
		next();
	}

	public void leftButtonClicked() {
		previous();
	}

	public void next() {
		getWebView().loadUrl("javascript:next()");
	}

	public void previous() {
		getWebView().loadUrl("javascript:prev()");
	}

	public WebView getWebView() {
		return webView;
	}
}
