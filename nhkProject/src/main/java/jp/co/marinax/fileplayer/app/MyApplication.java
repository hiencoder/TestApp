package jp.co.marinax.fileplayer.app;

import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

public class MyApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();

		initImageLoader(this);
	}
	
	/**
	 * Create global configuration and initialize ImageLoader with this configuration
	 */
	private void initImageLoader(Context context) {
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .resetViewBeforeLoading(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(context)
                .defaultDisplayImageOptions(defaultOptions)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(50 * 1024 * 1024))
                .memoryCacheSize(50 * 1024 * 1024)
                .threadPoolSize(5)
                .memoryCache(new WeakMemoryCache());
        ImageLoaderConfiguration config = builder.build();
        ImageLoader.getInstance().init(config);
	}
}