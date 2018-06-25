package jp.co.marinax.fileplayer.ui.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.co.marinax.fileplayer.R;
import jp.co.marinax.fileplayer.app.check.CheckFirstUse;
import jp.co.marinax.fileplayer.app.config.DebugOption;
import jp.co.marinax.fileplayer.app.config.Define;
import jp.co.marinax.fileplayer.io.db.DatabaseManager;
import jp.co.marinax.fileplayer.io.db.table.FilesTable;
import jp.co.marinax.fileplayer.io.db.table.FolderTable;
import jp.co.marinax.fileplayer.io.entity.FileEntity;
import jp.co.marinax.fileplayer.io.entity.FolderEntity;
import jp.co.marinax.fileplayer.io.save.SessionData;
import jp.co.marinax.fileplayer.io.save.SharedPreferencesData;
import jp.co.marinax.fileplayer.ui.activity.base.BaseActivity;
import jp.co.marinax.fileplayer.ui.activity.base.BaseActivity.SortClass;
import jp.co.marinax.fileplayer.utils.FileIO;
import jp.co.marinax.fileplayer.view.adapter.FolderAdapter;
import jp.co.marinax.fileplayer.view.custom.DLTimeComparator;
import jp.co.marinax.fileplayer.view.custom.TitleComparator;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.testflightapp.lib.TestFlight;

public class TopActivity extends BaseActivity implements SortClass {
	private String TAG = "TopActivity";
	private int mParentID = 0;
	private String PARENT_ID = "parent_id";
	private ArrayList<FolderEntity> lvFolderEntities = new ArrayList<FolderEntity>();
	private Button btnBackId;
	/** ListView */
	private ListView mLvFile;
	private List<Integer> mListDeleteFolder = new ArrayList<Integer>();
	/** ListView's adapter */
	private FolderAdapter mAdapter;

	/** Sort type */
	private int mTypeProcess = Define.FileType.FOLDER;

	/** Context */
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TestFlight.passCheckpoint(TAG);
		TestFlight.log("come in " + TAG);
		// check the first use
		if (CheckFirstUse.isTheFirstUse(TopActivity.this)) {
			DebugOption.info("THE FIRST USE : ", "content : "
					+ SharedPreferencesData.getFirstUse(TopActivity.this));
			SharedPreferencesData.setFirstUse(Define.THE_FIRST_USE,
					TopActivity.this);
			FileIO.deleteFile(SessionData.getFolderApp());
		} else {
			DebugOption.info("THE FIRST USE : ", "content : "
					+ SharedPreferencesData.getFirstUse(TopActivity.this));
		}

		// set content
		setContentView(R.layout.activity_top);
		btnBackId = (Button) findViewById(R.id.btnBackId);
		// Init
		init();
		// Create folder application
		if (FileIO.createFolderApp(this)) {
			DebugOption.info(TAG, "success");
		}
		checkStopPosition();

		// Find Views
		mLvFile = (ListView) findViewById(R.id.lvListfile);
		mAdapter = new FolderAdapter(TopActivity.this, lvFolderEntities);
		mLvFile.setAdapter(mAdapter);
		int extra = getIntent().getIntExtra(PARENT_ID, 0);
		mParentID = extra;
		setAction();
		mTypeProcess = getIntent().getIntExtra(Define.FILE_TYPE,
				Define.FileType.FOLDER);
		
		// onAction for llistView
		Log.e("TopActivity", "onCreate");
		handleUrlScheme(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		mTypeProcess = intent.getIntExtra(Define.FILE_TYPE,
				Define.FileType.FOLDER);
		DebugOption.info("SINGLE TOP", "SINGLE TOP = " + mTypeProcess);

		Log.e("TopActivity", "onNewIntent");
		handleUrlScheme(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateListFolder(mParentID, mTypeProcess);
		DebugOption.info("Distance to Top : ",
				"Distance to Top : " + SessionData.getmToTopDistance());
		if (this.isTaskRoot()) {
			btnBackId.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onDestroy() {
		if (this.isTaskRoot()) {
			DebugOption.error("DELETE", "DELETE");
			new AsyncTask<Void, Void, Void>() {
				@Override
				protected Void doInBackground(Void... params) {
					File folder = new File(SessionData.getFolderApp() + "/" + Define.TEMP_FOLDER);
					if (folder.exists()) {
						FileIO.deleteFolder(folder);
					}
					return null;
				}
			}.execute();
		}
		super.onDestroy();
	}
	
	private void handleUrlScheme(Intent intent) {
		// check data
		if (intent != null) {
			Uri data = intent.getData();
			if (data != null) {
				Log.e("data: ", "data: "+data);
				if (data.getScheme().endsWith("gogaku")) {
					// get url
					String url = data.getQueryParameter("url");
					
					if(url!=null && !url.equals("")) {
						// move to browser
						Intent i = new Intent(this, WebViewActivity.class);
						i.putExtra(Define.WEB_BOOKMARK_URL, url);
						startActivity(i);
					}
				}
			}
		}
	}

	/**
	 * Set on item click listener
	 */
	private void setAction() {
		// hold long click item to delete file
		mLvFile.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View view, int position, long arg3) {
				delete(position);
				return true;
			}
		});
		
		// set on itme click
		mLvFile.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				switch (lvFolderEntities.get(position).getTypeImage()) {
				case Define.TYPE_FOLDER:
					Intent intent = new Intent(TopActivity.this,
							TopActivity.class);
					intent.putExtra(Define.FILE_TYPE, mTypeProcess);
					intent.putExtra(PARENT_ID, lvFolderEntities.get(position)
							.getId());
					SessionData.setmToTopDistance(SessionData.getmToTopDistance() + 1);
					startActivityForResult(intent, Define.NEW_TOP_ACTIVITY);
					SessionData.setFlagActivity(false);
					break;
				case Define.TYPE_FILES:
					Intent intentAudio = new Intent(TopActivity.this, DisplayActivity.class);
					intentAudio
							.putExtra(Define.BOOK_ID, lvFolderEntities.get(position).getBookId());
					intentAudio
							.putExtra(Define.FILE_ID, lvFolderEntities.get(position)
							.getFileId());
					intentAudio.putExtra(Define.LESSON_NAME, lvFolderEntities.get(position)
							.getName().replace(Define.EXTENSION_AUDIO, ""));
					intentAudio.putExtra(Define.IS_SEARCH, true);
					intentAudio
							.putExtra(Define.FILE_TYPE, lvFolderEntities.get(position).getType());
					startActivity(intentAudio);
					break;

				case Define.TYPE_BOOK:
					Intent intentDisplay = new Intent(TopActivity.this,
							DisplayActivity.class);
					intentDisplay.putExtra(Define.BOOK_ID, lvFolderEntities
							.get(position).getBookId());
					intentDisplay.putExtra(Define.FILE_ID, lvFolderEntities
							.get(position).getFileId());
					intentDisplay.putExtra(Define.LESSON_NAME, lvFolderEntities
							.get(position).getName());
					intentDisplay.putExtra(Define.FILE_TYPE, lvFolderEntities
							.get(position).getType());
					intentDisplay.putExtra(Define.IS_SEARCH, true);
					startActivity(intentDisplay);
					break;

				default:
					break;
				}

			}
		});
	}


	/**
	 * Initialization
	 */
	private void init() {
		initDb();
		mContext = this;
	}

	/**
	 * Get and show data
	 */
	public void updateListFolder(int parentID, int typeProcess) {
		DebugOption.info("typeProcess : ", "Type Process = " + typeProcess);

		ArrayList<FolderEntity> lvFolder = new ArrayList<FolderEntity>();
		// get data

		lvFolderEntities.clear();
		lvFolderEntities.addAll(FolderTable.getAllEntityFromParentId(
				SessionData.getDb(), parentID));
		for (int i = 0; i < lvFolderEntities.size(); i++) {
			FolderEntity entity = lvFolderEntities.get(i);
			int countAudio = 0;
			if (mTypeProcess != Define.FileType.TEXT) {
				countAudio = FilesTable.countGetAllAudiosByFolderId(
						SessionData.getDb(), entity.getId());
			}
			DebugOption.info("countAudio", "countAudio = " + countAudio);

			int countBookFile = 0;
			if (mTypeProcess != Define.FileType.AUDIO) {
				countBookFile = FilesTable.countGetBooksFromTextFiles(
						SessionData.getDb(), entity.getId());
			}
			DebugOption.info("countBookFile", "countBookFile = "
					+ countBookFile);

			int countfolder = FolderTable.CountgetAllEntityFromParentId(
					SessionData.getDb(), entity.getId());
			int count = countAudio + countBookFile + countfolder;

			lvFolderEntities.get(i).setName(
					entity.getName() + "(" + count + ")");
		}

		// add List book
		ArrayList<FolderEntity> addListBook = new ArrayList<FolderEntity>();

		// add from book item
		if (mTypeProcess != Define.FileType.AUDIO) {
			ArrayList<FileEntity> addBookes = FilesTable.getBooksFromTextFiles(
					SessionData.getDb(), parentID);
			DebugOption.info("addBookes size :", "addBookes size = "
					+ addBookes.size());
			for (FileEntity bookEntity : addBookes) {
				FolderEntity folEntity = new FolderEntity();
				folEntity.setTypeImage(Define.TYPE_BOOK);
				folEntity.setId(bookEntity.getId());
				folEntity.setName(bookEntity.getName());
				folEntity.setParent_id(parentID);
				folEntity.setBookId(bookEntity.getBook_id());
				folEntity.setFileId(bookEntity.getId());
				folEntity.setType(bookEntity.getType());
				folEntity.setCreated_date(bookEntity.getCreated_time());
				folEntity.setModified_date(bookEntity.getModified_time());
				folEntity.setDeleted_date(bookEntity.getDeleted_time());
				folEntity.setDownloadTime(bookEntity.getDownloaded_time());
				folEntity.setPath(bookEntity.getPath());
				folEntity.setCheck(false);
				addListBook.add(folEntity);
			}
		}

		// }
		ArrayList<FolderEntity> addListAudio = new ArrayList<FolderEntity>();
		if (mTypeProcess != Define.FileType.TEXT) {
			ArrayList<FileEntity> addFiles = FilesTable.getAllAudiosByFolderId(
					SessionData.getDb(), parentID);
			DebugOption.info("addAudioSize size :", "addAudioSize = "
					+ addFiles.size());

			for (FileEntity fileEntity : addFiles) {
				FolderEntity folEntity = new FolderEntity();
				folEntity.setTypeImage(Define.TYPE_FILES);
				folEntity.setId(fileEntity.getId());
				folEntity.setName(fileEntity.getName());
				folEntity.setParent_id(parentID);
				folEntity.setBookId(fileEntity.getBook_id());
				folEntity.setFileId(fileEntity.getId());
				folEntity.setType(fileEntity.getType());
				folEntity.setPath(fileEntity.getPath());
				folEntity.setCreated_date(fileEntity.getCreated_time());
				folEntity.setModified_date(fileEntity.getModified_time());
				folEntity.setDeleted_date(fileEntity.getDeleted_time());
				folEntity.setDownloadTime(fileEntity.getDownloaded_time());
				folEntity.setCheck(false);
				addListAudio.add(folEntity);
			}
		}
		lvFolder.addAll(addListBook);
		lvFolder.addAll(addListAudio);

		// add audioItem

		DebugOption.info("LIST SIZE ",
				"LIST SIZE1 : " + lvFolderEntities.size());
		DebugOption.info("LIST SIZE ", "SESSION SIZE  : "
				+ SessionData.getMoveListItems().size());

		for (FolderEntity entity : SessionData.getMoveListItems()) {
			DebugOption.info("remove", "remove");
			int removeIndex = -1;
			for (int i = 0; i < lvFolderEntities.size(); i++) {
				if (entity.getId() == lvFolderEntities.get(i).getId()
						&& entity.getTypeImage() == lvFolderEntities.get(i)
								.getTypeImage()) {
					removeIndex = i;
				}
			}

			if (removeIndex >= 0) {
				lvFolderEntities.remove(removeIndex);
			}
		}

		// Mod Start 20160106 SonBX
//		if (SessionData.isTitleSort()) {
//			Collections.sort(lvFolder, new TitleComparator());
//		} else {
//			Collections.sort(lvFolder, new DLTimeComparator());
//		}
//
//		lvFolderEntities.addAll(lvFolder);
		// Mod 20160106
		lvFolderEntities.addAll(lvFolder);
		if (SessionData.isTitleSort()) {
			Collections.sort(lvFolderEntities, new TitleComparator());
		} else {
			Collections.sort(lvFolderEntities, new DLTimeComparator());
		}
		// Mod End 20160106 SonBX

		// DebugOption.info("LIST SIZE ",
		// "LIST SIZE2 : " + lvFolderEntities.size());
		// // add item empty
		// int add = 10 - lvFolderEntities.size();
		// if (add < 0) {
		// add = 0;
		// }
		// for (int i = 0; i < add + 3; i++) {
		// lvFolderEntities.add(new FolderEntity());
		// }

		mAdapter.notifyDataSetChanged();

	}

	/**
	 * Check if the APP has been killed before
	 */
	private void checkStopPosition() {
		if ((SharedPreferencesData.getBookId(mContext) != Define.DEFAULT_INT)
				|| !SharedPreferencesData.getAudioPath(mContext).equals(
						Define.DEFAULT_STRING)) {
			Intent intent = new Intent(TopActivity.this, DisplayActivity.class);
			intent.putExtra(Define.HAS_STOP_POSITION, true);
			startActivity(intent);
		}
	}

	/**
	 * Add data for debugging
	 * 
	 * @author KhangDD
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (this.isTaskRoot()) {
			btnBackId.setVisibility(View.GONE);
		}
		switch (requestCode) {

		case Define.NEW_TOP_ACTIVITY: {
			if (SessionData.isFlagActivity() == true && !this.isTaskRoot()) {
				SessionData.setmToTopDistance(SessionData.getmToTopDistance() - 1);
				finish();
				DebugOption.info("FINISH", "FINISH ----" + SessionData.getmToTopDistance());
			}
			break;
		}
		default:
			if (SessionData.getmToTopDistance() > 1 && !this.isTaskRoot()) {
				SessionData.setmToTopDistance(SessionData.getmToTopDistance() - 1);
				SessionData.setFlagActivity(true);
				DebugOption.info("FINISH", "FINISH ----" + SessionData.getmToTopDistance());
				finish();
			}
			break;
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	// on back tab
	public void btnBackTab(View view) {
		finish();
	}

	/**
	 * Initialize DB
	 */
	private void initDb() {
		if (SessionData.getDatabaseManager() == null) {
			SessionData
					.setDatabaseManager(new DatabaseManager(TopActivity.this));
		}
		if (SessionData.getDb() == null) {
			SessionData.setDb(SessionData.getDatabaseManager()
					.getWritableDatabase());
		}
	}

	// delete Folder
	public void delete(final int position) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle(getResources().getString(R.string.delete));
			alert.setMessage(getResources().getString(R.string.are_you_sure_delete_folder));

			alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// delete database
				FolderEntity entity = lvFolderEntities.get(position);
				if (entity.getTypeImage() == Define.TYPE_FOLDER) {
					listDeleteFolder(entity.getId());
				} else if (entity.getTypeImage() == Define.TYPE_BOOK) {
					new ProgressDelete(TopActivity.this)
							.execute(entity.getId());
				} else if (entity.getTypeImage() == Define.TYPE_FILES) {
					// delete in Database
					FilesTable.delete(SessionData.getDb(),
							entity.getId());
					// delete in sdcard
					DebugOption.info("PATH : ", "PATH = "
							+ entity.getPath());
					FileIO.deleteFile(entity.getPath());
					// delete listview

					setResult(RESULT_OK);
				}
				if (mListDeleteFolder.size() > 0) {
					new ProgressDeleteFolder(TopActivity.this).execute(mListDeleteFolder);
				}

				updateListFolder(mParentID, Define.TYPE_FOLDER);

			}
			});

			alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Canceled.
				}
			});
			alert.show();
		}

	// Delete Progress ID
	private class ProgressDelete extends AsyncTask<Integer, Void, Void> {
		private ProgressDialog progress;
		private Context mContext;

		public ProgressDelete(Context context) {
			mContext = context;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progress = new ProgressDialog(mContext);
			progress.setMessage(getResources().getString(R.string.please_wait));
			progress.setCancelable(false);
			progress.show();
		}

		@Override
		protected Void doInBackground(Integer... params) {
			int id = params[0];
			ArrayList<FileEntity> arr = FilesTable.getAllTextFromBookId(SessionData.getDb(), id);

			DebugOption.info("ARR SIZE :", arr.size() + "");

			for (FileEntity fileEntity : arr) {
				DebugOption.info("delete ID : ", "Delete Id = " + fileEntity.getId());
				FilesTable.delete(SessionData.getDb(), fileEntity.getId());
				FileIO.deleteFile(fileEntity.getPath());
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			progress.dismiss();
			updateListFolder(mParentID, Define.TYPE_FOLDER);
			mAdapter.setCheckVisible(false);
			mAdapter.notifyDataSetChanged();
			setResult(RESULT_OK);
		}
	}

	public void listDeleteFolder(int id) {
		mListDeleteFolder.add(id);
		ArrayList<Integer> arr = FolderTable.getAllIdFromParentId(SessionData.getDb(), id);
		if (arr.size() > 0) {
			for (Integer i : arr) {
				listDeleteFolder(i);
			}
		}
	}

	// Delete Progress ID
	private class ProgressDeleteFolder extends AsyncTask<List<Integer>, Void, Void> {
		private ProgressDialog progress;
		private Context mContext;

		public ProgressDeleteFolder(Context context) {
			mContext = context;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progress = new ProgressDialog(mContext);
			progress.setMessage(getResources().getString(R.string.please_wait));
			progress.setCancelable(false);
			progress.show();
		}

		@Override
		protected Void doInBackground(List<Integer>... params) {
			List<Integer> list = params[0];
			for (Integer i : list) {
				deleteFolderId(i);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			mListDeleteFolder.clear();
			progress.dismiss();
			updateListFolder(mParentID, Define.TYPE_FOLDER);
			mAdapter.notifyDataSetChanged();
			// actionId.setText(getResources().getString(R.string.action));
			setResult(RESULT_OK);
		}
	}

	public void deleteFolderId(int id) {
		ArrayList<FileEntity> arr = FilesTable.getAllFromFolderId(SessionData.getDb(), id);

		DebugOption.info("ARR SIZE :", arr.size() + "");

		for (FileEntity fileEntity : arr) {
			DebugOption.info("delete ID : ", "Delete Id = " + fileEntity.getId());
			FilesTable.delete(SessionData.getDb(), fileEntity.getId());
			FileIO.deleteFile(fileEntity.getPath());
		}
		FolderTable.delete(SessionData.getDb(), id);
	}

	@Override
	public void sort(boolean flag) {
		DebugOption.info(" FLAG : ", " FLAG : " + flag);

		if (flag) {
			Collections.sort(lvFolderEntities, new TitleComparator());
			mAdapter.notifyDataSetChanged();
		} else {
			Collections.sort(lvFolderEntities, new DLTimeComparator());
			mAdapter.notifyDataSetChanged();
		}

	}

}
