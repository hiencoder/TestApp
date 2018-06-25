package jp.co.marinax.fileplayer.app.config;

public class Define {

	/****************************************
	 * Define for default file
	 ****************************************/
	
	public static String TEMP_FOLDER = "temp";

	
	/*************************
	 * define parameter for download
	 */
	public static String EXTENSION_TEXT = ".zip";
	public static String EXTENSION_AUDIO = ".mp3";
	public static String CONTENT_DISPOSITION = "Content-Disposition";

	/************************************
	 * encode , decode
	 ************************************/
	public static String ALGORITHM = "Blowfish";
	public static String KEY_DECODE = "hogehogehogehogehogehogehogehoge";
	public static String KEY_DECODE_SERVER = "Jfa6Pc5XvYS4dYdwVVUBfQavzZwZppPe";
	public static String UUID = "uuid";

	// BASE
	public static int DEFAULT_INT = 0;
	public static String DEFAULT_STRING = "";

	// to separated text or audio
	public static int IS_TEXT = 1;
	public static int IS_AUDIO = 2;

	/*************************************
	 * Define for DatabaseManager
	 *************************************/
	public static String DATABASE_NAME = "NhkProject";
	public static int VERSION = 1;
	// New Start 20151008 SonBX
	public static int DB_VERSION_2 = 2;
	// New End 20151008 SonBX

	/** WebView URL */
	// http://r-gogaku.jp/sp/indexNew.php
	// http://r-gogaku.jp/sp/
//	public static final String WEBVIEW_URL = "http://52.69.156.30/user_data/gogaku_top";
	// New Start 20151008 SONBX temp URL
//	public static final String WEBVIEW_URL = "http://r-gogaku.jp/sp/";
	public static final String WEBVIEW_URL = "https://dls.nhk-sc.or.jp/";
//	public static final String WEBVIEW_URL = "https://www.dropbox.com/sh/j6epvsgdbjgkgtx/AACMAmdtVRB5_umzAtJmms41a?dl=0";
//	public static final String WEBVIEW_URL = "http://www.city.yokohama.lg.jp/tsurumi/etc/publish/sounddate/h26sounddate.html";
	// New End 20151008 SONBX
	
	// New Start 20151009 SonBX
	public static final String FILE_NAME = "filename.txt";
	// New End 20151009 SonBX
	/*****************
	 * TestFlight series Id
	 ***************/
	public static String TESTFILGHT_ID = "7bc86f3d-96f5-4602-ac31-c13be8d4cb05";
	/****************************************
	 * Define for menu item
	 */
	public static final int SELECT_ALL = 1;
	public static final int NEW_FOLDER = 2;
	public static final int MOVE = 3;
	public static final int DESELECT_ALL = 4;
	public static final int DELETE = 5;
	public static final int RLDELETE = 6;
	public static final int CANCEL = 7;
	public static final int CHANGE_FILE_NAME = 8;
	/*****************************************
	 * Define for type Image
	 *****************************************/

	public static final int TYPE_FOLDER = 1;
	public static final int TYPE_BOOK = 2;
	public static final int TYPE_FILES = 3;

	/*******************************************
	 * 
	 */

	/**
	 * File type
	 * 
	 * @author SONBX
	 * 
	 */
	public static final class FileType {
		public static final int FOLDER = 3;
		public static final int AUDIO = 2;
		public static final int TEXT = 1;
	}

	public static final class SortType {
		public static final int TITLE = 1;
		public static final int DL_TIME = 2;
	}

	// New Start 20151009 SonBX
	/**
	 * Link type
	 * @author SONBX
	 *
	 */
	public static final class LinkType {
		public static final String TEXT_LINK = "text.php";
		public static final String AUDIO_LINK = "audio.php";
//		public static final String SOUND_LINK = "sounds.php";
//		public static final String SOUND_LINK = "dl.dropboxusercontent.com";
	}
	// New End 20151009 SonBX
	
	// New Start 20151221 SonBX
	public static final class FileTypeDownload {
		public static final String ZIP_TEXT = "textzip";
		public static final String ZIP_MP3 = "mp3zip";
		public static final String MP3 = "mp3";
	}
	// New End 20151221 SonBX
	
	/** Extras names */
	public static final String BOOK_ID = "book_id";
	public static final String FILE_ID = "file_id";
	public static final String FILE_TYPE = "file_type";
	public static final String IS_SEARCH = "is_search";
	public static final String HAS_STOP_POSITION = "has_stop_position";
	public static final String SORT_TYPE = "sort_type";
	public static final String LESSON_NAME = "lesson_name";

	/** REQUEST CODES */
	public static final int REQUEST_CODE_SELECT_FILE = 1;
	public static final int REQUEST_CODE_FOLDER_ACTION = 2;
	public static final int REQUEST_CODE_BROWSING = 3;
	public static final int REQUEST_CODE_SORT = 4;
	public static final int REQUEST_CODE_FILE_LIST = 5;
	/** SharedPreferences */
	public static final String STOP_POSITION_SHARED_PREFERENCES = "stop_position_shared_prefrences";

	/**
	 * StopPositionSharedPreferences's Keys
	 * 
	 * @author SONBX
	 * 
	 */
	public static final class StopPositionSharedPreferencesKeys {
		/** Audio file's path */
		public static final String AUDIO_PATH = "audio_path";

		/** Book ID */
		public static final String BOOK_ID = "book_id";

		/** Audio progress */
		public static final String AUDIO_PROGRESS = "audio_progress";

		/*********************
		 * Define move Activity
		 */

	}

	public static int MOVE_FLAG = 1000;

	// the first use
	public static final String THE_FIRST_USE = "the_first_use";

	public static final int NEW_TOP_ACTIVITY = 100;
	public static final int NEW_OTHER_ACTIVITY = 2;

	/*********************************************
	 * BookMark First
	 *********************************************/
	public static String BOOK_MARK_FIRST = "book_mark_first";
	
	// New Start 20160112 SonBX
	public static String BOOK_MARK_SECOND = "book_mark_second";
	// New End 20160112 SonBX
	
	public static String WEB_BOOKMARK_URL = "webview_url";

}