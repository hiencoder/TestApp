package jp.co.marinax.fileplayer.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TimeUtils {
	public static String format = "yyyy/MM/dd HH:mm:ss";

	public static String getCurrenttime() {
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
		Calendar c = Calendar.getInstance();
		return sdf.format(c.getTime());
	}

	public static String changeTime(String time) {
		String timechange = time.substring(0, time.length() - 3);
		return timechange;
	}
}
