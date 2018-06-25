package jp.co.marinax.fileplayer.utils;

import android.content.Context;
import android.widget.Toast;

public class ShowToastUtils {
	public static void showT(Context mContext, String message) {
		Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
	}
}
