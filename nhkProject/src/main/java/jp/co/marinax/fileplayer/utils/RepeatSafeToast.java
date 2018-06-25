package jp.co.marinax.fileplayer.utils;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * This class can help shows a Toast and prevents showing Toast continuously
 * within a specific duration
 * 
 * @author SONBX
 * 
 */

public class RepeatSafeToast {
	private static final int DURATION = 2000;

	private static final Map<Object, Long> lastShown = new HashMap<Object, Long>();

	private static boolean isRecent(Object obj) {
		Long last = lastShown.get(obj);
		if (last == null) {
			return false;
		}
		long now = System.currentTimeMillis();
		if (last + DURATION < now) {
			return false;
		}
		return true;
	}

	/**
	 * Show a Toast
	 * 
	 * @param context
	 * @param resId
	 * @param type
	 *            if type = 1 then show a short toast
	 */
	public static synchronized void show(Context context, int resId, int type) {
		if (isRecent(resId)) {
			return;
		}
		if (type == 1) {
			Toast toast = Toast.makeText(context, resId, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.BOTTOM, 0, 300);
			toast.show();
		} else {
			Toast toast = Toast.makeText(context, resId, Toast.LENGTH_LONG);
			toast.setGravity(Gravity.BOTTOM, 0, 0);
			toast.show();
		}
		lastShown.put(resId, System.currentTimeMillis());
	}

	/**
	 * Show a Toast
	 * 
	 * @param context
	 * @param msg
	 * @param type
	 *            if type = 1 then show a short toast
	 */
	public static synchronized void show(Context context, String msg, int type) {
		if (isRecent(msg)) {
			return;
		}
		if (type == 1) {
			Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.BOTTOM, 0, 200);
			toast.show();
		} else {
			Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
			toast.setGravity(Gravity.BOTTOM, 0, 200);
			toast.show();
		}
		lastShown.put(msg, System.currentTimeMillis());
	}
}
