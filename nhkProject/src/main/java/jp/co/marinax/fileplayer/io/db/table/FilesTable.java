package jp.co.marinax.fileplayer.io.db.table;

import java.util.ArrayList;
import java.util.Collections;

import jp.co.marinax.fileplayer.app.config.DebugOption;
import jp.co.marinax.fileplayer.app.config.Define;
import jp.co.marinax.fileplayer.io.db.table.BookTable.DtbBookKey;
import jp.co.marinax.fileplayer.io.entity.FileEntity;
import jp.co.marinax.fileplayer.utils.PathCompatator;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class FilesTable {
	public static final String DTB_FILE = "dtbFile";

	public static class DtbFileKey {
		public static String ID = "id";
		public static String NAME = "name";
		public static String URL = "url";
		public static String KEY = "key";
		public static String PATH = "path";
		public static String DOWNLOADED_TIME = "downloaded_time";
		public static String BOOK_ID = "book_id";
		public static String FOLDER_ID = "folder_id";
		public static String IS_AUDIO = "is_audio";
		public static String CREATED_DATE = "created_date";
		public static String MODIFIED_DATE = "modified_date";
		public static String DELETED_DATE = "deleted_date";
	}

	// create table AudioTable
	public static void createTable(SQLiteDatabase db) {
		String CREATE_TABLE = " CREATE TABLE " + DTB_FILE + "(" + DtbFileKey.ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," + DtbFileKey.NAME + " TEXT,"
				+ DtbFileKey.URL + " TEXT," + DtbFileKey.KEY + " TEXT," + DtbFileKey.PATH
				+ " TEXT," + DtbFileKey.DOWNLOADED_TIME + " TEXT," + DtbFileKey.BOOK_ID
				+ " INTEGER," + DtbFileKey.FOLDER_ID + " INTEGER," + DtbFileKey.IS_AUDIO
				+ " INTEGER, " + DtbFileKey.CREATED_DATE + " TEXT," + DtbFileKey.MODIFIED_DATE
				+ " TEXT," + DtbFileKey.DELETED_DATE + " TEXT" + ")";

		DebugOption.info("CREATE TABLE FILE : ", CREATE_TABLE);
		db.execSQL(CREATE_TABLE);
	}

	public static void dropTable(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS " + DTB_FILE);
	}

	public static void insert(SQLiteDatabase db, FileEntity entity) {
		ContentValues values = new ContentValues();
		values.put(DtbFileKey.NAME, entity.getName());
		values.put(DtbFileKey.URL, entity.getUrl());
		values.put(DtbFileKey.KEY, entity.getKey());
		values.put(DtbFileKey.PATH, entity.getPath());
		values.put(DtbFileKey.DOWNLOADED_TIME, entity.getDownloaded_time());
		values.put(DtbFileKey.BOOK_ID, entity.getBook_id());
		values.put(DtbFileKey.FOLDER_ID, entity.getFolder_id());
		values.put(DtbFileKey.IS_AUDIO, entity.getType());
		values.put(DtbFileKey.CREATED_DATE, entity.getCreated_time());
		values.put(DtbFileKey.MODIFIED_DATE, entity.getModified_time());
		values.put(DtbFileKey.DELETED_DATE, entity.getDeleted_time());
		try {
			db.insert(DTB_FILE, null, values);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static FileEntity getEntity(SQLiteDatabase db, int id) {
		FileEntity entity = new FileEntity();
		String sql = "SELECT * FROM " + DTB_FILE + " WHERE " + DtbFileKey.ID + " = " + id;
		Cursor cursor = db.rawQuery(sql, null);

		try {
			if (cursor.moveToFirst()) {
				entity.setId(id);
				entity.setName(cursor.getString(cursor.getColumnIndex(DtbFileKey.NAME)));
				entity.setUrl(cursor.getString(cursor.getColumnIndex(DtbFileKey.URL)));
				entity.setKey(cursor.getString(cursor.getColumnIndex(DtbFileKey.KEY)));
				entity.setPath(cursor.getString(cursor.getColumnIndex(DtbFileKey.PATH)));
				entity.setDownloaded_time(cursor.getString(cursor
						.getColumnIndex(DtbFileKey.DOWNLOADED_TIME)));
				entity.setBook_id(cursor.getInt(cursor.getColumnIndex(DtbFileKey.BOOK_ID)));
				entity.setFolder_id(cursor.getInt(cursor.getColumnIndex(DtbFileKey.FOLDER_ID)));
				entity.setType(cursor.getInt(cursor.getColumnIndex(DtbFileKey.IS_AUDIO)));
				entity.setCreated_time(cursor.getString(cursor
						.getColumnIndex(DtbFileKey.CREATED_DATE)));
				entity.setModified_time(cursor.getString(cursor
						.getColumnIndex(DtbFileKey.MODIFIED_DATE)));
				entity.setDeleted_time(cursor.getString(cursor
						.getColumnIndex(DtbFileKey.DELETED_DATE)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		cursor.close();
		return entity;
	}

	public static ArrayList<FileEntity> getAllEntity(SQLiteDatabase db) {
		ArrayList<FileEntity> list = new ArrayList<FileEntity>();
		String sql = "SELECT * FROM " + DTB_FILE;
		Cursor cursor = db.rawQuery(sql, null);

		try {
			if (cursor.moveToFirst()) {
				do {
					FileEntity entity = new FileEntity();
					entity.setId(cursor.getInt(cursor.getColumnIndex(DtbFileKey.ID)));
					entity.setName(cursor.getString(cursor.getColumnIndex(DtbFileKey.NAME)));
					entity.setUrl(cursor.getString(cursor.getColumnIndex(DtbFileKey.URL)));
					entity.setKey(cursor.getString(cursor.getColumnIndex(DtbFileKey.KEY)));
					entity.setPath(cursor.getString(cursor.getColumnIndex(DtbFileKey.PATH)));
					entity.setDownloaded_time(cursor.getString(cursor
							.getColumnIndex(DtbFileKey.DOWNLOADED_TIME)));
					entity.setBook_id(cursor.getInt(cursor.getColumnIndex(DtbFileKey.BOOK_ID)));
					entity.setFolder_id(cursor.getInt(cursor.getColumnIndex(DtbFileKey.FOLDER_ID)));
					entity.setType(cursor.getInt(cursor.getColumnIndex(DtbFileKey.IS_AUDIO)));
					entity.setCreated_time(cursor.getString(cursor
							.getColumnIndex(DtbFileKey.CREATED_DATE)));
					entity.setModified_time(cursor.getString(cursor
							.getColumnIndex(DtbFileKey.MODIFIED_DATE)));
					entity.setDeleted_time(cursor.getString(cursor
							.getColumnIndex(DtbFileKey.DELETED_DATE)));
					list.add(entity);

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		cursor.close();
		return list;
	}


	/**
	 * Get list of files by Type
	 * 
	 * @param db
	 * @param fileType
	 * @return
	 */
	public static ArrayList<FileEntity> getFilesByType(SQLiteDatabase db, int fileType) {
		ArrayList<FileEntity> list = new ArrayList<FileEntity>();
		String sql = "SELECT * FROM " + DTB_FILE + " WHERE " + DtbFileKey.IS_AUDIO + " = "
				+ fileType;
		Cursor cursor = db.rawQuery(sql, null);

		try {
			if (cursor.moveToFirst()) {
				do {
					FileEntity entity = new FileEntity();
					entity.setId(cursor.getInt(cursor.getColumnIndex(DtbFileKey.ID)));
					entity.setName(cursor.getString(cursor.getColumnIndex(DtbFileKey.NAME)));
					entity.setUrl(cursor.getString(cursor.getColumnIndex(DtbFileKey.URL)));
					entity.setKey(cursor.getString(cursor.getColumnIndex(DtbFileKey.KEY)));
					entity.setPath(cursor.getString(cursor.getColumnIndex(DtbFileKey.PATH)));
					entity.setDownloaded_time(cursor.getString(cursor
							.getColumnIndex(DtbFileKey.DOWNLOADED_TIME)));
					entity.setBook_id(cursor.getInt(cursor.getColumnIndex(DtbFileKey.BOOK_ID)));
					entity.setFolder_id(cursor.getInt(cursor.getColumnIndex(DtbFileKey.FOLDER_ID)));
					entity.setType(cursor.getInt(cursor.getColumnIndex(DtbFileKey.IS_AUDIO)));
					entity.setCreated_time(cursor.getString(cursor
							.getColumnIndex(DtbFileKey.CREATED_DATE)));
					entity.setModified_time(cursor.getString(cursor
							.getColumnIndex(DtbFileKey.MODIFIED_DATE)));
					entity.setDeleted_time(cursor.getString(cursor
							.getColumnIndex(DtbFileKey.DELETED_DATE)));
					list.add(entity);

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		cursor.close();
		return list;
	}

	/**
	 * Get list of Books from list of text files
	 * 
	 * @param db
	 * @return
	 */

	public static int countGetBooksFromTextFiles(SQLiteDatabase db, int folderId) {
		String sql = "";
		if (folderId == -1) {
			sql = "SELECT COUNT(" + DtbFileKey.ID + ") FROM " + DTB_FILE + " WHERE "
					+ DtbFileKey.IS_AUDIO + " = " + Define.FileType.TEXT + " GROUP BY "
					+ DtbFileKey.BOOK_ID;
		} else {
			sql = "SELECT COUNT(" + DtbFileKey.ID + ") FROM " + DTB_FILE + " WHERE "
					+ DtbFileKey.IS_AUDIO + " = " + Define.FileType.TEXT + " AND "
					+ DtbFileKey.FOLDER_ID + " = " + folderId + " GROUP BY " + DtbFileKey.BOOK_ID;
		}
		DebugOption.info("sql", "sql  = " + sql);
		Cursor cursor = db.rawQuery(sql, null);
		int count = 0;
		try {
			if (cursor.moveToFirst()) {
				count = cursor.getCount();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		cursor.close();
		return count;
	}

	public static ArrayList<FileEntity> getBooksFromTextFiles(SQLiteDatabase db, int folderId) {
		ArrayList<FileEntity> list = new ArrayList<FileEntity>();
		String sql = "";
		if (folderId == -1) {
			sql = "SELECT * FROM " + DTB_FILE + " WHERE " + DtbFileKey.IS_AUDIO + " = "
					+ Define.FileType.TEXT + " GROUP BY " + DtbFileKey.BOOK_ID;
		} else {
			sql = "SELECT * FROM " + DTB_FILE + " WHERE " + DtbFileKey.IS_AUDIO + " = "
					+ Define.FileType.TEXT + " AND " + DtbFileKey.FOLDER_ID + " = " + folderId
					+ " GROUP BY " + DtbFileKey.BOOK_ID;
		}
		Cursor cursor = db.rawQuery(sql, null);

		try {
			if (cursor.moveToFirst()) {
				do {
					FileEntity entity = new FileEntity();
					entity.setId(cursor.getInt(cursor.getColumnIndex(DtbFileKey.ID)));
					entity.setName(cursor.getString(cursor.getColumnIndex(DtbFileKey.NAME)));
					entity.setUrl(cursor.getString(cursor.getColumnIndex(DtbFileKey.URL)));
					entity.setKey(cursor.getString(cursor.getColumnIndex(DtbFileKey.KEY)));
					entity.setPath(cursor.getString(cursor.getColumnIndex(DtbFileKey.PATH)));
					DebugOption.info("PATH >>>>>>>>>>>>>",
							cursor.getString(cursor.getColumnIndex(DtbFileKey.PATH)));
					entity.setDownloaded_time(cursor.getString(cursor
							.getColumnIndex(DtbFileKey.DOWNLOADED_TIME)));
					entity.setBook_id(cursor.getInt(cursor.getColumnIndex(DtbFileKey.BOOK_ID)));
					entity.setFolder_id(cursor.getInt(cursor.getColumnIndex(DtbFileKey.FOLDER_ID)));
					entity.setType(cursor.getInt(cursor.getColumnIndex(DtbFileKey.IS_AUDIO)));
					entity.setCreated_time(cursor.getString(cursor
							.getColumnIndex(DtbFileKey.CREATED_DATE)));
					entity.setModified_time(cursor.getString(cursor
							.getColumnIndex(DtbFileKey.MODIFIED_DATE)));
					entity.setDeleted_time(cursor.getString(cursor
							.getColumnIndex(DtbFileKey.DELETED_DATE)));
					// New Start 20151228 SonBX
					String displayName = BookTable.getDisplayName(db, entity.getBook_id());
					if(!displayName.equals("")) {
						entity.setName(displayName);
					}
					// New End 20151228 SonBX
					list.add(entity);

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		cursor.close();
		return list;
	}

	public static ArrayList<FileEntity> getAllTextFromBookId(SQLiteDatabase db, int bookId) {
		ArrayList<FileEntity> list = new ArrayList<FileEntity>();
		String sql = "SELECT * FROM " + DTB_FILE + " WHERE " + DtbFileKey.BOOK_ID + " = " + bookId
				+ " AND " + DtbFileKey.IS_AUDIO + " = " + Define.FileType.TEXT;
		Cursor cursor = db.rawQuery(sql, null);

		try {
			if (cursor.moveToFirst()) {
				do {
					FileEntity entity = new FileEntity();
					entity.setId(cursor.getInt(cursor.getColumnIndex(DtbFileKey.ID)));
					entity.setName(cursor.getString(cursor.getColumnIndex(DtbFileKey.NAME)));
					entity.setUrl(cursor.getString(cursor.getColumnIndex(DtbFileKey.URL)));
					entity.setKey(cursor.getString(cursor.getColumnIndex(DtbFileKey.KEY)));
					entity.setPath(cursor.getString(cursor.getColumnIndex(DtbFileKey.PATH)));
					entity.setDownloaded_time(cursor.getString(cursor
							.getColumnIndex(DtbFileKey.DOWNLOADED_TIME)));
					entity.setBook_id(cursor.getInt(cursor.getColumnIndex(DtbFileKey.BOOK_ID)));
					entity.setFolder_id(cursor.getInt(cursor.getColumnIndex(DtbFileKey.FOLDER_ID)));
					entity.setType(cursor.getInt(cursor.getColumnIndex(DtbFileKey.IS_AUDIO)));
					entity.setCreated_time(cursor.getString(cursor
							.getColumnIndex(DtbFileKey.CREATED_DATE)));
					entity.setModified_time(cursor.getString(cursor
							.getColumnIndex(DtbFileKey.MODIFIED_DATE)));
					entity.setDeleted_time(cursor.getString(cursor
							.getColumnIndex(DtbFileKey.DELETED_DATE)));
					list.add(entity);

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		cursor.close();
		return list;
	}

	/**
	 * Get list audio files by book_id
	 * 
	 * @param db
	 * @param bookId
	 * @return
	 */
	public static ArrayList<FileEntity> getAllAudiosByBookId(SQLiteDatabase db, int bookId) {
		ArrayList<FileEntity> list = new ArrayList<FileEntity>();
		String sql = "SELECT * FROM " + DTB_FILE + " WHERE " + DtbFileKey.BOOK_ID + " = " + bookId
				+ " AND " + DtbFileKey.IS_AUDIO + " = " + Define.FileType.AUDIO;
		Cursor cursor = db.rawQuery(sql, null);
		try {
			if (cursor.moveToFirst()) {
				do {
					FileEntity entity = new FileEntity();
					entity.setId(cursor.getInt(cursor.getColumnIndex(DtbFileKey.ID)));
					entity.setName(cursor.getString(cursor.getColumnIndex(DtbFileKey.NAME)));
					entity.setUrl(cursor.getString(cursor.getColumnIndex(DtbFileKey.URL)));
					entity.setKey(cursor.getString(cursor.getColumnIndex(DtbFileKey.KEY)));
					entity.setPath(cursor.getString(cursor.getColumnIndex(DtbFileKey.PATH)));
					entity.setDownloaded_time(cursor.getString(cursor
							.getColumnIndex(DtbFileKey.DOWNLOADED_TIME)));
					entity.setBook_id(cursor.getInt(cursor.getColumnIndex(DtbFileKey.BOOK_ID)));
					entity.setFolder_id(cursor.getInt(cursor.getColumnIndex(DtbFileKey.FOLDER_ID)));
					entity.setType(cursor.getInt(cursor.getColumnIndex(DtbFileKey.IS_AUDIO)));
					entity.setCreated_time(cursor.getString(cursor
							.getColumnIndex(DtbFileKey.CREATED_DATE)));
					entity.setModified_time(cursor.getString(cursor
							.getColumnIndex(DtbFileKey.MODIFIED_DATE)));
					entity.setDeleted_time(cursor.getString(cursor
							.getColumnIndex(DtbFileKey.DELETED_DATE)));
					list.add(entity);

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		cursor.close();

		Collections.sort(list, new PathCompatator());
		return list;
	}
	
	/*
	 * public static ArrayList<FileEntity> getAllAudiosFromBookId(SQLiteDatabase db, int bookId,
			String filePath) {
		Remove @filePath by NamHV
	 */
	public static ArrayList<FileEntity> getAllAudiosFromBookId(SQLiteDatabase db, int bookId) {

		ArrayList<FileEntity> list = new ArrayList<FileEntity>();
		String sql = "SELECT * FROM " + DTB_FILE + " WHERE " + DtbFileKey.BOOK_ID + " = " + bookId
				+ " AND " + DtbFileKey.IS_AUDIO + " = " + Define.FileType.AUDIO;
		Cursor cursor = db.rawQuery(sql, null);
		
		try {
			if (cursor.moveToFirst()) {
				do {
					/*
					 * Modify by NamHV
					 *String path = cursor.getString(cursor.getColumnIndex(DtbFileKey.PATH));
					// check from it
					if (path.compareTo(filePath) >= 0) { 
					 */
					
					FileEntity entity = new FileEntity();
					entity.setId(cursor.getInt(cursor.getColumnIndex(DtbFileKey.ID)));
					entity.setName(cursor.getString(cursor.getColumnIndex(DtbFileKey.NAME)));
					entity.setUrl(cursor.getString(cursor.getColumnIndex(DtbFileKey.URL)));
					entity.setKey(cursor.getString(cursor.getColumnIndex(DtbFileKey.KEY)));
					entity.setPath(cursor.getString(cursor.getColumnIndex(DtbFileKey.PATH)));
					entity.setDownloaded_time(cursor.getString(cursor
							.getColumnIndex(DtbFileKey.DOWNLOADED_TIME)));
					entity.setBook_id(cursor.getInt(cursor.getColumnIndex(DtbFileKey.BOOK_ID)));
					entity.setFolder_id(cursor.getInt(cursor.getColumnIndex(DtbFileKey.FOLDER_ID)));
					entity.setType(cursor.getInt(cursor.getColumnIndex(DtbFileKey.IS_AUDIO)));
					entity.setCreated_time(cursor.getString(cursor
							.getColumnIndex(DtbFileKey.CREATED_DATE)));
					entity.setModified_time(cursor.getString(cursor
							.getColumnIndex(DtbFileKey.MODIFIED_DATE)));
					entity.setDeleted_time(cursor.getString(cursor
							.getColumnIndex(DtbFileKey.DELETED_DATE)));
					list.add(entity);
					/*
					 * Modify by NamHV
					 * }
					 */
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		cursor.close();
		
		Collections.sort(list, new PathCompatator());
		return list;
	}

	public static int countGetAllAudiosByFolderId(SQLiteDatabase db, int folderId) {
		String sql = "SELECT COUNT(" + DtbFileKey.ID + ") FROM " + DTB_FILE + " WHERE "
				+ DtbFileKey.FOLDER_ID + " = " + folderId + " AND " + DtbFileKey.IS_AUDIO + " = "
				+ Define.FileType.AUDIO;
		Cursor cursor = db.rawQuery(sql, null);

		int count = 0;
		try {
			if (cursor.moveToFirst()) {
				count = cursor.getInt(cursor.getColumnIndex("COUNT(" + DtbFileKey.ID + ")"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		cursor.close();
		return count;
	}

	public static ArrayList<FileEntity> getAllAudiosByFolderId(SQLiteDatabase db, int folderId) {
		ArrayList<FileEntity> list = new ArrayList<FileEntity>();
		String sql = "SELECT * FROM " + DTB_FILE + " WHERE " + DtbFileKey.FOLDER_ID + " = "
				+ folderId + " AND " + DtbFileKey.IS_AUDIO + " = " + Define.FileType.AUDIO;
		Cursor cursor = db.rawQuery(sql, null);

		try {
			if (cursor.moveToFirst()) {
				do {
					FileEntity entity = new FileEntity();
					entity.setId(cursor.getInt(cursor.getColumnIndex(DtbFileKey.ID)));
					entity.setName(cursor.getString(cursor.getColumnIndex(DtbFileKey.NAME)));
					entity.setUrl(cursor.getString(cursor.getColumnIndex(DtbFileKey.URL)));
					entity.setKey(cursor.getString(cursor.getColumnIndex(DtbFileKey.KEY)));
					entity.setPath(cursor.getString(cursor.getColumnIndex(DtbFileKey.PATH)));
					entity.setDownloaded_time(cursor.getString(cursor
							.getColumnIndex(DtbFileKey.DOWNLOADED_TIME)));
					entity.setBook_id(cursor.getInt(cursor.getColumnIndex(DtbFileKey.BOOK_ID)));
					entity.setFolder_id(cursor.getInt(cursor.getColumnIndex(DtbFileKey.FOLDER_ID)));
					entity.setType(cursor.getInt(cursor.getColumnIndex(DtbFileKey.IS_AUDIO)));
					entity.setCreated_time(cursor.getString(cursor
							.getColumnIndex(DtbFileKey.CREATED_DATE)));
					entity.setModified_time(cursor.getString(cursor
							.getColumnIndex(DtbFileKey.MODIFIED_DATE)));
					entity.setDeleted_time(cursor.getString(cursor
							.getColumnIndex(DtbFileKey.DELETED_DATE)));
					list.add(entity);

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		cursor.close();
		return list;
	}

	/**
	 * Get a audio file by Book_Id
	 * 
	 * @param db
	 * @param bookId
	 * @return
	 */
	public static FileEntity getAudioByBookId(SQLiteDatabase db, int bookId) {
		FileEntity entity = new FileEntity();
		String sql = "SELECT * FROM " + DTB_FILE + " WHERE " + DtbFileKey.BOOK_ID + " = " + bookId
				+ " AND " + DtbFileKey.IS_AUDIO + " = " + Define.FileType.AUDIO + " ORDER BY "
				+ DtbFileKey.DOWNLOADED_TIME;
		Cursor cursor = db.rawQuery(sql, null);

		try {
			if (cursor.moveToFirst()) {
				entity.setId(cursor.getInt(cursor.getColumnIndex(DtbFileKey.ID)));
				entity.setName(cursor.getString(cursor.getColumnIndex(DtbFileKey.NAME)));
				entity.setUrl(cursor.getString(cursor.getColumnIndex(DtbFileKey.URL)));
				entity.setKey(cursor.getString(cursor.getColumnIndex(DtbFileKey.KEY)));
				entity.setPath(cursor.getString(cursor.getColumnIndex(DtbFileKey.PATH)));
				entity.setDownloaded_time(cursor.getString(cursor
						.getColumnIndex(DtbFileKey.DOWNLOADED_TIME)));
				entity.setBook_id(cursor.getInt(cursor.getColumnIndex(DtbFileKey.BOOK_ID)));
				entity.setFolder_id(cursor.getInt(cursor.getColumnIndex(DtbFileKey.FOLDER_ID)));
				entity.setType(cursor.getInt(cursor.getColumnIndex(DtbFileKey.IS_AUDIO)));
				entity.setCreated_time(cursor.getString(cursor
						.getColumnIndex(DtbFileKey.CREATED_DATE)));
				entity.setModified_time(cursor.getString(cursor
						.getColumnIndex(DtbFileKey.MODIFIED_DATE)));
				entity.setDeleted_time(cursor.getString(cursor
						.getColumnIndex(DtbFileKey.DELETED_DATE)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		cursor.close();
		return entity;
	}

	/**
	 * Get list text files by book_id
	 * 
	 * @param db
	 * @param bookId
	 * @return
	 */
	public static ArrayList<FileEntity> getAllTextsByBookId(SQLiteDatabase db, int bookId) {
		ArrayList<FileEntity> list = new ArrayList<FileEntity>();
		String sql = "SELECT * FROM " + DTB_FILE + " WHERE " + DtbFileKey.BOOK_ID + " = " + bookId
				+ " AND " + DtbFileKey.IS_AUDIO + " = " + Define.FileType.TEXT;
		Cursor cursor = db.rawQuery(sql, null);

		try {
			if (cursor.moveToFirst()) {
				do {
					FileEntity entity = new FileEntity();
					entity.setId(cursor.getInt(cursor.getColumnIndex(DtbFileKey.ID)));
					entity.setName(cursor.getString(cursor.getColumnIndex(DtbFileKey.NAME)));
					entity.setUrl(cursor.getString(cursor.getColumnIndex(DtbFileKey.URL)));
					entity.setKey(cursor.getString(cursor.getColumnIndex(DtbFileKey.KEY)));
					entity.setPath(cursor.getString(cursor.getColumnIndex(DtbFileKey.PATH)));
					entity.setDownloaded_time(cursor.getString(cursor
							.getColumnIndex(DtbFileKey.DOWNLOADED_TIME)));
					entity.setBook_id(cursor.getInt(cursor.getColumnIndex(DtbFileKey.BOOK_ID)));
					entity.setFolder_id(cursor.getInt(cursor.getColumnIndex(DtbFileKey.FOLDER_ID)));
					entity.setType(cursor.getInt(cursor.getColumnIndex(DtbFileKey.IS_AUDIO)));
					entity.setCreated_time(cursor.getString(cursor
							.getColumnIndex(DtbFileKey.CREATED_DATE)));
					entity.setModified_time(cursor.getString(cursor
							.getColumnIndex(DtbFileKey.MODIFIED_DATE)));
					entity.setDeleted_time(cursor.getString(cursor
							.getColumnIndex(DtbFileKey.DELETED_DATE)));
					list.add(entity);

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		
		for (FileEntity entity : list) {
			DebugOption.info("entity", entity.getName());
		}
		// Collections.sort(list, new TitleSortUtils());
		cursor.close();
		return list;
	}

	public static ArrayList<FileEntity> getAllFromFolderId(SQLiteDatabase db, int folderId) {
		ArrayList<FileEntity> list = new ArrayList<FileEntity>();
		String sql = "SELECT * FROM " + DTB_FILE + " WHERE " + DtbFileKey.FOLDER_ID + " = "
				+ folderId;
		Cursor cursor = db.rawQuery(sql, null);

		try {
			if (cursor.moveToFirst()) {
				do {
					FileEntity entity = new FileEntity();
					entity.setId(cursor.getInt(cursor.getColumnIndex(DtbFileKey.ID)));
					entity.setName(cursor.getString(cursor.getColumnIndex(DtbFileKey.NAME)));
					entity.setUrl(cursor.getString(cursor.getColumnIndex(DtbFileKey.URL)));
					entity.setKey(cursor.getString(cursor.getColumnIndex(DtbFileKey.KEY)));
					entity.setPath(cursor.getString(cursor.getColumnIndex(DtbFileKey.PATH)));
					entity.setDownloaded_time(cursor.getString(cursor
							.getColumnIndex(DtbFileKey.DOWNLOADED_TIME)));
					entity.setBook_id(cursor.getInt(cursor.getColumnIndex(DtbFileKey.BOOK_ID)));
					entity.setFolder_id(cursor.getInt(cursor.getColumnIndex(DtbFileKey.FOLDER_ID)));
					entity.setType(cursor.getInt(cursor.getColumnIndex(DtbFileKey.IS_AUDIO)));
					entity.setCreated_time(cursor.getString(cursor
							.getColumnIndex(DtbFileKey.CREATED_DATE)));
					entity.setModified_time(cursor.getString(cursor
							.getColumnIndex(DtbFileKey.MODIFIED_DATE)));
					entity.setDeleted_time(cursor.getString(cursor
							.getColumnIndex(DtbFileKey.DELETED_DATE)));
					list.add(entity);

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		cursor.close();
		return list;
	}

	/**
	 * Get all of audio files
	 * 
	 * @param db
	 * @return list files
	 */
	public static ArrayList<FileEntity> getAllAudio(SQLiteDatabase db) {
		ArrayList<FileEntity> list = new ArrayList<FileEntity>();
		String sql = "SELECT * FROM " + DTB_FILE + " WHERE " + DtbFileKey.IS_AUDIO + " = "
				+ Define.FileType.AUDIO;
		Cursor cursor = db.rawQuery(sql, null);

		try {
			if (cursor.moveToFirst()) {
				do {
					FileEntity entity = new FileEntity();
					entity.setId(cursor.getInt(cursor.getColumnIndex(DtbFileKey.ID)));
					entity.setName(cursor.getString(cursor.getColumnIndex(DtbFileKey.NAME)));
					entity.setUrl(cursor.getString(cursor.getColumnIndex(DtbFileKey.URL)));
					entity.setKey(cursor.getString(cursor.getColumnIndex(DtbFileKey.KEY)));
					entity.setPath(cursor.getString(cursor.getColumnIndex(DtbFileKey.PATH)));
					entity.setDownloaded_time(cursor.getString(cursor
							.getColumnIndex(DtbFileKey.DOWNLOADED_TIME)));
					entity.setBook_id(cursor.getInt(cursor.getColumnIndex(DtbFileKey.BOOK_ID)));
					entity.setFolder_id(cursor.getInt(cursor.getColumnIndex(DtbFileKey.FOLDER_ID)));
					entity.setType(cursor.getInt(cursor.getColumnIndex(DtbFileKey.IS_AUDIO)));
					entity.setCreated_time(cursor.getString(cursor
							.getColumnIndex(DtbFileKey.CREATED_DATE)));
					entity.setModified_time(cursor.getString(cursor
							.getColumnIndex(DtbFileKey.MODIFIED_DATE)));
					entity.setDeleted_time(cursor.getString(cursor
							.getColumnIndex(DtbFileKey.DELETED_DATE)));
					list.add(entity);

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		cursor.close();
		return list;
	}

	/**
	 * Search a File by it's name
	 * 
	 * @param db
	 * @param name
	 * @return
	 */
	public static int searchByName(SQLiteDatabase db, String name) {
		int fileId = Define.DEFAULT_INT;
		if (!name.equals(Define.DEFAULT_STRING)) {
			String sql = "SELECT * FROM " + DTB_FILE + " WHERE " + DtbFileKey.NAME + " = '" + name
					+ "'";
			Cursor cursor = db.rawQuery(sql, null);
				try {
					if (cursor.moveToFirst()) {
					fileId = cursor.getInt(cursor.getColumnIndex(DtbFileKey.ID));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}finally {
					cursor.close();
				}

			}

		return fileId;
	}

	public static int searchTextBook(SQLiteDatabase db, int bookId) {
		String sql = "SELECT * FROM " + DTB_FILE + " WHERE " + DtbFileKey.BOOK_ID + " = " + bookId
				+ " AND " + DtbFileKey.IS_AUDIO + " = " + Define.FileType.TEXT;
		DebugOption.info("SQL", "SQL = " + sql);
		int fileId = Define.DEFAULT_INT;
		Cursor cursor = db.rawQuery(sql, null);
		try {
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				fileId = cursor.getInt(cursor.getColumnIndex(DtbFileKey.ID));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		return fileId;
	}
	
	public static int searchFileExistPath(SQLiteDatabase db, String path) {
		String sql = "SELECT * FROM " + DTB_FILE + " WHERE " + DtbFileKey.PATH + " = '" + path
				+ "'";
		int fileId = Define.DEFAULT_INT;
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			fileId = cursor.getInt(cursor.getColumnIndex(DtbFileKey.ID));
		}
		cursor.close();

		return fileId;
	}
	
	public static void updateFilename(SQLiteDatabase db, int fileId, String name) {
		ContentValues values = new ContentValues();
		values.put(DtbFileKey.NAME, name);

		// update row
		db.update(DTB_FILE, values, DtbFileKey.ID + " = ?", new String[] { "" + fileId });
	}

	public static void delete(SQLiteDatabase db, int id) {
		try {
			db.delete(DTB_FILE, DtbFileKey.ID + " = ?", new String[] { id + "" });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void changeNewFolderId(SQLiteDatabase db, int id, int newFolderId) {
		ContentValues values = new ContentValues();
		values.put(DtbFileKey.FOLDER_ID, newFolderId);
		db.update(DTB_FILE, values, DtbBookKey.ID + " = ?", new String[] { id + "" });
	}

}
