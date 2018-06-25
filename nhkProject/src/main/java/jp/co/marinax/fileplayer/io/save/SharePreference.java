package jp.co.marinax.fileplayer.io.save;

import jp.co.marinax.fileplayer.app.config.Define;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharePreference {
	private SharedPreferences mPreferences;

	public SharePreference() {

	}

	// initialize sharePreference
	public void init(Context context) {
		if (mPreferences == null) {
			mPreferences = PreferenceManager
					.getDefaultSharedPreferences(context);
		}
	}

	public void saveString(String key, String value) {
		mPreferences.edit().putString(key, value).commit();
	}

	public String getString(String key) {
		return mPreferences.getString(key, Define.DEFAULT_STRING);
	}

	public void saveInt(String key, int value) {
		mPreferences.edit().putInt(key, value).commit();
	}

	public int getInt(String key) {
		return mPreferences.getInt(key, -1);
	}
}
