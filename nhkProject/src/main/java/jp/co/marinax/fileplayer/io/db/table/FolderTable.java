package jp.co.marinax.fileplayer.io.db.table;

import java.util.ArrayList;

import jp.co.marinax.fileplayer.app.config.DebugOption;
import jp.co.marinax.fileplayer.app.config.Define;
import jp.co.marinax.fileplayer.io.db.table.BookTable.DtbBookKey;
import jp.co.marinax.fileplayer.io.entity.FolderEntity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class FolderTable {
	public static final String DTB_FOLDER = "dtbFolder";
	public static int PARENT_ROOT_ID = 0;

	public static class DtbFolderKey {
		public static String ID = "id";
		public static String NAME = "name";
		public static String PARENT_ID = "parent_id";
		public static String CREATED_DATE = "created_date";
		public static String MODIFIED_DATE = "modified_date";
		public static String DELETED_DATE = "deleted_date";
		// New Start 20151008 SonBX
		public static String TEMP_NAME = "temp_name";
		public static String CAN_REDOWNLOAD = "can_redownload";
		// New End 20151008 SonBX
	}

	// create table dtbResultPlayed
	public static void createTable(SQLiteDatabase db) {
		// Mod Start 20151008 SonBX
//		String CREATE_TABLE = " CREATE TABLE " + DTB_FOLDER + "("
//				+ DtbFolderKey.ID + " INTEGER PRIMARY KEY," + DtbFolderKey.NAME
//				+ " TEXT, " + DtbFolderKey.PARENT_ID + " INTEGER,"
//				+ DtbFolderKey.CREATED_DATE + " TEXT,"
//				+ DtbFolderKey.MODIFIED_DATE + " TEXT,"
//				+ DtbFolderKey.DELETED_DATE + " TEXT" + ")";
		// Mod 20151008
		String CREATE_TABLE = " CREATE TABLE " + DTB_FOLDER + "("
				+ DtbFolderKey.ID + " INTEGER PRIMARY KEY," + DtbFolderKey.NAME
				+ " TEXT, " + DtbFolderKey.PARENT_ID + " INTEGER,"
				+ DtbFolderKey.CREATED_DATE + " TEXT,"
				+ DtbFolderKey.MODIFIED_DATE + " TEXT,"
				+ DtbFolderKey.DELETED_DATE + " TEXT,"
				+ DtbFolderKey.TEMP_NAME + " TEXT,"
				+ DtbFolderKey.CAN_REDOWNLOAD + " INTEGER"+ ")";
		// Mod End 20151008 SonBX
		DebugOption.info("CREATE TABLE FOLDER : ", CREATE_TABLE);
		db.execSQL(CREATE_TABLE);
	}

	public static void dropTable(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS " + DTB_FOLDER);
	}

	public static long newFolder(SQLiteDatabase db, FolderEntity entity,
			int parentID) {
		ContentValues value = new ContentValues();
		// id auto increment
		value.put(DtbFolderKey.NAME, entity.getName());
		value.put(DtbFolderKey.PARENT_ID, parentID);
		value.put(DtbFolderKey.CREATED_DATE, entity.getCreated_date());
		value.put(DtbFolderKey.MODIFIED_DATE, entity.getModified_date());
		value.put(DtbFolderKey.DELETED_DATE, entity.getDeleted_date());
		// New Start 20151008 SonBX
		value.put(DtbFolderKey.TEMP_NAME, entity.getTemp_name());
		value.put(DtbFolderKey.CAN_REDOWNLOAD, entity.getCan_redownload());
		// New End 20151008 SonBX
		long id = -1;
		try {
			id = db.insert(DTB_FOLDER, null, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return id;
	}

	public static FolderEntity getEntity(SQLiteDatabase db, int id) {
		FolderEntity entity = new FolderEntity();
		String sql = "SELECT * FROM " + DTB_FOLDER + " WHERE "
				+ DtbFolderKey.ID + " = " + id;
		Cursor cursor = db.rawQuery(sql, null);
		try {
			if (cursor.moveToFirst()) {
				do {
					entity.setId(id);
					entity.setParent_id(cursor.getInt(cursor
							.getColumnIndex(DtbFolderKey.PARENT_ID)));
					entity.setName(cursor.getString(cursor
							.getColumnIndex(DtbFolderKey.NAME)));
					entity.setCreated_date(cursor.getString(cursor
							.getColumnIndex(DtbFolderKey.CREATED_DATE)));
					entity.setModified_date(cursor.getString(cursor
							.getColumnIndex(DtbFolderKey.MODIFIED_DATE)));
					entity.setDeleted_date(cursor.getString(cursor
							.getColumnIndex(DtbFolderKey.DELETED_DATE)));
					// New Start 20151008 SonBX
					entity.setTemp_name(cursor.getString(cursor.getColumnIndex(DtbFolderKey.TEMP_NAME)));
					entity.setCan_redownload(cursor.getInt(cursor.getColumnIndex(DtbFolderKey.CAN_REDOWNLOAD)));
					// New End 20151008 SonBX
					entity.setCheck(false);
					entity.setTypeImage(Define.TYPE_FOLDER);
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		cursor.close();
		return entity;
	}

	public static void delete(SQLiteDatabase db, int id) {

		db.delete(DTB_FOLDER, DtbFolderKey.ID + " = ?",
				new String[] { id + "" });

		String sql = "SELECT " + DtbFolderKey.ID + " FROM " + DTB_FOLDER
				+ " WHERE " + DtbFolderKey.PARENT_ID + " = " + id;

		Cursor cursor = db.rawQuery(sql, null);

		try {
			if (cursor.moveToFirst()) {
				do {
					int childId = cursor.getInt(cursor
							.getColumnIndex(DtbFolderKey.ID));
					delete(db, childId);
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		cursor.close();
	}

	public static int CountgetAllEntityFromParentId(SQLiteDatabase db,
			int parentId) {
		String sql = "SELECT COUNT(" + DtbFolderKey.ID + ") FROM " + DTB_FOLDER
				+ " WHERE " + DtbFolderKey.PARENT_ID + " = " + parentId;
		Cursor cursor = db.rawQuery(sql, null);
		int count = 0;
		try {
			if (cursor.moveToFirst()) {
				do {
					count = cursor.getInt(cursor.getColumnIndex("COUNT("
							+ DtbFolderKey.ID + ")"));
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		cursor.close();
		return count;
	}

	public static ArrayList<Integer> getAllIdFromParentId(SQLiteDatabase db,
			int parentId) {
		ArrayList<Integer> arrListTable = new ArrayList<Integer>();
		String sql = "SELECT " + DtbFolderKey.ID + " FROM " + DTB_FOLDER
				+ " WHERE " + DtbFolderKey.PARENT_ID + " = " + parentId;
		Cursor cursor = db.rawQuery(sql, null);
		try {
			if (cursor.moveToFirst()) {
				do {
					arrListTable.add(cursor.getInt(cursor
							.getColumnIndex(DtbFolderKey.ID)));
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		cursor.close();
		return arrListTable;
	}

	public static ArrayList<FolderEntity> getAllEntityFromParentId(
			SQLiteDatabase db, int parentId) {
		ArrayList<FolderEntity> arrListTable = new ArrayList<FolderEntity>();
		String sql = "SELECT * FROM " + DTB_FOLDER + " WHERE "
				+ DtbFolderKey.PARENT_ID + " = " + parentId;
		Cursor cursor = db.rawQuery(sql, null);
		try {
			if (cursor.moveToFirst()) {
				do {
					FolderEntity entity = new FolderEntity();
					entity.setId(cursor.getInt(cursor
							.getColumnIndex(DtbFolderKey.ID)));
					entity.setName(cursor.getString(cursor
							.getColumnIndex(DtbFolderKey.NAME)));
					entity.setParent_id(parentId);
					entity.setCreated_date(cursor.getString(cursor
							.getColumnIndex(DtbFolderKey.CREATED_DATE)));
					entity.setModified_date(cursor.getString(cursor
							.getColumnIndex(DtbFolderKey.MODIFIED_DATE)));
					entity.setDeleted_date(cursor.getString(cursor
							.getColumnIndex(DtbFolderKey.DELETED_DATE)));
					entity.setCheck(false);
					entity.setTypeImage(Define.TYPE_FOLDER);
					arrListTable.add(entity);
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		cursor.close();
		return arrListTable;
	}

	public static int getParentIdFromChildID(SQLiteDatabase db, int childId) {
		int parentId = 0;
		String sql = "SELECT " + DtbFolderKey.PARENT_ID + " FROM " + DTB_FOLDER
				+ " WHERE " + DtbFolderKey.ID + " = " + childId;
		Cursor cursor = db.rawQuery(sql, null);
		try {
			if (cursor.moveToFirst()) {
				parentId = cursor.getInt(cursor
						.getColumnIndex(DtbFolderKey.PARENT_ID));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		cursor.close();
		return parentId;
	}

	public static void changeNewFolderId(SQLiteDatabase db, int id,
			int newParentId) {
		ContentValues values = new ContentValues();
		values.put(DtbFolderKey.PARENT_ID, newParentId);
		db.update(DTB_FOLDER, values, DtbBookKey.ID + " = ?", new String[] { id
				+ "" });
	}

	public static void move(SQLiteDatabase db, int newParentId) {

	}

	public static void updateFilename(SQLiteDatabase db, int fileId, String name) {
		ContentValues values = new ContentValues();
		values.put(DtbFolderKey.NAME, name);

		// update row
		db.update(DTB_FOLDER, values, DtbFolderKey.ID + " = ?", new String[] { "" + fileId });
	}
	
	// New Start 20151008 SonBX
	public static boolean checkIfCanRedownload(SQLiteDatabase db, String tempName) {
		boolean canReDownload = true;
		String sql = "SELECT * FROM " + DTB_FOLDER
				+ " WHERE " + DtbFolderKey.TEMP_NAME + " = '" + tempName + "'";
		DebugOption.info("sql", sql);
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()) {
			do {
				if(cursor.getInt(cursor.getColumnIndex(DtbFolderKey.CAN_REDOWNLOAD)) == Define.DEFAULT_INT) {
					canReDownload = false;
					break;
				}
			} while (cursor.moveToNext());
		}
		cursor.close();
		return canReDownload;
	}
	// New End 20151008 SonBX

	// New Start 20151009 SonBX
	public static int checkExistByTempName(SQLiteDatabase db, String tempName) {
		int id = Define.DEFAULT_INT;
		String sql = "SELECT * FROM " + DTB_FOLDER
				+ " WHERE " + DtbFolderKey.TEMP_NAME + " = '" + tempName + "'";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()) {
			id = cursor.getInt(cursor.getColumnIndex(DtbFolderKey.ID));
		}
		cursor.close();
		return id;
	}
	
	public static void setCanReDownload(SQLiteDatabase db, int folderId, int canRedownload) {
		ContentValues values = new ContentValues();
		values.put(DtbFolderKey.CAN_REDOWNLOAD, canRedownload);
		
		// update row
		db.update(DTB_FOLDER, values, DtbFolderKey.ID + " = ?", new String[] { "" + folderId });
	}
	// New End 20151009 SonBX
}
