package jp.co.marinax.fileplayer.utils;

import jp.co.marinax.fileplayer.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class ShowDialogUtils {
	public static void showDialog(Context context, String mesage) {
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
		alertBuilder.setTitle(mesage);
		alertBuilder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
		alertBuilder.create().show();
	}
}
