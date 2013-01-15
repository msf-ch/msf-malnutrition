package org.msf.android.fragments.malnutrition;

import org.msf.android.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MalnutritionHomeButton extends LinearLayout implements
		View.OnTouchListener {

	private ImageView imageView;
	private TextView textView;

	private static final int[] ATTRIBUTES_TO_OBTAIN = { android.R.attr.text,
			android.R.attr.drawable, android.R.attr.layout_width };

	private static final int[] PRESSED_STATE = { android.R.attr.state_pressed };
	private static final int[] NORMAL_STATE = {};

	private static final int BACKGROUND_RES_ID = R.drawable.malnutrition_home_button_selector;

	public MalnutritionHomeButton(Context context) {
		this(context, null);
	}

	public MalnutritionHomeButton(Context context, AttributeSet attrs) {
		super(context, attrs);

		setOrientation(LinearLayout.VERTICAL);

		imageView = new ImageView(context) {
			protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
				super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			};
		};

		textView = new TextView(context) {
			@Override
			protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
				// TODO Auto-generated method stub
				super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			}
		};

		addView(imageView);
		addView(textView);
		
		textView.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		textView.setGravity(Gravity.CENTER);

		TypedArray ta = context.obtainStyledAttributes(attrs,
				ATTRIBUTES_TO_OBTAIN);

		String textFromAttr = ta.getString(0);
		if (textFromAttr != null) {
			textView.setText(textFromAttr);
			textView.setTextAppearance(context,
					R.style.MalnutritionHome_Button_Text);
		}

		Drawable drawableFromAttr = ta.getDrawable(1);
		if (drawableFromAttr != null) {
			imageView.setImageDrawable(drawableFromAttr);
		}

		setBackgroundResource(BACKGROUND_RES_ID);

		setOnTouchListener(this);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
	}

	@Override
	protected void onMeasure(int wSpec, int hSpec) {
		int ws = MeasureSpec.getMode(wSpec);
		int w = MeasureSpec.getSize(wSpec);
		int hs = MeasureSpec.getMode(hSpec);
		int h = MeasureSpec.getSize(hSpec);

		int textBottom = h - getPaddingTop() - getPaddingBottom();

		int childWidth = w - getPaddingRight() - getPaddingLeft();

		imageView.measure(
				MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY),
				MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY));
		textView.measure(
				MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY),
				MeasureSpec.makeMeasureSpec(textBottom, MeasureSpec.AT_MOST));

		int totalHeightSpec;
		if (hs == MeasureSpec.AT_MOST) {
			int totalHeight = imageView.getMeasuredHeight()
					+ textView.getMeasuredHeight() + getPaddingTop()
					+ getPaddingBottom();
			totalHeightSpec = MeasureSpec.makeMeasureSpec(totalHeight, MeasureSpec.AT_MOST);
		} else {
			totalHeightSpec = hSpec;
		}

		setMeasuredDimension(wSpec, totalHeightSpec);
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		return super.onSaveInstanceState();
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(state);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int action = event.getAction();
		if (action == MotionEvent.ACTION_DOWN) {
			getBackground().setState(PRESSED_STATE);
			invalidate();
		} else if (action == MotionEvent.ACTION_UP
				|| action == MotionEvent.ACTION_CANCEL) {
			getBackground().setState(NORMAL_STATE);
			invalidate();
		}
		return false;
	}
}
