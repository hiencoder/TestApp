package jp.co.marinax.fileplayer.app.config;

import android.util.Log;

public class DebugOption {
	public static boolean flagDebugOption = false;

	/**
	 * purpose : propose debug for you
	 * 
	 * @param tag
	 * @param content
	 */
	public static void debug(String tag, String content) {
		if (flagDebugOption) {
			Log.d(tag, content);
		}
	}

	/**
	 * propose : information
	 * 
	 * @param tag
	 * @param content
	 */

	public static void info(String tag, String content) {
		if (flagDebugOption) {
			Log.i(tag, content);
		}
	}

	/**
	 * propose error
	 * 
	 * @param tag
	 * @param content
	 */
	public static void error(String tag, String content) {
		if (flagDebugOption) {
			Log.e(tag, content);
		}
	}

	/**
	 * propose verbose
	 * 
	 * @param tag
	 * @param content
	 */
	public static void verbose(String tag, String content) {
		if (flagDebugOption) {
			Log.v(tag, content);
		}
	}

	/**
	 * propose warning
	 * 
	 * @param tag
	 * @param content
	 */
	public static void warning(String tag, String content) {
		if (flagDebugOption) {
			Log.w(tag, content);
		}
	}
}
