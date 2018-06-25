package jp.co.marinax.fileplayer.ui.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.co.marinax.fileplayer.R;
import jp.co.marinax.fileplayer.app.config.DebugOption;
import jp.co.marinax.fileplayer.app.config.Define;
import jp.co.marinax.fileplayer.io.db.table.BookTable;
import jp.co.marinax.fileplayer.io.db.table.FilesTable;
import jp.co.marinax.fileplayer.io.db.table.FolderTable;
import jp.co.marinax.fileplayer.io.entity.FileEntity;
import jp.co.marinax.fileplayer.io.entity.FolderEntity;
import jp.co.marinax.fileplayer.io.save.SessionData;
import jp.co.marinax.fileplayer.ui.activity.base.BaseActivity;
import jp.co.marinax.fileplayer.utils.FileIO;
import jp.co.marinax.fileplayer.utils.ShowDialogUtils;
import jp.co.marinax.fileplayer.utils.TimeUtils;
import jp.co.marinax.fileplayer.view.adapter.FolderAdapter;
import jp.co.marinax.fileplayer.view.adapter.FolderAdapter.MoveClass;
import jp.co.marinax.fileplayer.view.adapter.FolderAdapter.UpdateList;
import jp.co.marinax.fileplayer.view.custom.DLTimeComparator;
import jp.co.marinax.fileplayer.view.custom.MenuBottomUpCus;
import jp.co.marinax.fileplayer.view.custom.MenuBottomUpCus.MenuClick;
import jp.co.marinax.fileplayer.view.custom.MenuBottomUpCus.ShowHide;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.testflightapp.lib.TestFlight;

public class FolderActivity extends BaseActivity implements MenuClick,
		UpdateList, MoveClass, ShowHide {
	private String TAG = FolderActivity.class.getSimpleName();
	View view;
	private boolean flag = true;
	MenuBottomUpCus menuBottom;
	private ListView mLv;
	private FolderAdapter mAdapter;
	private TextView mBeforeParentId;
	private TextView folderName;
	private ImageView backBtn;
	private ArrayList<FolderEntity> lvFolderEntities = new ArrayList<FolderEntity>();
	private Button actionId;
	private int mLastPosition;
	private int mParentID = 0;
	private int mTypeProcess = Define.TYPE_FOLDER;
	public static String PARENT_ID = "parent_id";
	public static String TYPE_PROCESS = "type_process";
	public static String MOVE_FLAG = "move_flag";
	private List<Integer> mListDeleteFolder = new ArrayList<Integer>();

	Button btnChangeFileName;

	// private Button cancelId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TestFlight.passCheckpoint(TAG);
		TestFlight.log("come in " + TAG);
		setContentView(R.layout.activity_folder);
		mLv = (ListView) findViewById(R.id.lvFolderId);
		mAdapter = new FolderAdapter(FolderActivity.this, lvFolderEntities);
		mLv.setAdapter(mAdapter);
		menuBottom = (MenuBottomUpCus) findViewById(R.id.menu_bottom);
		actionId = (Button) findViewById(R.id.actionId);
		// cancelId = (Button) findViewById(R.id.cancelId);
		mParentID = getIntent().getIntExtra(PARENT_ID, 0);
		folderName = (TextView) findViewById(R.id.folderName);
		backBtn = (ImageView) findViewById(R.id.backOnclick);
		mTypeProcess = getIntent()
				.getIntExtra(TYPE_PROCESS, Define.TYPE_FOLDER);
		mBeforeParentId = (Button) findViewById(R.id.beforeParentId);
		btnChangeFileName = (Button) findViewById(R.id.btnChangeFileName);
		DebugOption.info("mParentID : ", "mParentID = " + mParentID);
		updateListFolder(mParentID, Define.TYPE_FOLDER);
		setAction();
		
//		if(mParentID != Define.DEFAULT_INT) {
//			FolderEntity folder = FolderTable.getEntity(SessionData.getDb(), mParentID);
//			DebugOption.info("Folder", "Temp name: " + folder.getTemp_name() + " Can redownload: " + folder.getCan_redownload());
//			if(!folder.getTemp_name().equals(Define.DEFAULT_STRING) && (folder.getCan_redownload() == Define.DEFAULT_INT)) {
//				// Because of having some audio files deleted, this folder needed to be re-download
//				FolderTable.setCanReDownload(SessionData.getDb(), folder.getId(), 1);
//			}
//		}
	}

	// set action menu and ListView onClick
	public void setAction() {

		mLv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				FolderEntity entity = lvFolderEntities.get(position);
				entity.setCheck(true);

				int selectedItem = 0;
				for (FolderEntity enFolder : lvFolderEntities) {
					if (enFolder.isCheck()) {
						selectedItem++;
					}
				}
				DebugOption.debug("selectedItem", "selectedItem = " + selectedItem);
				SessionData.setSelectedItem(selectedItem);

				menuBottom.MenuUp();
				menuBottom.setMoveVisibleOrHide(true);
				flag = false;
				mAdapter.setCheckVisible(true);
				mAdapter.notifyDataSetChanged();
				return false;
			}
		});

		mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				// DebugOption.info("item list onclick", "item list onclick");
				if (lvFolderEntities.get(position).getTypeImage() == Define.TYPE_FOLDER) {
					int mParentBefore = mParentID;
					mParentID = lvFolderEntities.get(position).getId();
					mTypeProcess = lvFolderEntities.get(position)
							.getTypeImage();
					// updateListFolder(mParentID);
					Intent intent = new Intent(FolderActivity.this,
							FolderActivity.class);
					intent.putExtra(PARENT_ID, mParentID);
					intent.putExtra(TYPE_PROCESS, mTypeProcess);
					startActivityForResult(intent, mParentBefore);
				} else {
					// do nothing
				}
			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();
		// update status of move
		DebugOption.info("mParentID : ", "mParentID = " + mParentID);
		if (SessionData.getMoveListItems().size() == 0) {
			menuBottom.setMoveLabel(getResources().getString(R.string.move));
			menuBottom.setMoveVisibleOrHide(false);
			boolean move = false;
			for (FolderEntity entity : lvFolderEntities) {
				if (entity.isCheck()) {
					move = true;
					break;
				}
			}
			menuBottom.setMoveVisibleOrHide(move);
			// actionId.setText(getResources().getString(R.string.action));
		} else {
			String str = "Paste ";
			int size = SessionData.getMoveListItems().size();
			str = size + getResources().getString(R.string.move_items_here);

			menuBottom.setMoveLabel(str);
			menuBottom.setMoveVisibleOrHide(true);
			// actionId.setText(getResources().getString(R.string.done));
		}

		if (mParentID == 0) {
			mBeforeParentId.setVisibility(View.GONE);
			folderName.setText(getResources().getString(R.string.folder_process));
			backBtn.setVisibility(View.VISIBLE);
		} else {
			backBtn.setVisibility(View.GONE);
			mBeforeParentId.setVisibility(View.VISIBLE);
			int beforeParentId = FolderTable.getParentIdFromChildID(
					SessionData.getDb(), mParentID);
			if (beforeParentId == 0) {
				mBeforeParentId
						.setText(getResources().getString(R.string.back));
			} else {
				FolderEntity entity = FolderTable.getEntity(
						SessionData.getDb(), beforeParentId);
				mBeforeParentId.setText(entity.getName());
			}

			FolderEntity folder = FolderTable.getEntity(SessionData.getDb(),
					mParentID);
			folderName.setText(folder.getName());
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		DebugOption.info("ActivityForResult", "ActivityForResult "
				+ requestCode);

		DebugOption.info("ActivityForResult", "ActivityForResult "
				+ requestCode);
		mParentID = requestCode;
		updateListFolder(mParentID, Define.TYPE_FOLDER);

		super.onActivityResult(requestCode, resultCode, data);
	}

	// update List Folder
	public void updateListFolder(int parentID, int typeProcess) {

		// get data
		if (typeProcess == Define.TYPE_FOLDER) {
			lvFolderEntities.clear();
			lvFolderEntities.addAll(FolderTable.getAllEntityFromParentId(
					SessionData.getDb(), parentID));
			for (int i = 0; i < lvFolderEntities.size(); i++) {
				FolderEntity entity = lvFolderEntities.get(i);
				int countAudio = FilesTable.countGetAllAudiosByFolderId(
						SessionData.getDb(), entity.getId());
				DebugOption.info("countAudio", "countAudio = " + countAudio);

				int countBookFile = FilesTable.countGetBooksFromTextFiles(
						SessionData.getDb(), entity.getId());
				DebugOption.info("countBookFile", "countBookFile = "
						+ countBookFile);

				int countfolder = FolderTable.CountgetAllEntityFromParentId(
						SessionData.getDb(), entity.getId());
				int count = countAudio + countBookFile + countfolder;

				lvFolderEntities.get(i).setName(
						entity.getName() + "(" + count + ")");
			}

			mLastPosition = lvFolderEntities.size();

			ArrayList<FolderEntity> lvAdd = new ArrayList<FolderEntity>();
			// add List book
			ArrayList<FolderEntity> addListBook = new ArrayList<FolderEntity>();

			// add from book item
			ArrayList<FileEntity> addBookes = FilesTable.getBooksFromTextFiles(
					SessionData.getDb(), mParentID);
			DebugOption.info("addBookes size :", "addBookes size = "
					+ addBookes.size());
			for (FileEntity bookEntity : addBookes) {
				FolderEntity folEntity = new FolderEntity();
				folEntity.setTypeImage(Define.TYPE_BOOK);
				folEntity.setId(bookEntity.getId());
				folEntity.setName(bookEntity.getName());
				folEntity.setParent_id(mParentID);
				folEntity.setBookId(bookEntity.getBook_id());
				folEntity.setDownloadTime(bookEntity.getDownloaded_time());
				folEntity.setCreated_date(bookEntity.getCreated_time());
				folEntity.setModified_date(bookEntity.getModified_time());
				folEntity.setDeleted_date(bookEntity.getDeleted_time());
				folEntity.setCheck(false);
				addListBook.add(folEntity);
			}

			// }
			ArrayList<FolderEntity> addListAudio = new ArrayList<FolderEntity>();
			ArrayList<FileEntity> addFiles = FilesTable.getAllAudiosByFolderId(
					SessionData.getDb(), mParentID);
			DebugOption.info("addAudioSize size :", "addAudioSize = "
					+ addFiles.size());

			for (FileEntity fileEntity : addFiles) {
				FolderEntity folEntity = new FolderEntity();
				folEntity.setTypeImage(Define.TYPE_FILES);
				folEntity.setId(fileEntity.getId());
				folEntity.setName(fileEntity.getName());
				folEntity.setParent_id(mParentID);
				folEntity.setBookId(fileEntity.getBook_id());
				folEntity.setPath(fileEntity.getPath());
				folEntity.setDownloadTime(fileEntity.getDownloaded_time());
				folEntity.setCreated_date(fileEntity.getCreated_time());
				folEntity.setModified_date(fileEntity.getModified_time());
				folEntity.setDeleted_date(fileEntity.getDeleted_time());
				folEntity.setCheck(false);
				addListAudio.add(folEntity);
			}
			lvAdd.addAll(addListBook);
			lvAdd.addAll(addListAudio);
			Collections.sort(lvAdd, new DLTimeComparator());

			lvFolderEntities.addAll(lvAdd);
		}
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

		if (lvFolderEntities.size() > 0) {
			menuBottom.isShowSelectAndDeSelect(true);
		} else {
			menuBottom.isShowSelectAndDeSelect(false);
			menuBottom.setMoveVisibleOrHide(false);
		}
		DebugOption.info("LIST SIZE ",
				"LIST SIZE2 : " + lvFolderEntities.size());
		// add item empty
		int add = 10 - lvFolderEntities.size();
		if (add < 0) {
			add = 0;
		}
		for (int i = 0; i < add + 3; i++) {
			lvFolderEntities.add(new FolderEntity());
		}

		// check is status paste
		if (SessionData.getMoveListItems().size() > 0) {
			menuBottom.setGoneDelteLayout(true);
			menuBottom.MenuUp();
			flag = false;
			mAdapter.setCheckVisible(false);
		} else {
			menuBottom.setGoneDelteLayout(false);
			menuBottom.MenuDown();
			flag = true;
			mAdapter.setCheckVisible(false);
		}
		mAdapter.notifyDataSetChanged();
	}

	public boolean checkExistId(ArrayList<FolderEntity> lvfolder, int id) {
		boolean check = false;
		for (FolderEntity entity : lvfolder) {
			if (entity.getId() == id) {
				check = true;
				break;
			}
		}
		return check;
	}

	/****************************************
	 * onclick event
	 ***************************************/
	public void backOnclick(View view) {
		if (mParentID == 0) {
			SessionData.clearMoveItems();
		}
		finish();
	}

	public void actionOnclick(View view) {
		action();
	}

	public void action() {
		if (flag) {
			// actionId.setText(getResources().getString(R.string.done));
			mAdapter.setCheckVisible(true);
			mAdapter.notifyDataSetChanged();
			menuBottom.MenuUp();
			flag = false;
		} else {
			// actionId.setText(getResources().getString(R.string.action));
			mAdapter.setCheckVisible(false);
			mAdapter.notifyDataSetChanged();
			menuBottom.MenuDown();
			flag = true;
		}
	}

	public void selectAll() {
		if (mAdapter.isShow()) {
			// check virtual list
			boolean moveCondition = false;
			for (FolderEntity entity : lvFolderEntities) {
				if (entity.getId() != Define.DEFAULT_INT) {
					moveCondition = true;
				}
			}
			// check condition
			if (moveCondition) {
				DebugOption.info("selectAll", "selectAll");
				for (FolderEntity entity : lvFolderEntities) {
					if (entity.getId() != Define.DEFAULT_INT) {
						entity.setCheck(true);
					}
				}
				mAdapter.notifyDataSetChanged();
				menuBottom.setMoveVisibleOrHide(true);
			}
		}
	}

	// open Folder
	public void newFolder(int parentID) {
		DebugOption.info("newFolder", "newFolder");
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		// alert.setTitle(getResources().getString(R.string.create_a_folder));
		alert.setMessage(getResources().getString(R.string.enter_folder_name));
		// Set an EditText view to get user input
		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String value = input.getText().toString();
				if (!value.replace(" ", "").equals(Define.DEFAULT_STRING)) {
						ArrayList<FolderEntity> folList = FolderTable
								.getAllEntityFromParentId(SessionData.getDb(),
										mParentID);

						if (checkExistFolder(folList, value)) {
							ShowDialogUtils.showDialog(FolderActivity.this,
								getResources().getString(R.string.folder_exist));
						} else {
							// Mod Start 20151008 SonBX
//							FolderEntity entity = new FolderEntity(value, 0,
//									TimeUtils.getCurrenttime(), "", "");
							// Mod 20151008
							FolderEntity entity = new FolderEntity(value, 0,
									TimeUtils.getCurrenttime(), "", "", "", 0);
							// Mod End 20151008 SonBX
							long id = FolderTable.newFolder(
									SessionData.getDb(), entity, mParentID);
							lvFolderEntities.add(mLastPosition, FolderTable
									.getEntity(SessionData.getDb(), (int) id));
							mLastPosition++;
							updateListFolder(mParentID, Define.TYPE_FOLDER);
						}
				} else {
					ShowDialogUtils.showDialog(FolderActivity.this,
							getResources().getString(R.string.please_input_name));
				}
					}
				});

		alert.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
					}
				});

		alert.show();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		mParentID = intent.getIntExtra(PARENT_ID, 0);
		// actionId.setText(getResources().getString(R.string.done));
		updateListFolder(mParentID, Define.TYPE_FOLDER);
		// setAction();
	}

	public void move() {
		DebugOption.info("MOVE", "MOVE");
		if (SessionData.getMoveListItems().size() == 0) {
			// save folder to choose
			ArrayList<FolderEntity> folderChose = new ArrayList<FolderEntity>();
			for (FolderEntity entity : lvFolderEntities) {
				if (entity.isCheck()) {
					folderChose.add(entity);
				}
			}
			SessionData.setMoveListItems(folderChose);
			Intent intent = new Intent(this, FolderActivity.class);
			intent.putExtra(PARENT_ID, mParentID);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
		} else {
			// change folderId all to this mParentId
			DebugOption.info("MOVE ITEMS", "MOVE ITEMS");
			ArrayList<FolderEntity> list = SessionData.getMoveListItems();
			DebugOption.info("SIZE : ", "SIZE = " + list.size());
			for (FolderEntity entity : list) {
				if (entity.getTypeImage() == Define.TYPE_FOLDER) {
					FolderTable.changeNewFolderId(SessionData.getDb(),
							entity.getId(), mParentID);
				} else if (entity.getTypeImage() == Define.TYPE_BOOK) {
					ArrayList<FileEntity> arr = FilesTable
							.getAllTextFromBookId(SessionData.getDb(),
									entity.getBookId());
					for (int i = 0; i < arr.size(); i++) {
						FilesTable.changeNewFolderId(SessionData.getDb(), arr
								.get(i).getId(), mParentID);
					}
				} else if (entity.getTypeImage() == Define.TYPE_FILES) {
					FilesTable.changeNewFolderId(SessionData.getDb(),
							entity.getId(), mParentID);
				}
			}

			SessionData.clearMoveItems();
			menuBottom.setMoveLabel(getResources().getString(R.string.move));
			menuBottom.setMoveVisibleOrHide(false);
			updateListFolder(mParentID, Define.TYPE_FOLDER);
		}
	}

	public void deSelectAll() {
		if (mAdapter.isShow()) {
			DebugOption.info("selectAll", "selectAll");
			for (FolderEntity entity : lvFolderEntities) {
				entity.setCheck(false);
			}
			mAdapter.notifyDataSetChanged();
			menuBottom.setMoveVisibleOrHide(false);
		}
	}

	public void delete() {

		boolean deleteFlag = false;
		for (FolderEntity entity : lvFolderEntities) {
			if (entity.isCheck()) {
				deleteFlag = true;
				break;
			}
		}

		if (deleteFlag) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle(getResources().getString(R.string.delete));
			alert.setMessage(getResources().getString(
					R.string.are_you_sure_delete_folder));

			alert.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							DebugOption.info("delete", "delete");
							for (final FolderEntity entity : lvFolderEntities) {
								if (entity.isCheck()) {
									if (entity.getTypeImage() == Define.TYPE_FOLDER) {
										listDeleteFolder(entity.getId());
									} else if (entity.getTypeImage() == Define.TYPE_BOOK) {
										DebugOption.debug(TAG, "Delete a book");
										// Mod Start 20151228 SonBX
//										new ProgressDelete(FolderActivity.this)
//										.execute(entity.getId());
										// Mod 20151228
										new ProgressDelete(FolderActivity.this)
												.execute(entity.getBookId());
										// Mod End 20151228 SonBX
									} else if (entity.getTypeImage() == Define.TYPE_FILES) {
										// delete in Database
										FilesTable.delete(SessionData.getDb(),
												entity.getId());
										// New Start 20151009 SonBX
										if(entity.getParent_id() != Define.DEFAULT_INT) {
											FolderEntity folder = FolderTable.getEntity(SessionData.getDb(), entity.getParent_id());
											DebugOption.info("Folder", "Temp name: " + folder.getTemp_name() + " Can redownload: " + folder.getCan_redownload());
											if(!folder.getTemp_name().equals(Define.DEFAULT_STRING) && (folder.getCan_redownload() == Define.DEFAULT_INT)) {
												// Because of having some audio files deleted, this folder needed to be re-download
												FolderTable.setCanReDownload(SessionData.getDb(), folder.getId(), 1);
											}
										}
										// New End 20151009 SonBX
										// delete in sdcard
										DebugOption.info("PATH : ", "PATH = "
												+ entity.getPath());
										FileIO.deleteFile(entity.getPath());
									}
								}
							}
							if (mListDeleteFolder.size() > 0) {
								new ProgressDeleteFolder(FolderActivity.this)
										.execute(mListDeleteFolder);
							}

							updateListFolder(mParentID, Define.TYPE_FOLDER);
							mAdapter.setCheckVisible(false);
							mAdapter.notifyDataSetChanged();
							menuBottom.MenuDown();
							menuBottom.setMoveVisibleOrHide(false);
							flag = true;
							setResult(RESULT_OK);
						}
					});

			alert.setNegativeButton(R.string.cancel,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							// Canceled.
						}
					});
			alert.show();
		}
	}

	@Override
	public void ClickItem(int id) {
		switch (id) {
		case Define.SELECT_ALL:
			selectAll();
			break;
		case Define.NEW_FOLDER:
			newFolder(mParentID);
			break;
		case Define.MOVE:
			move();
			break;
		case Define.DESELECT_ALL:
			deSelectAll();
			break;
		case Define.DELETE:
			delete();
			break;
		case Define.RLDELETE:
			action();
			break;
		case Define.CANCEL:
			cancel();
			break;
			case Define.CHANGE_FILE_NAME:
				changeFileName();
				break;
		default:
			break;
		}
	}

	@Override
	public void update(List<FolderEntity> arrFolder) {
		lvFolderEntities.clear();
		lvFolderEntities.addAll(arrFolder);
	}

	public void cancel() {
		DebugOption.info("cancel", "cancel");
		SessionData.clearMoveItems();
		menuBottom.setMoveLabel(getResources().getString(R.string.move));
		updateListFolder(mParentID, Define.TYPE_FOLDER);
	}

	// check exist
	public boolean checkExistFolder(ArrayList<FolderEntity> list, String name) {
		boolean check = false;
		for (FolderEntity entity : list) {
			if (entity.getName().equals(name)) {
				check = true;
				break;
			}
		}
		return check;
	}

	@Override
	public void onBackPressed() {
		if (mParentID == 0) {
			SessionData.clearMoveItems();
		}
		super.onBackPressed();

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
			ArrayList<FileEntity> arr = FilesTable.getAllTextFromBookId(
					SessionData.getDb(), id);

			DebugOption.info("ARR SIZE :", arr.size() + "");

			for (FileEntity fileEntity : arr) {
				DebugOption.info("delete ID : ",
						"Delete Id = " + fileEntity.getId());
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
			menuBottom.MenuDown();
			flag = true;
			setResult(RESULT_OK);
		}
	}

	// Delete Progress ID
	private class ProgressDeleteFolder extends
			AsyncTask<List<Integer>, Void, Void> {
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
			mAdapter.setCheckVisible(false);
			mAdapter.notifyDataSetChanged();
			menuBottom.MenuDown();
			flag = true;
			// actionId.setText(getResources().getString(R.string.action));
			setResult(RESULT_OK);
		}
	}

	public void deleteFolderId(int id) {
		ArrayList<FileEntity> arr = FilesTable.getAllFromFolderId(
				SessionData.getDb(), id);

		DebugOption.info("ARR SIZE :", arr.size() + "");

		for (FileEntity fileEntity : arr) {
			DebugOption.info("delete ID : ",
					"Delete Id = " + fileEntity.getId());
			FilesTable.delete(SessionData.getDb(), fileEntity.getId());
			FileIO.deleteFile(fileEntity.getPath());
		}
		FolderTable.delete(SessionData.getDb(), id);
	}

	public void listDeleteFolder(int id) {
		mListDeleteFolder.add(id);
		ArrayList<Integer> arr = FolderTable.getAllIdFromParentId(
				SessionData.getDb(), id);
		if (arr.size() > 0) {
			for (Integer i : arr) {
				listDeleteFolder(i);
			}
		}
	}

	@Override
	public void moveFlag(boolean moveflag) {
		DebugOption.info("flag : ", "flag : " + moveflag);
		menuBottom.setMoveVisibleOrHide(moveflag);

		int count = 0;
		for (int i = 0; i < lvFolderEntities.size(); i++) {
			FolderEntity entity = lvFolderEntities.get(i);
			if (entity.isCheck()) {
				count++;
			}
		}

		enableChangeFileNameButton(count == 1);
	}

	@Override
	public void setShowHideDone(boolean flag) {
		if (flag) {
			actionId.setText(getResources().getString(R.string.done));
		} else {
			actionId.setText(getResources().getString(R.string.action));
		}
	}

	public void changeFileName() {
		int count = 0;
		int index = -1;
		for (int i = 0; i < lvFolderEntities.size(); i++) {
			FolderEntity entity = lvFolderEntities.get(i);
			if (entity.isCheck()) {
				index = i;
				count++;
				Log.e("Folder: " + i, entity.toString());
			}
		}
		// TODO
		if(count!=1) {
			enableChangeFileNameButton(false);
			return;
		}
		
		final FolderEntity entity = lvFolderEntities.get(index);
		String filename = entity.getName();// .replace(Define.EXTENSION_AUDIO, "");

		// show dialog to change file name
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.dialog_change_filename, null);
		final EditText et = (EditText) v.findViewById(R.id.etFileName);
		et.setText(filename);
		builder.setView(v)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// change file name
						String newFilename = et.getText().toString();
						// TODO update to database
						entity.setName(newFilename);
						if (Define.TYPE_BOOK == entity.getTypeImage() || Define.TYPE_FILES == entity.getTypeImage()) {
							FilesTable.updateFilename(SessionData.getDb(), entity.getId(), newFilename);
							// New Start 20151228 SonBX
							if(Define.TYPE_BOOK == entity.getTypeImage()) {
								BookTable.changeDisplayName(SessionData.getDb(), entity.getBookId(), newFilename);
							}
							// New End 20151228 SonBX
						} else {
							String name = newFilename;
							if (name.contains("(")) {
								name = name.substring(0, name.indexOf("("));
							}
							FolderTable.updateFilename(SessionData.getDb(), entity.getId(), name);
						}

						if (mAdapter != null) {
							mAdapter.notifyDataSetChanged();
						}
					}
				})
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// User cancelled the dialog
						// do nothing
					}
				});
		// Create the AlertDialog object and return it
		builder.create().show();
	}

	public void enableChangeFileNameButton(boolean enable) {
		Button btnChangeFileName = (Button) findViewById(R.id.btnChangeFileName);
		if (enable) {
			btnChangeFileName.setBackgroundResource(R.drawable.menu_item);
			btnChangeFileName.setEnabled(enable);
		} else {
			btnChangeFileName.setBackgroundResource(R.drawable.menu_item_disable);
			btnChangeFileName.setEnabled(enable);
		}
	}
}
