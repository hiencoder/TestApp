package jp.co.marinax.fileplayer.io.save;

import jp.co.marinax.fileplayer.app.config.Define;
import jp.co.marinax.fileplayer.app.config.Define.StopPositionSharedPreferencesKeys;
import jp.co.marinax.fileplayer.utils.SharedPrefUtil;
import android.content.Context;

public class SharedPreferencesData {

	/**********************************************
	 * the first use
	 **********************************************/

	public static void setFirstUse(String firstUse, Context applicationText) {
		if (applicationText == null)
			return;
		SharedPrefUtil shared = new SharedPrefUtil(applicationText,
				Define.STOP_POSITION_SHARED_PREFERENCES);
		shared.putString(Define.THE_FIRST_USE, firstUse);
	}

	public static String getFirstUse(Context applicationContext) {
		SharedPrefUtil shared = new SharedPrefUtil(applicationContext,
				Define.STOP_POSITION_SHARED_PREFERENCES);
		return shared.getString(Define.THE_FIRST_USE);
	}

	/**
	 * Save audio file's path to SharedPreferences
	 * @param path
	 * @param applicationContext
	 */
	public static void setAudioPath(String path, Context applicationContext) {
		if (applicationContext == null)
			return;
		SharedPrefUtil shared = new SharedPrefUtil(applicationContext,
				Define.STOP_POSITION_SHARED_PREFERENCES);
		shared.putString(StopPositionSharedPreferencesKeys.AUDIO_PATH, path);
	}

	/**
	 * Get audio file's path stored in SharedPreferences
	 * @param applicationContext
	 * @return
	 */
	public static String getAudioPath(Context applicationContext) {
		SharedPrefUtil shared = new SharedPrefUtil(applicationContext,
				Define.STOP_POSITION_SHARED_PREFERENCES);
		return shared.getString(StopPositionSharedPreferencesKeys.AUDIO_PATH);
	}

	/**
	 * Save Book ID to SharedPreferences
	 * 
	 * @param bookId
	 * @param applicationContext
	 */
	public static void setBookId(int bookId, Context applicationContext) {
		if (applicationContext == null)
			return;

		SharedPrefUtil shared = new SharedPrefUtil(applicationContext,
				Define.STOP_POSITION_SHARED_PREFERENCES);
		shared.putInt(StopPositionSharedPreferencesKeys.BOOK_ID, bookId);
	}

	/**
	 * Get Book Id stored in SharedPreferences
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static int getBookId(Context applicationContext) {
		SharedPrefUtil shared = new SharedPrefUtil(applicationContext,
				Define.STOP_POSITION_SHARED_PREFERENCES);
		return shared.getInt(StopPositionSharedPreferencesKeys.BOOK_ID);
	}

	/**
	 * Save audio progress to SharedPreferences
	 * 
	 * @param progress
	 * @param applicationContext
	 */
	public static void setAudioProgress(int progress, Context applicationContext) {
		if (applicationContext == null)
			return;

		SharedPrefUtil shared = new SharedPrefUtil(applicationContext,
				Define.STOP_POSITION_SHARED_PREFERENCES);
		shared.putInt(StopPositionSharedPreferencesKeys.AUDIO_PROGRESS,
				progress);
	}

	/**
	 * Get audio progress stored in SharedPreferences
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static int getAudioProgress(Context applicationContext) {
		SharedPrefUtil shared = new SharedPrefUtil(applicationContext,
				Define.STOP_POSITION_SHARED_PREFERENCES);
		return shared.getInt(StopPositionSharedPreferencesKeys.AUDIO_PROGRESS);
	}

}
