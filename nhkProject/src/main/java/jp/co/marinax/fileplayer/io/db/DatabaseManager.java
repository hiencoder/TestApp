package jp.co.marinax.fileplayer.io.db;

import jp.co.marinax.fileplayer.app.config.DebugOption;
import jp.co.marinax.fileplayer.app.config.Define;
import jp.co.marinax.fileplayer.io.db.table.BookMarksTable;
import jp.co.marinax.fileplayer.io.db.table.BookTable;
import jp.co.marinax.fileplayer.io.db.table.FilesTable;
import jp.co.marinax.fileplayer.io.db.table.FolderTable;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseManager extends SQLiteOpenHelper {

	public DatabaseManager(Context context) {
		super(context, Define.DATABASE_NAME, null, Define.DB_VERSION_2);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		DebugOption.debug("create Table : ", "create Table");
		BookTable.createTable(db);
		FilesTable.createTable(db);
		FolderTable.createTable(db);
		BookMarksTable.createTable(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		DebugOption.info("upgrade : ", "Upgrade ");
		// Mod Start 20151008 SonBX
//		BookTable.dropTable(db);
//		FilesTable.dropTable(db);
//		FolderTable.dropTable(db);
//		BookMarksTable.dropTable(db);
//		onCreate(db);
		// Mod 20151008
		if(oldVersion == Define.VERSION && newVersion == Define.DB_VERSION_2){
			// Add temp_name to folder table for storing temporary name
			String upgrade1 = "ALTER TABLE " + FolderTable.DTB_FOLDER + " ADD COLUMN " + FolderTable.DtbFolderKey.TEMP_NAME + " text default ''";
			// Add can_redownload to folder table for indicating that you can re-download this folder or not
			String upgrade2 = "ALTER TABLE " + FolderTable.DTB_FOLDER + " ADD COLUMN " + FolderTable.DtbFolderKey.CAN_REDOWNLOAD + " integer default 0";
			
			// New Start 20151223 SonBX
			// Add display_name to book table for displaying real name
			String upgrade3 = "ALTER TABLE " + BookTable.DTB_BOOK + " ADD COLUMN " + BookTable.DtbBookKey.DISPLAY_NAME + " text default ''";
			db.execSQL(upgrade3);
			// New End 20151223 SonBX
			db.execSQL(upgrade1);
			db.execSQL(upgrade2);
		}
		// Mod End 20151008 SonBX
	}

}
