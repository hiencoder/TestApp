package jp.co.marinax.fileplayer.view.custom;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Custom ViewPager, can enable or disable touch event at anytime Purpose:
 * disable swipe when handle zoom gesture in touchimageview
 * 
 * @author chiennd
 * 
 */
public class MyViewPager extends ViewPager {

	private boolean mEnabled = true;

	public MyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		if (mEnabled) {
			return super.onInterceptTouchEvent(event);
		}

		return false;
	}

	public boolean isEnabled() {
		return mEnabled;
	}

	public void setEnabled(boolean enabled) {
		this.mEnabled = enabled;
	}
}