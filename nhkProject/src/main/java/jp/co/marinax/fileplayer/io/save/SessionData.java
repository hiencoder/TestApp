package jp.co.marinax.fileplayer.io.save;

import java.util.ArrayList;

import jp.co.marinax.fileplayer.app.config.Define;
import jp.co.marinax.fileplayer.io.db.DatabaseManager;
import jp.co.marinax.fileplayer.io.entity.FolderEntity;
import android.database.sqlite.SQLiteDatabase;

public class SessionData {

	// int item selected

	public static int selectedItem = 0;

	public static int getSelectedItem() {
		return selectedItem;
	}

	public static void setSelectedItem(int selectedItem) {
		SessionData.selectedItem = selectedItem;
	}

	public static boolean flagActivity = false;

	public static boolean isFlagActivity() {
		return flagActivity;
	}

	public static void setFlagActivity(boolean flagActivity) {
		SessionData.flagActivity = flagActivity;
	}

	public static boolean flag = true;
	/********************************************
	 * setup DatabaseManager
	 ********************************************/

	private static DatabaseManager databaseManager = null;
	private static SQLiteDatabase db = null;

	// set get method for Database manager
	public static DatabaseManager getDatabaseManager() {
		return databaseManager;
	}

	public static void setDatabaseManager(DatabaseManager databaseManager) {
		SessionData.databaseManager = databaseManager;
	}

	public static SQLiteDatabase getDb() {
		return db;
	}

	public static void setDb(SQLiteDatabase db) {
		SessionData.db = db;
	}

	/************************************************
	 * Define folder
	 ************************************************/
	public static String folderApp = "";

	public static String getFolderApp() {
		return folderApp;
	}

	public static void setFolderApp(String folderApp) {
		SessionData.folderApp = folderApp;
	}

	/***********************************************
	 * Define for move Item
	 ***********************************************/
	public static ArrayList<FolderEntity> moveListItems = new ArrayList<FolderEntity>();

	public static ArrayList<FolderEntity> getMoveListItems() {
		return moveListItems;
	}

	public static void setMoveListItems(ArrayList<FolderEntity> moveListItems) {
		SessionData.moveListItems.clear();
		SessionData.moveListItems.addAll(moveListItems);
	}

	public static void clearMoveItems() {
		SessionData.moveListItems.clear();
	}

	/**********************************************
	 * SelectActivity
	 **********************************************/
	public static int fileType = Define.DEFAULT_INT;

	public static int getFileType() {
		return fileType;
	}

	public static void setFileType(int fileType) {
		SessionData.fileType = fileType;
	}



	/**************************************
	 * Sort
	 **************************************/
	// Mod Start 20160106 SonBX
	// Set default order to true
//	public static boolean titleSort = false;
	// Mod 20160106
	public static boolean titleSort = true;
	// Mod End 20160106 SonBX

	public static boolean isTitleSort() {
		return titleSort;
	}

	public static void setTitleSort(boolean titleSort) {
		SessionData.titleSort = titleSort;
	}

	/************************************
	 * to top distance
	 ************************************/
	public static int mToTopDistance = 0;

	public static int getmToTopDistance() {
		return mToTopDistance;
	}

	public static void setmToTopDistance(int mToTopDistance) {
		SessionData.mToTopDistance = mToTopDistance;
	}

	public static String mUUID;

	public static String getmUUID() {
		return mUUID;
	}

	public static void setmUUID(String mUUID) {
		SessionData.mUUID = mUUID;
	}
	// variable for download
	public static boolean download = true;

	public static boolean isDownload() {
		return download;
	}

	public static void setDownload(boolean download) {
		SessionData.download = download;
	}

}
