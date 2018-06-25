package jp.co.marinax.fileplayer.io.db.table;

import java.util.ArrayList;

import jp.co.marinax.fileplayer.app.config.DebugOption;
import jp.co.marinax.fileplayer.app.config.Define;
import jp.co.marinax.fileplayer.io.entity.BookMarkEntity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class BookMarksTable {
	public static final String DTB_BOOK_MARK = "DtbBookMark";

	public static class BookMarksKey {
		public static String ID = "id";
		public static String NAME = "name";
		public static String URL = "url";
	}
	
	// create table AudioTable
	public static void createTable(SQLiteDatabase db) {
		String CREATE_TABLE = " CREATE TABLE " + DTB_BOOK_MARK + "(" + BookMarksKey.ID
				+ " INTEGER PRIMARY KEY," + BookMarksKey.NAME + " TEXT, " + BookMarksKey.URL
				+ " TEXT" + ")";

		DebugOption.info("CREATE TABLE BOOK MARKS : ", CREATE_TABLE);
		db.execSQL(CREATE_TABLE);
	}

	public static void dropTable(SQLiteDatabase db) {
		DebugOption.info("DopTable", "DropTable");
		db.execSQL("DROP TABLE IF EXISTS " + DTB_BOOK_MARK);
	}

	// get all Data
	public static ArrayList<BookMarkEntity> getAllEntity(SQLiteDatabase db) {
		ArrayList<BookMarkEntity> list = new ArrayList<BookMarkEntity>();
		String sql = "SELECT * FROM " + DTB_BOOK_MARK + " ORDER BY " + BookMarksKey.ID + " DESC";
		Cursor cursor = db.rawQuery(sql, null);

		try {
			if (cursor.moveToFirst()) {
				do {
					BookMarkEntity entity = new BookMarkEntity();
					entity.setId(cursor.getInt(cursor.getColumnIndex(BookMarksKey.ID)));
					entity.setName(cursor.getString(cursor.getColumnIndex(BookMarksKey.NAME)));
					entity.setUrl(cursor.getString(cursor.getColumnIndex(BookMarksKey.URL)));
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

	// insert Data
	public static long insert(SQLiteDatabase db, BookMarkEntity entity) {
		DebugOption.info("insert Book", "insert Book");
		long bookMarksId = 0;

		ContentValues value = new ContentValues();
		value.put(BookMarksKey.NAME, entity.getName());
		value.put(BookMarksKey.URL, entity.getUrl());
		

		try {
			bookMarksId = db.insert(DTB_BOOK_MARK, null, value);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bookMarksId;
	}
	
	// search Link
	public static long searchLink(SQLiteDatabase db,String url) {
		int bookMarksId = Define.DEFAULT_INT;
		DebugOption.info("url", "url = " + url);
		
		String sql = "SELECT * FROM " + DTB_BOOK_MARK 
 + " WHERE " + BookMarksKey.URL + " = '" + url
				+ "'";
		Cursor cursor = db.rawQuery(sql, null);

		if (cursor.moveToFirst()) {
			bookMarksId = cursor.getInt(cursor.getColumnIndex(BookMarksKey.ID));
		}
		cursor.close();
		return bookMarksId;
	}

	// delete Link
	public static void deleteLink(SQLiteDatabase db, int id) {
		db.delete(DTB_BOOK_MARK, BookMarksKey.ID + " = ?", new String[] { id + "" });
	}

	// New Start 20160112 SonBX
	public static void deleteLink(SQLiteDatabase db, BookMarkEntity entity) {
		long id = searchLink(db, entity.getUrl());
		if((int)id != Define.DEFAULT_INT) {
			deleteLink(db, (int)id);
		}
	}
	// New End 20160112 SonBX
}

