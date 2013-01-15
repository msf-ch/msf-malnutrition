package org.msf.android.utilities;

import java.text.SimpleDateFormat;

import org.msf.android.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.preference.PreferenceManager;

public class MSFCommonUtils {
	public static final SimpleDateFormat DDMMYYYY_DATE_FORMATTER = new SimpleDateFormat(
			"dd-MM-yyyy");
	public static final SimpleDateFormat DDMMMYYYY_DATE_FORMATTER = new SimpleDateFormat(
			"dd-MMM-yyyy");
	public static final SimpleDateFormat DDMMMMMYYYY_DATE_FORMATTER = new SimpleDateFormat(
			"dd-MMMMM-yyyy");
	
	public static final boolean DEBUGGING_MODE = true;
	
	public static final String preferences_password = "Geneva9090";

	// Color matrix for drawing grayscale images
	private static final ColorMatrix colorMatrix = new ColorMatrix();
	private static final ColorMatrix grayscaleMatrix = new ColorMatrix();
	static {
		colorMatrix.setSaturation(1f);
		grayscaleMatrix.setSaturation(0f);
	}
	public static final ColorMatrixColorFilter COLOR_FILTER = new ColorMatrixColorFilter(
			colorMatrix);
	public static final ColorMatrixColorFilter GRAYSCALE_FILTER = new ColorMatrixColorFilter(
			grayscaleMatrix);
}
