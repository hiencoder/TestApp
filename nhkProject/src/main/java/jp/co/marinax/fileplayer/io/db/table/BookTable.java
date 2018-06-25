package jp.co.marinax.fileplayer.io.db.table;

import java.util.ArrayList;

import jp.co.marinax.fileplayer.app.config.DebugOption;
import jp.co.marinax.fileplayer.app.config.Define;
import jp.co.marinax.fileplayer.io.entity.BookEntity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class BookTable {
	public static final String DTB_BOOK = "dtbBook";

	public static class DtbBookKey {
		public static String ID = "id";
		public static String NAME = "name";
		public static String FOLDER_ID = "folder_id";
		public static String CREATED_DATE = "create_date";
		public static String MODIFIED_DATE = "modifield_date";
		public static String DELETED_DATE = "deleted_date";
		// New Start 20151223 SonBX
		public static String DISPLAY_NAME = "display_name";
		// New End 20151223 SonBX
	}

	// create table AudioTable
	public static void createTable(SQLiteDatabase db) {
		// Mod Start 20151223 SonBX
//		String CREATE_TABLE = " CREATE TABLE " + DTB_BOOK + "(" + DtbBookKey.ID
//				+ " INTEGER PRIMARY KEY," + DtbBookKey.NAME + " TEXT, " + DtbBookKey.FOLDER_ID
//				+ " INTEGER, " + DtbBookKey.CREATED_DATE + " TEXT," + DtbBookKey.MODIFIED_DATE
//				+ " TEXT," + DtbBookKey.DELETED_DATE + " TEXT" + ")";
		// Mod 20151223
		String CREATE_TABLE = " CREATE TABLE " + DTB_BOOK + "(" + DtbBookKey.ID
				+ " INTEGER PRIMARY KEY," + DtbBookKey.NAME + " TEXT, " + DtbBookKey.DISPLAY_NAME
				+ " TEXT," + DtbBookKey.FOLDER_ID
				+ " INTEGER, " + DtbBookKey.CREATED_DATE + " TEXT," + DtbBookKey.MODIFIED_DATE
				+ " TEXT," + DtbBookKey.DELETED_DATE + " TEXT" + ")";
		// Mod End 20151223

		DebugOption.info("CREATE TABLE BOOK : ", CREATE_TABLE);
		db.execSQL(CREATE_TABLE);
	}

	public static void dropTable(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS " + DTB_BOOK);
	}

	public static long insert(SQLiteDatabase db, BookEntity entity) {
		DebugOption.info("insert Book", "insert Book");
		long bookId = 0;

		ContentValues value = new ContentValues();
		if (entity.getId() != Define.DEFAULT_INT) {
			value.put(DtbBookKey.ID, entity.getId());
		}
		value.put(DtbBookKey.NAME, entity.getName());
		value.put(DtbBookKey.FOLDER_ID, entity.getFolder_id());
		value.put(DtbBookKey.CREATED_DATE, entity.getCreated_date());
		value.put(DtbBookKey.MODIFIED_DATE, entity.getModified_date());
		value.put(DtbBookKey.DELETED_DATE, entity.getDeleted_date());
		// New Start 20151223 SonBX
		value.put(DtbBookKey.DISPLAY_NAME, entity.getDisplay_name());
		// New End 20151223
		try {
			bookId = db.insert(DTB_BOOK, null, value);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bookId;
	}

	public static BookEntity getEntity(SQLiteDatabase db, int id) {
		BookEntity entity = new BookEntity();
		String sql = "SELECT * FROM " + DTB_BOOK + " WHERE " + DtbBookKey.ID + " = " + id;
		Cursor cursor = db.rawQuery(sql, null);

		try {
			if (cursor.moveToFirst()) {
				entity.setId(id);
				entity.setName(cursor.getString(cursor.getColumnIndex(DtbBookKey.NAME)));
				entity.setFolder_id(cursor.getInt(cursor.getColumnIndex(DtbBookKey.FOLDER_ID)));
				entity.setModified_date(cursor.getString(cursor
						.getColumnIndex(DtbBookKey.MODIFIED_DATE)));
				entity.setCreated_date(cursor.getString(cursor
						.getColumnIndex(DtbBookKey.CREATED_DATE)));
				entity.setDeleted_date(cursor.getString(cursor
						.getColumnIndex(DtbBookKey.DELETED_DATE)));
				// New Start 20151223 SonBX
				entity.setDisplay_name(cursor.getString(cursor.getColumnIndex(DtbBookKey.DISPLAY_NAME)));
				// New End 20151223 SonBX
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		cursor.close();
		return entity;
	}

	public static ArrayList<BookEntity> getAllEntity(SQLiteDatabase db) {
		ArrayList<BookEntity> list = new ArrayList<BookEntity>();
		String sql = "SELECT * FROM " + DTB_BOOK;
		Cursor cursor = db.rawQuery(sql, null);

		try {
			if (cursor.moveToFirst()) {
				do {
					BookEntity entity = new BookEntity();
					entity.setId(cursor.getInt(cursor.getColumnIndex(DtbBookKey.ID)));
					entity.setName(cursor.getString(cursor.getColumnIndex(DtbBookKey.NAME)));
					entity.setFolder_id(cursor.getInt(cursor.getColumnIndex(DtbBookKey.FOLDER_ID)));
					entity.setModified_date(cursor.getString(cursor
							.getColumnIndex(DtbBookKey.MODIFIED_DATE)));
					entity.setCreated_date(cursor.getString(cursor
							.getColumnIndex(DtbBookKey.CREATED_DATE)));
					entity.setDeleted_date(cursor.getString(cursor
							.getColumnIndex(DtbBookKey.DELETED_DATE)));
					// New Start 20151223 SonBX
					entity.setDisplay_name(cursor.getString(cursor.getColumnIndex(DtbBookKey.DISPLAY_NAME)));
					// New End 20151223 SonBX
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

	public static ArrayList<BookEntity> getAllEntitiesFromFolderId(SQLiteDatabase db, int folderId) {
		ArrayList<BookEntity> list = new ArrayList<BookEntity>();
		String sql = "SELECT * FROM " + DTB_BOOK + " WHERE " + DtbBookKey.FOLDER_ID + " = "
				+ folderId;
		Cursor cursor = db.rawQuery(sql, null);

		try {
			if (cursor.moveToFirst()) {
				do {
					BookEntity entity = new BookEntity();
					entity.setId(cursor.getInt(cursor.getColumnIndex(DtbBookKey.ID)));
					entity.setName(cursor.getString(cursor.getColumnIndex(DtbBookKey.NAME)));
					entity.setFolder_id(cursor.getInt(cursor.getColumnIndex(DtbBookKey.FOLDER_ID)));
					entity.setModified_date(cursor.getString(cursor
							.getColumnIndex(DtbBookKey.MODIFIED_DATE)));
					entity.setCreated_date(cursor.getString(cursor
							.getColumnIndex(DtbBookKey.CREATED_DATE)));
					entity.setDeleted_date(cursor.getString(cursor
							.getColumnIndex(DtbBookKey.DELETED_DATE)));
					// New Start 20151223 SonBX
					entity.setDisplay_name(cursor.getString(cursor.getColumnIndex(DtbBookKey.DISPLAY_NAME)));
					// New End 20151223 SonBX
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
	 * Get book that has same name
	 * 
	 * @param db
	 * @param bookName
	 * @return book_id
	 */
	public static int searchBook(SQLiteDatabase db, String bookName) {
		ArrayList<BookEntity> arrayList = getAllEntity(db);
		for (BookEntity entity : arrayList) {
			DebugOption.info("entityNAme ", entity.getName());
		}
		
		String sql = "SELECT * FROM " + DTB_BOOK + " WHERE " + DtbBookKey.NAME + " = '"
				+ bookName
				+ "'";
		
		DebugOption.info("SQL = ", "sql = " + sql);
		Cursor cursor = db.rawQuery(sql, null);

		int bookId = 0;

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			bookId = cursor.getInt(cursor.getColumnIndex(DtbBookKey.ID));
			cursor.close();
		}
		DebugOption.info("BOOK ID ", "bookId = " + bookId);
		return bookId;
	}

	public static void changeFolderId(SQLiteDatabase db, int id, int newFolderId) {
		// String sql = "UPDATE " + DTB_BOOK + " SET " + DtbBookKey.FOLDER_ID
		// + " = " + newFolderId + " WHERE " + DtbBookKey.ID + " = " + id;
		ContentValues values = new ContentValues();
		values.put(DtbBookKey.FOLDER_ID, newFolderId);
		db.update(DTB_BOOK, values, DtbBookKey.ID + " = ?", new String[] { id + "" });
	}
	
	// New Start 20151223 SonBX
	public static void changeDisplayName(SQLiteDatabase db, int id, String newName) {
		ContentValues values = new ContentValues();
		values.put(DtbBookKey.DISPLAY_NAME, newName);
		db.update(DTB_BOOK, values, DtbBookKey.ID + " = ?", new String[] { id + "" });
	}
	
	public static String getDisplayName(SQLiteDatabase db, int id) {
		String displayName = "";
		String sql = "SELECT " + DtbBookKey.DISPLAY_NAME + " FROM " + DTB_BOOK + " WHERE " + DtbBookKey.ID + " = "
				+ id;
		DebugOption.debug("getDisplayName", sql);
		Cursor cursor = db.rawQuery(sql, null);
		if(cursor.getCount() > 0) {
			cursor.moveToFirst();
			displayName = cursor.getString(0);
		}
		cursor.close();
		return displayName;
	}
	// New End 20151223 SonBX

	public static void delete(SQLiteDatabase db, int id) {
		try {
			db.delete(DTB_BOOK, DtbBookKey.ID + " = ?", new String[] { id + "" });
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
