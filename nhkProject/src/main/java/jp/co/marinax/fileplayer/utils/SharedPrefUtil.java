package jp.co.marinax.fileplayer.utils;

import jp.co.marinax.fileplayer.app.config.Define;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefUtil {
	/** Shared preference */
	SharedPreferences pref;

	/** Editor */
	SharedPreferences.Editor editor;

	public SharedPrefUtil(Context context, String prefName) {
		pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
	}

	public void putString(String name, String value) {
		editor = pref.edit();
		editor.putString(name, value);
		editor.commit();
	}

	public String getString(String name) {
		return pref.getString(name, Define.DEFAULT_STRING);
	}

	public void putInt(String name, int value) {
		editor = pref.edit();
		editor.putInt(name, value);
		editor.commit();
	}

	public int getInt(String name) {
		return pref.getInt(name, Define.DEFAULT_INT);
	}

	public void putBoolean(String name, boolean value) {
		editor = pref.edit();
		editor.putBoolean(name, value);
		editor.commit();
	}

	public boolean getBoolean(String name) {
		return pref.getBoolean(name, true);
	}
}
