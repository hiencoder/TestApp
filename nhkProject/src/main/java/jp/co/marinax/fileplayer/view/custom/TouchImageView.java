package jp.co.marinax.fileplayer.view.custom;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

public class TouchImageView extends ImageView {

	private Matrix mMatrix;

	// We can be in one of these 3 states
	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	private static final int CLICK = 3;
	private int mMode = NONE;

	// Remember some things for zooming
	private PointF mLast = new PointF();
	private PointF mStart = new PointF();
	private float mMinScale = 1f;
	private float mMaxScale = 3f;
	private float[] m;

	private int mViewWidth, mViewHeight;
	private float mSaveScale = 1f;
	protected float mOrigWidth, mOrigHeight;
	private int mOldMeasuredWidth, mOldMeasuredHeight;

	private ScaleGestureDetector mScaleDetector;
	private DragEventListener mDragEventListener;
	private Context mContext;

	public TouchImageView(Context context, GestureDetector detector) {
		super(context);
		sharedConstructing(context, detector);
	}

	public TouchImageView(Context context, GestureDetector detector, AttributeSet attrs) {
		super(context, attrs);
		sharedConstructing(context, detector);
	}

	public TouchImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void sharedConstructing(Context context, final GestureDetector detector) {
		super.setClickable(true);
		try {
			this.mContext = context;
			mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
			mMatrix = new Matrix();
			m = new float[9];
			setImageMatrix(mMatrix);
			setScaleType(ScaleType.MATRIX);
			setDragEventListener(new DragEventListener() {

				@Override
				public void onDragToBorder() {
					// do nothing
				}

				@Override
				public void onZoom() {
					// do nothing
				}
			});
			setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					mScaleDetector.onTouchEvent(event);
					if (detector != null) {
						detector.onTouchEvent(event);
					}

					PointF curr = new PointF(event.getX(), event.getY());

					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						mLast.set(curr);
						mStart.set(mLast);
						mMode = DRAG;
						if (mDragEventListener != null) {
							if (mSaveScale == 1.0)
								mDragEventListener.onDragToBorder();
							else
								mDragEventListener.onZoom();
						}
						break;

					case MotionEvent.ACTION_MOVE:
						if (mMode == DRAG) {
							float deltaX = curr.x - mLast.x;
							float deltaY = curr.y - mLast.y;
							float fixTransX = getFixDragTrans(deltaX, mViewWidth, mOrigWidth
									* mSaveScale);
							// ALog.d("Touchimageview", "fixtranX: "+fixTransX);
							float fixTransY = getFixDragTrans(deltaY, mViewHeight, mOrigHeight
									* mSaveScale);
							// ALog.d("Touchimageview", "fixtranY: "+fixTransY);
							mMatrix.postTranslate(fixTransX, fixTransY);
							fixTrans();
							mLast.set(curr.x, curr.y);

							// if drag to border of image, enable viewpager to
							// next
							// or prev image
							float[] values = new float[9];
							mMatrix.getValues(values);
							float translateX = values[2];
							// float translateY = values[5];

							float scaledWidth = Math.round(mOrigWidth * mSaveScale);
							if (Math.abs(mViewWidth - translateX - scaledWidth) <= 1.0) {
								// drag to right border
								// ALog.d("touchimageview", "right x: "
								// + (mViewWidth - translateX - scaledWidth));
								mDragEventListener.onDragToBorder();
								break;
							}

							if (Math.abs(translateX) <= 1.0) {
								// drag to left border
								// ALog.d("touchimageview", "left x: " +
								// translateX);
								mDragEventListener.onDragToBorder();
								break;
							}

						}
						break;

					case MotionEvent.ACTION_UP:
						mMode = NONE;
						int xDiff = (int) Math.abs(curr.x - mStart.x);
						int yDiff = (int) Math.abs(curr.y - mStart.y);
						if (xDiff < CLICK && yDiff < CLICK) performClick();

					case MotionEvent.ACTION_POINTER_UP:
						mMode = NONE;
						break;
					}

					setImageMatrix(mMatrix);
					invalidate();
					return true; // indicate event was handled
				}

			});
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void setMaxZoom(float x) {
		mMaxScale = x;
	}

	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			mMode = ZOOM;
			mDragEventListener.onZoom();
			return true;
		}

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			float mScaleFactor = detector.getScaleFactor();
			float origScale = mSaveScale;
			mSaveScale *= mScaleFactor;
			if (mSaveScale > mMaxScale) {
				mSaveScale = mMaxScale;
				mScaleFactor = mMaxScale / origScale;
			} else if (mSaveScale < mMinScale) {
				mSaveScale = mMinScale;
				mScaleFactor = mMinScale / origScale;
			}

			if (mOrigWidth * mSaveScale <= mViewWidth || mOrigHeight * mSaveScale <= mViewHeight) {
				mMatrix.postScale(mScaleFactor, mScaleFactor, mViewWidth / 2, mViewHeight / 2);
			} else {
				mMatrix.postScale(mScaleFactor, mScaleFactor, detector.getFocusX(),
						detector.getFocusY());
			}

			fixTrans();
			return true;
		}
	}

	public void zoomFromCenter1(int scale) {
		if (scale == 1) return;
		Matrix imageMatrix = getImageMatrix();
		// RectF drawableRect = new RectF(0, 0, mOrigWidth, mOrigHeight);
		// RectF viewRect = new RectF(0, 0, getWidth(), getHeight());
		//
		// imageMatrix.setRectToRect(drawableRect, viewRect,
		// Matrix.ScaleToFit.CENTER);

		imageMatrix.postScale(scale, scale, 0, 0);

		mMatrix = imageMatrix;
		setImageMatrix(mMatrix);

		// fitAndCenterView();

		invalidate();
	}

	void fixTrans() {
		mMatrix.getValues(m);
		float transX = m[Matrix.MTRANS_X];
		float transY = m[Matrix.MTRANS_Y];

		float fixTransX = getFixTrans(transX, mViewWidth, mOrigWidth * mSaveScale);
		float fixTransY = getFixTrans(transY, mViewHeight, mOrigHeight * mSaveScale);

		if (fixTransX != 0 || fixTransY != 0) mMatrix.postTranslate(fixTransX, fixTransY);
	}

	float getFixTrans(float trans, float viewSize, float contentSize) {
		float minTrans, maxTrans;

		if (contentSize <= viewSize) {
			minTrans = 0;
			maxTrans = viewSize - contentSize;
		} else {
			minTrans = viewSize - contentSize;
			maxTrans = 0;
		}

		if (trans < minTrans) return -trans + minTrans;
		if (trans > maxTrans) return -trans + maxTrans;
		return 0;
	}

	float getFixDragTrans(float delta, float viewSize, float contentSize) {
		if (contentSize <= viewSize) {
			return 0;
		}
		return delta;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mViewWidth = MeasureSpec.getSize(widthMeasureSpec);
		mViewHeight = MeasureSpec.getSize(heightMeasureSpec);

		//
		// Rescales image on rotation
		//
		if (mOldMeasuredHeight == mViewWidth && mOldMeasuredHeight == mViewHeight
				|| mViewWidth == 0 || mViewHeight == 0) return;
		mOldMeasuredHeight = mViewHeight;
		mOldMeasuredWidth = mViewWidth;

		if (mSaveScale == 1) {
			// Fit to screen.
			float scale;

			Drawable drawable = getDrawable();
			if (drawable == null || drawable.getIntrinsicWidth() == 0
					|| drawable.getIntrinsicHeight() == 0) return;
			int bmWidth = drawable.getIntrinsicWidth();
			int bmHeight = drawable.getIntrinsicHeight();

			float scaleX = (float) mViewWidth / (float) bmWidth;
			float scaleY = (float) mViewHeight / (float) bmHeight;
			scale = Math.min(scaleX, scaleY);
			mMatrix.setScale(scale, scale);

			// Center the image
			float redundantYSpace = (float) mViewHeight - (scale * (float) bmHeight);
			float redundantXSpace = (float) mViewWidth - (scale * (float) bmWidth);
			redundantYSpace /= (float) 2;
			redundantXSpace /= (float) 2;

			mMatrix.postTranslate(redundantXSpace, redundantYSpace);

			mOrigWidth = mViewWidth - 2 * redundantXSpace;
			mOrigHeight = mViewHeight - 2 * redundantYSpace;
			setImageMatrix(mMatrix);
		}
		fixTrans();
	}

	public void resetZoom() {
		// if (mSaveScale == 1) return;
		// ALog.d("touchimageview", "resetzoom");
		// Matrix imageMatrix = getImageMatrix();
		// RectF drawableRect = new RectF(0, 0, mOrigWidth, mOrigHeight);
		// RectF viewRect = new RectF(0, 0, getWidth(), getHeight());
		//
		// imageMatrix.setRectToRect(drawableRect, viewRect,
		// Matrix.ScaleToFit.CENTER);
		//
		// mMatrix = imageMatrix;
		// setImageMatrix(mMatrix);
		//
		// // fitAndCenterView();
		//
		// invalidate();
	}

	public DragEventListener getDragEventListener() {
		return mDragEventListener;
	}

	public void setDragEventListener(DragEventListener dragEventListener) {
		this.mDragEventListener = dragEventListener;
	}

	public interface DragEventListener {
		void onZoom();

		void onDragToBorder();
	}
}
