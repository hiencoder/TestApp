package jp.co.marinax.fileplayer.view.custom;

import jp.co.marinax.fileplayer.app.config.DebugOption;
import android.content.Context;
import android.graphics.Point;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class DisplayActivityCustom extends FrameLayout implements
		ViewPager.OnPageChangeListener {
	private ViewPager mPager;
	boolean mNeedsRedraw = false;
	private Context mContext;

	public DisplayActivityCustom(Context context) {
		super(context);
		mContext = context;
		init();
	}

	public DisplayActivityCustom(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	public DisplayActivityCustom(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		init();
	}

	private void init() {
		// Disable clipping of children so non-selected pages are visible
		setClipChildren(false);
	}

	@Override
	protected void onFinishInflate() {
		try {
			mPager = (ViewPager) getChildAt(0);
			mPager.setOnPageChangeListener(this);
		} catch (Exception e) {
			throw new IllegalStateException(
					"The root child of SelectCollectionPagerCustomer must be a ViewPager");
		}
	}

	public ViewPager getViewPager() {
		return mPager;
	}

	private Point mCenter = new Point();

	// private Point mInitialTouch = new Point();

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mCenter.x = w / 2;
		mCenter.y = h / 2;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		return false;
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
		// Without this the outer pages render initially and then stay static
		if (mNeedsRedraw)
			invalidate();
	}

	@Override
	public void onPageSelected(int position) {
		// display change for each item this here
		// (mContext)TopBar.
		// if (callback != null) {
		DebugOption.info("position", position + "");
		((TopBar) mContext).setPosition(position);
		// }
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		mNeedsRedraw = (state != ViewPager.SCROLL_STATE_IDLE);
	}

	public interface TopBar {
		public void setPosition(int position);
	}

}
