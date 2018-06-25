package jp.co.marinax.fileplayer.app.check;

import jp.co.marinax.fileplayer.app.config.Define;
import jp.co.marinax.fileplayer.io.save.SharedPreferencesData;
import android.content.Context;

public class CheckFirstUse {
	public static boolean isTheFirstUse(Context context) {
		if (SharedPreferencesData.getFirstUse(context).equals(
				Define.DEFAULT_STRING)) {
			return true;
		} else {
			return false;
		}

	}
}
