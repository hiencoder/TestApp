package jp.co.marinax.fileplayer.view.adapter;

import java.util.ArrayList;

import jp.co.marinax.fileplayer.utils.BitmapUtils;
import jp.co.marinax.fileplayer.view.custom.MyViewPager;
import jp.co.marinax.fileplayer.view.custom.TouchImageViewEx;
import jp.co.marinax.fileplayer.view.custom.TouchImageView.DragEventListener;
import jp.co.marinax.fileplayer.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class DisplayActivityAdapter extends PagerAdapter {

	private ArrayList<String> mArrayList = new ArrayList<String>();
	private Context mContext;
	private Activity mActivity;
	public static int countAsync = 0;
	
	public MyViewPager viewPager;

	@SuppressLint("NewApi")
	@Override
	public Object instantiateItem(ViewGroup container, int position) {

		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.entity_paging, null);
		TouchImageViewEx imgv = (TouchImageViewEx) view
				.findViewById(R.id.imgvContent);
		imgv.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);
		imgv.setDragEventListener(new DragEventListener() {

			@Override
			public void onDragToBorder() {
				viewPager.setEnabled(true);
			}

			@Override
			public void onZoom() {
				viewPager.setEnabled(false);
			}
		});
		viewPager.setEnabled(true);

		String filePath = mArrayList.get(position);
		if (!filePath.startsWith("file://")) {
			filePath = "file://" + filePath;
		}
		ImageLoadingListener imgListerner = new SimpleImageLoadingListener() {
			
			@Override
			public void onLoadingStarted(String arg0, View arg1) {
				
			}
			
			@Override
			public void onLoadingFailed(String path, View imv, FailReason arg2) {
				if (arg2.getType() == FailReason.FailType.OUT_OF_MEMORY) {
					if (imv instanceof TouchImageViewEx) {
						ImageLoader.getInstance().displayImage(path, (TouchImageViewEx)imv, this);
					}
				}
			}
			
			@Override
			public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
				
			}
			
			@Override
			public void onLoadingCancelled(String arg0, View arg1) {
				
			}
		};
		
		ImageLoader.getInstance().displayImage(filePath, imgv, imgListerner);
		// Log.e("GetView", "Position: " + position + ", File: " + mArrayList.get(position));
		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
		// new DisplayImageTask(mArrayList.get(position), imgv, mActivity)
		// .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		// } else {
		// new DisplayImageTask(mArrayList.get(position), imgv, mActivity)
		// .execute();
		// }

		imgv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				((TabText) mContext).tabOnclick();

			}
		});

		container.addView(view);
		return view;
	}
	
	public DisplayActivityAdapter(Context context, Activity activity,
			ArrayList<String> arr, MyViewPager myViewPager) {
		mContext = context;
		mActivity = activity;
		mArrayList = arr;
		this.viewPager = myViewPager;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		View view = (View) object;
	    ImageView imgView = (ImageView) view.findViewById(R.id.imgvContent);
	    BitmapDrawable bmpDrawable = (BitmapDrawable) imgView.getDrawable();
	    if (bmpDrawable != null && bmpDrawable.getBitmap() != null) {
	            // This is the important part
	            bmpDrawable.getBitmap().recycle();
	    }
	    view = null;
		container.removeView((View) object);
	}

	@Override
	public int getCount() {
		return mArrayList.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return (view == object);
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	public interface TabText {
		public void tabOnclick();
	}
	
	private class DisplayImageTask extends AsyncTask<String, Void, Bitmap> {
		private String mUrl;
		private ImageView mIv;
		private Activity mActivity;

		public DisplayImageTask(String url, ImageView iv, Activity activity) {
			mUrl = url;
			mIv = iv;
			mActivity = activity;
			countAsync++;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			DisplayMetrics displaymetrics = new DisplayMetrics();
			mActivity.getWindowManager().getDefaultDisplay()
					.getMetrics(displaymetrics);
			int windowwidth = displaymetrics.widthPixels;
			System.out.println("Screen width: " + windowwidth);
			return BitmapUtils.decodeBitmapWithScale(mUrl, 720);
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			mIv.setImageBitmap(result);
			super.onPostExecute(result);
			countAsync--;
		}
	}

}
