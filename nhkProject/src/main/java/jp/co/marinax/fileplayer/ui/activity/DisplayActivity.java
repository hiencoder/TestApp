package jp.co.marinax.fileplayer.ui.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.co.marinax.fileplayer.R;
import jp.co.marinax.fileplayer.app.config.DebugOption;
import jp.co.marinax.fileplayer.app.config.Define;
import jp.co.marinax.fileplayer.app.media.AudioCallback;
import jp.co.marinax.fileplayer.app.media.AudioService;
import jp.co.marinax.fileplayer.app.media.AudioService.AudioIBinder;
import jp.co.marinax.fileplayer.io.db.table.BookTable;
import jp.co.marinax.fileplayer.io.db.table.FilesTable;
import jp.co.marinax.fileplayer.io.entity.BookEntity;
import jp.co.marinax.fileplayer.io.entity.FileEntity;
import jp.co.marinax.fileplayer.io.save.SessionData;
import jp.co.marinax.fileplayer.io.save.SharedPreferencesData;
import jp.co.marinax.fileplayer.ui.activity.base.BaseActivity;
import jp.co.marinax.fileplayer.utils.Decompress;
import jp.co.marinax.fileplayer.utils.EncodeAndDecodeUtils;
import jp.co.marinax.fileplayer.utils.FileIO;
import jp.co.marinax.fileplayer.view.adapter.DisplayActivityAdapter;
import jp.co.marinax.fileplayer.view.adapter.DisplayActivityAdapter.TabText;
import jp.co.marinax.fileplayer.view.custom.DisplayActivityCustom;
import jp.co.marinax.fileplayer.view.custom.DisplayActivityCustom.TopBar;
import jp.co.marinax.fileplayer.view.custom.MyViewPager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.testflightapp.lib.TestFlight;

/**
 * Display a Book
 * 
 * @author SONBX
 * 
 */
public class DisplayActivity extends BaseActivity implements TopBar, OnSeekBarChangeListener,
		TabText, AudioCallback {
	// b1 Decryption and Unzip data
	private ProgressDialog progressDecrypt;
	private int mCurrentPlayIndex = 0;
	private int totalAudio = 0;
	private List<String> lvAudioPath = new ArrayList<String>();

	private String TAG = DisplayActivity.class.getSimpleName();
	public static boolean sActive = false;
	public static boolean mFlag = true;

	/** Book ID */
	private int mBookId;

	/** Audio file's path */
	private String mAudioPath;

	/** Context */
	private Context mContext;

	private SeekBar mSeekBar;
	private SeekBar mSeekBarMusic;
	private int beforePager;
	// private MediaPlayer mp;
	private Handler mHandler = new Handler();
	DisplayActivityCustom displayActivityCustom;
	PagerAdapter adapter;
	MyViewPager pager;
	private ImageView mImgPause;
	private boolean checkExistAudio = false;
	private Button noAudioId;
	private boolean checkVisible = true;
	private RelativeLayout rlTopId;
	private RelativeLayout llProgressId;
	private LinearLayout llNoText;
	private RelativeLayout rlAudioId;
	private TextView mLessonName;
	private TextView mTvPagerPosition;
	Bundle extras;

	private boolean isBounded = false;
	private AudioService mAudioService;
	private ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			isBounded = false;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			AudioIBinder iBinder = (AudioIBinder) service;
			mAudioService = iBinder.getService();
			mAudioService.setAudioCallback(DisplayActivity.this);
			isBounded = true;
			if (extras != null) {
				int bookId = extras.getInt(Define.BOOK_ID);
				BookEntity entity = BookTable.getEntity(SessionData.getDb(), bookId);
				DebugOption.info("GET NAME", "GET NAME" + entity.getName());
				String encryptPath = SessionData.getFolderApp() + "/" + entity.getName()
						+ Define.EXTENSION_TEXT;
				String[] paStrings = { encryptPath };
				new DeleteFileTempBefore().execute(paStrings);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TestFlight.passCheckpoint(TAG);
		TestFlight.log("come in " + TAG);
		setContentView(R.layout.activity_display);
		mTvPagerPosition = (TextView) findViewById(R.id.tvPagerPosition);
		progressDecrypt = new ProgressDialog(this);
		extras = getIntent().getExtras();
		// Find views
		findView();
		// Initialization
		init();
		/*if (extras != null) {
			int bookId = extras.getInt(Define.BOOK_ID);
			BookEntity entity = BookTable.getEntity(SessionData.getDb(), bookId);
			DebugOption.info("GET NAME", "GET NAME" + entity.getName());
			String encryptPath = SessionData.getFolderApp() + "/" + entity.getName()
					+ Define.EXTENSION_TEXT;
			String[] paStrings = { encryptPath };
			new DeleteFileTempBefore().execute(paStrings);
		}*/
		Intent serviceIntent = new Intent(this, AudioService.class);
		bindService(serviceIntent, mServiceConnection, Service.BIND_AUTO_CREATE);
	}

	public void loadData() {
		if (extras != null) {
			boolean hasStopPosition = extras.getBoolean(Define.HAS_STOP_POSITION);
			if (hasStopPosition) {
				showBookFromStopPosition();
			} else {
				int fileType = extras.getInt(Define.FILE_TYPE);
				boolean isSearch = extras.getBoolean(Define.IS_SEARCH);

				// If text
				if (fileType == Define.FileType.TEXT) {
					mBookId = extras.getInt(Define.BOOK_ID);
					DebugOption.info(TAG, "Show text, Book_id: " + mBookId);
					showText(getListTextFiles(mBookId));
					if (isSearch) {
						List<String> listAudio = new ArrayList<String>();
						List<FileEntity> lvFile = FilesTable.getAllAudiosByBookId(
								SessionData.getDb(), mBookId);
						DebugOption.info("SIZE", "SIZE = " + lvFile.size());

						for (FileEntity file : lvFile) {
							DebugOption
									.info("file.getPath()", "file.getPath() = " + file.getPath());
							listAudio.add(file.getPath());
						}

						if (listAudio.size() > 0) {
							showAudioList(listAudio);
						}

					}
				} else if (fileType == Define.FileType.AUDIO) {
					int fileId = extras.getInt(Define.FILE_ID);
					DebugOption.info(TAG, "Show audio, File_id: " + fileId);
					FileEntity entity = FilesTable.getEntity(SessionData.getDb(), fileId);
					String path = entity.getPath();

					List<String> listAudio = new ArrayList<String>();

					List<FileEntity> lvFile = FilesTable.getAllAudiosFromBookId(
							SessionData.getDb(), entity.getBook_id());

					for (FileEntity enti : lvFile) {
						DebugOption.info("Link", " link: " + enti.getPath());
						listAudio.add(enti.getPath());
					}
					// Get start index
					for (int i = 0; i < listAudio.size(); i++) {
						if (listAudio.get(i).compareTo(path) == 0) {
							mCurrentPlayIndex = i;
							break;
						}
					}
					
					DebugOption.info("PATH AUDIO : ", "PATH AUDIO : " + entity.getPath());
					showAudioList(listAudio);

					if (isSearch) {
						// Find matching text files
						mBookId = extras.getInt(Define.BOOK_ID);
						showText(getListTextFiles(mBookId));
					}
				}
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		// if (mp.isPlaying()) {
		// mp.pause();
		// mFlag = true;
		// } else {
		// mFlag = false;
		// }

		// Save stop position
		// DebugOption.info(TAG, "Save stop position");
		// SharedPreferencesData.setBookId(mBookId, mContext);
		// SharedPreferencesData.setAudioPath(mAudioPath, mContext);
		// SharedPreferencesData.setAudioProgress(mp.getCurrentPosition(),
		// mContext);

	}

	@Override
	protected void onResume() {
		super.onResume();
		// if (mFlag) {
		// mp.start();
		// }

	}

	@Override
	protected void onStart() {
		super.onStart();
		sActive = true;
	}

	/**
	 * Find views
	 */
	private void findView() {
		// Layouts
		rlTopId = (RelativeLayout) findViewById(R.id.rlTopId);
		llProgressId = (RelativeLayout) findViewById(R.id.rlProgressId);
		rlAudioId = (RelativeLayout) findViewById(R.id.rlAudioId);
		llNoText = (LinearLayout) findViewById(R.id.llNoText);
		mLessonName = (TextView) findViewById(R.id.lessonName);
		// SeekBar
		mSeekBar = (SeekBar) findViewById(R.id.seekBarPager);
		mSeekBarMusic = (SeekBar) findViewById(R.id.seekBarMusic);

		// Audio's view
		mImgPause = (ImageView) findViewById(R.id.playAndPause);
		noAudioId = (Button) findViewById(R.id.noAudioId);

		// Pager
		displayActivityCustom = (DisplayActivityCustom) findViewById(R.id.pager_container);
	}

	/**
	 * Initialization
	 */
	private void init() {
		mContext = this;
		mLessonName.setText(getIntent().getStringExtra(Define.LESSON_NAME));
		// Hide audio's view
		mSeekBarMusic.setVisibility(SeekBar.GONE);
		mImgPause.setVisibility(ImageView.GONE);

		// Hide Text's SeekBar
		mSeekBar.setVisibility(SeekBar.GONE);

		// Media player
		// mp = new MediaPlayer();
		mBookId = 0;
		mAudioPath = "";
	}

	/**
	 * Get list text's path of a Book
	 * 
	 * @param bookId
	 * @return
	 */
	private ArrayList<String> getListTextFiles(int bookId) {
		ArrayList<String> listPath = new ArrayList<String>();
		ArrayList<FileEntity> listFile = FilesTable
				.getAllTextsByBookId(SessionData.getDb(), bookId);
		int listSize = listFile.size();
		for (int i = 0; i < listSize; i++) {
			String filePath = listFile.get(i).getPath();
			if (filePath.endsWith(".png") || filePath.endsWith(".jpg")) {
				listPath.add(filePath);
			}
		}
		Collections.sort(listPath);
		return listPath;
	}

	/**
	 * Show Book (Text & Audio) from Stop Position
	 */
	private void showBookFromStopPosition() {
		mBookId = SharedPreferencesData.getBookId(mContext);
		mAudioPath = SharedPreferencesData.getAudioPath(mContext);

		if (mBookId != Define.DEFAULT_INT) {
			showText(getListTextFiles(mBookId));
		}
		if (mAudioPath != Define.DEFAULT_STRING) {
			showAudio(mAudioPath);
			int progress = SharedPreferencesData.getAudioProgress(mContext);
			System.out.println("Progress: " + progress);
			mSeekBarMusic.setProgress(progress);
			// mp.seekTo(progress);
			if (isBounded && mAudioService != null) {
				mAudioService.seekTo(progress);
			}
		}
	}

	/**
	 * Show text
	 * 
	 * @param listPath
	 */
	private void showText(ArrayList<String> listPath) {
		mTvPagerPosition.setVisibility(View.GONE);
		if (listPath.size() > 0) {
			File file = new File(listPath.get(0));
			if (file.exists()) {
				mTvPagerPosition.setVisibility(View.VISIBLE);
			} else {
				mTvPagerPosition.setVisibility(View.GONE);
			}

			if (listPath.size() > 0) {
				System.out.println("Show text");

				// Show SeekBar
				mSeekBar.setVisibility(SeekBar.VISIBLE);

				// Hide NO_TEXT label
				llNoText.setVisibility(LinearLayout.GONE);

				pager = (MyViewPager) displayActivityCustom.getViewPager();
				adapter = new DisplayActivityAdapter(this, this, listPath, pager);
				adapter.notifyDataSetChanged();
				pager.setAdapter(adapter);
				// New START by NamHV 20160616
				pager.setOffscreenPageLimit(5);
				// New END by NamHV 20160616
				
				pager.setCurrentItem(0);

				// A little space between pages
				pager.setPageMargin(20);
				//
				// pager.setClipChildren(true);

				mSeekBar.setMax((adapter.getCount() - 1) * 100 + 7);
				mTvPagerPosition.setText(1 + "/" + adapter.getCount());
				// Touch actions
				setAction();
			} else {
				mSeekBar.setVisibility(SeekBar.GONE);
			}
		}
	}

	/**
	 * Show audio
	 * 
	 * @param audioPath
	 */
	private void showAudioList(List<String> audioList) {
		DebugOption.info("listAudio", "listAudio = " + audioList.size());
		lvAudioPath.clear();
		lvAudioPath.addAll(audioList);
		totalAudio = lvAudioPath.size();
		if (lvAudioPath.size() > 0) {
			showAudio(lvAudioPath.get(mCurrentPlayIndex));
		}

	}

	private void showAudio(String audioPath) {
		DebugOption.info("audioPath : ", "audioPath : " + audioPath);
		String lesson = audioPath.substring(audioPath.lastIndexOf("/") + 1,
				audioPath.lastIndexOf("."));
		mLessonName.setText(lesson);

		File file = new File(audioPath);
		if (file.exists()) {
			checkExistAudio = true;
		} else {
			checkExistAudio = false;
		}

		if (checkExistAudio) {
			// set title
			MediaMetadataRetriever retriever = new MediaMetadataRetriever();
			retriever.setDataSource(audioPath);
			String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
			if (title != null && !title.equals("")) {
				mLessonName.setText(title);
			}

			mSeekBarMusic.setVisibility(View.VISIBLE);
			mImgPause.setVisibility(View.VISIBLE);
			noAudioId.setVisibility(View.INVISIBLE);
			mSeekBarMusic.setProgress(0);
			mSeekBarMusic.setMax(100);
			if (isBounded && mAudioService != null) {
				mAudioService.tryToGetAudioFocus();
				mAudioService.playNextSong(audioPath);
			}
			/*try {
				mp.pause();
				mHandler.removeCallbacks(mUpdateTimeTask);
				mp.reset();
				mp.setDataSource(audioPath);
				mp.prepare();
				mp.start();
				mImgPause.setImageResource(R.drawable.ic_media_pause);
				mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer mp) {
						mImgPause.setImageResource(R.drawable.ic_media_play);
						DebugOption.info("next Audio", "next Audio" + lvAudioPath.get(index)
								+ "totalAudio : " + totalAudio);
						mp.pause();
						if (index < (totalAudio - 1)) {
							index = index + 1;
							showAudio(lvAudioPath.get(index));
						}
					}
				});
				long duration = mp.getDuration();
				long currentPosition = mp.getCurrentPosition();

				mSeekBarMusic.setMax((int) duration);
				// mSeekBarMusic.setProgress((int) currentPosition);

				updateProgressSong();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}*/

			// SeekBar change listener
			mSeekBarMusic.setOnSeekBarChangeListener(this);
		} else {
			mSeekBarMusic.setVisibility(View.INVISIBLE);
			mImgPause.setVisibility(View.INVISIBLE);
			noAudioId.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * Handle touch actions of Text's progress bar
	 */
	private void setAction() {
		beforePager = 0;

		mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				float flProgress = (float) (seekBar.getProgress() % 100) / 100;
				int intProgress = (int) (seekBar.getProgress() / 100);
				if (flProgress > 0.5) {
					intProgress = intProgress + 1;
				}

				final int temp = intProgress;
				runOnUiThread(new Runnable() {
					public void run() {
						pager.setCurrentItem(temp);
					}
				});
				beforePager = intProgress;
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				final int intProgress = (int) (progress / 100);

				if (beforePager != intProgress) {
					beforePager = intProgress;
					if (DisplayActivityAdapter.countAsync <= 0) {
						runOnUiThread(new Runnable() {
							public void run() {
								pager.setCurrentItem(intProgress);
							}
						});
					}


				}
			}
		});
	}

	// update Song Progress
	private void updateProgressSong() {
		if (checkExistAudio) {
			mHandler.postDelayed(mUpdateTimeTask, 100);
		}
	}

	// handler about progress
	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {
			if (checkExistAudio && isBounded && mAudioService != null) {
				int currentDuration = mAudioService.getCurrentPosition();
				mSeekBarMusic.setProgress((int) currentDuration);
				mHandler.postDelayed(this, 100);
			}
		}
	};

	// back on click button
	public void imgvBackOnclick(View view) {
		finish();
	}

	// menu onclick
	public void imgvMenuOnclick(View view) {

	}

	@Override
	public void setPosition(int position) {
		mSeekBar.setProgress(position * 100);
		mTvPagerPosition.setText(position + 1 + "/" + adapter.getCount());
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		mHandler.removeCallbacks(mUpdateTimeTask);
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		int currentPosition = seekBar.getProgress();
		if (isBounded && mAudioService != null) {
			mAudioService.seekTo(currentPosition);
		}
		// if (mp.isPlaying()) {
		// mp.start();
		// }
		updateProgressSong();
	}

	@Override
	protected void onDestroy() {
		sActive = false;
		if (checkExistAudio) {
			mHandler.removeCallbacks(mUpdateTimeTask);
		}

		// Clear stop position
		DebugOption.info(TAG, "Clear stop position");
		SharedPreferencesData.setBookId(Define.DEFAULT_INT, mContext);
		SharedPreferencesData.setAudioPath(Define.DEFAULT_STRING, mContext);
		unbindService(mServiceConnection);
		super.onDestroy();
	}

	/**
	 * Show list files
	 */
	@Override
	public void fileListOnclick() {
		finish();
	}

	@Override
	public void sortTitle() {
		finish();
	}

	@Override
	public void sortDownloadTime() {
		finish();
	}

	@Override
	public void browserStart() {
		finish();
		Intent intent = new Intent(this, WebViewActivity.class);
		startActivityForResult(intent, Define.REQUEST_CODE_BROWSING);
	}

	@Override
	public void createOrDelFolder() {
		finish();
		Intent intent = new Intent(this, FolderActivity.class);
		startActivityForResult(intent, Define.REQUEST_CODE_FOLDER_ACTION);
	}

	/**
	 * Play or pause audio
	 * 
	 * @param view
	 */

	public void PlayOrPauseOnclick(View view) {
		if (isBounded && mAudioService != null) {
			mAudioService.processTogglePlaybackRequest();
		}
	}

	/**
	 * Go to find Book's Text
	 * 
	 * @param view
	 */
	public void findTextOnClick(View view) {
		DebugOption.info(TAG, "findTextOnClick");

		if (FilesTable.getFilesByType(SessionData.getDb(), Define.FileType.TEXT).size() > 0) {
			Intent intent = new Intent(DisplayActivity.this, SelectActivity.class);
			intent.putExtra(Define.FILE_TYPE, Define.FileType.TEXT);
			startActivityForResult(intent, Define.REQUEST_CODE_SELECT_FILE);
		} else {
			noDataDialog();
		}
	}

	/**
	 * Go to find Book's Audio
	 * 
	 * @param view
	 */
	public void findAudioOnClick(View view) {
		DebugOption.info(TAG, "findAudioOnClick");

		if (FilesTable.getFilesByType(SessionData.getDb(), Define.FileType.AUDIO).size() > 0) {
			Intent intent = new Intent(DisplayActivity.this, SelectActivity.class);
			intent.putExtra(Define.FILE_TYPE, Define.FileType.AUDIO);
			startActivityForResult(intent, Define.REQUEST_CODE_SELECT_FILE);
		} else {
			noDataDialog();
		}
	}

	/**
	 * Create a dialog that notify there is no data
	 */
	private void noDataDialog() {
		final Dialog dialog = new Dialog(mContext);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_no_data);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(false);

		TextView tvBack = (TextView) dialog.findViewById(R.id.tvBack);
		tvBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	@Override
	public void tabOnclick() {
		if (checkVisible) {
			rlTopId.setVisibility(View.INVISIBLE);
			rlAudioId.setVisibility(View.INVISIBLE);
			llProgressId.setVisibility(View.INVISIBLE);
			checkVisible = false;
		} else {
			rlTopId.setVisibility(View.VISIBLE);
			rlAudioId.setVisibility(View.VISIBLE);
			llProgressId.setVisibility(View.VISIBLE);
			checkVisible = true;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Define.REQUEST_CODE_SELECT_FILE) {
			if (resultCode == RESULT_OK) {
				if(data != null) {
					Bundle extras = data.getExtras();
					if (extras != null) {
						int fileType = extras.getInt(Define.FILE_TYPE);
						// If text
						if (fileType == Define.FileType.TEXT) {
							mBookId = extras.getInt(Define.BOOK_ID);
							DebugOption.info(TAG, "Show text, Book_id: " + mBookId);
							showText(getListTextFiles(mBookId));
						} else if (fileType == Define.FileType.AUDIO) {
							int fileId = extras.getInt(Define.FILE_ID);
							DebugOption.info(TAG, "Show audio, File_id: " + fileId);
							FileEntity entity = FilesTable.getEntity(SessionData.getDb(), fileId);
							mAudioPath = entity.getPath();
							showAudio(mAudioPath);
						}
					}
				}
			}
		}
	}

	private class DeleteFileTempBefore extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {


			String path = params[0].substring(params[0].lastIndexOf("/") + 1);
			File folderTemp = new File(SessionData.getFolderApp() + "/" + Define.TEMP_FOLDER + "/"
					+ path);
			DebugOption.info("folderTemp", "folderTemp = " + folderTemp.getAbsolutePath());

			if (folderTemp.exists()) {
				DebugOption.info("NOT LOAD", "NOT LOAD");
				return Define.DEFAULT_STRING;
			} else {
				// File file = new File(SessionData.getFolderApp() + "/" +
				// Define.TEMP_FOLDER);
				// if (file.exists()) {
				// FileIO.deleteFolder(new File(SessionData.getFolderApp() + "/"
				// + Define.TEMP_FOLDER));
				// }
				return params[0];
			}
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result.equals(Define.DEFAULT_STRING)) {
				DebugOption.info("NOT LOAD", "NOT LOAD");
				loadData();
			} else {
				DebugOption.info("LOAD", "LOAD");
				new DecryptAndUnzipData().execute(result);
			}
		}
	}

	private class DecryptAndUnzipData extends AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			progressDecrypt.setMessage(getResources().getString(R.string.please_wait_decrypting));
			progressDecrypt.show();
			super.onPreExecute();
		}

		protected Void doInBackground(String... params) {
			File file = new File(SessionData.getFolderApp() + "/" + Define.TEMP_FOLDER);
			file.mkdirs();

			byte[] fileData = FileIO.getByteFromfile(params[0]);
			byte[] encodeData = null;
			try {
				encodeData = EncodeAndDecodeUtils.decrypt(FileIO.md5(SessionData.getmUUID()),
						fileData);
			} catch (InvalidAlgorithmParameterException e1) {
				e1.printStackTrace();
			}

			// FileIO.deleteFile(params[0]);
			String folderName = params[0].substring(params[0].lastIndexOf("/") + 1);
			String folderTempName = SessionData.getFolderApp() + "/" + Define.TEMP_FOLDER + "/"
					+ folderName;

			DebugOption.info("encodeData", "encodeData lend = " + encodeData.length);
			OutputStream mOutputStream = null;
			try {
				mOutputStream = new FileOutputStream(folderTempName);
				mOutputStream.write(encodeData);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				mOutputStream.flush();
				mOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			String folder = folderTempName.replace(".zip", "");

			// DebugOption.info("Folder", folder);

			Decompress decompress = new Decompress(folderTempName, folder);
			decompress.unzipj4();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			loadData();
			progressDecrypt.dismiss();
		}
	}

	@Override
	public void onAudioPlay() {
		if (isBounded && mAudioService != null) {
			mAudioService.processPlayRequest();
		}
	}

	@Override
	public void onAudioPause() {
		if (isBounded && mAudioService != null) {
			mAudioService.processPauseRequest();
		}
	}

	@Override
	public void onAudioToggle() {
		if (isBounded && mAudioService != null) {
			mAudioService.processTogglePlaybackRequest();
		}
	}

	@Override
	public void onAudioHeadseHook() {

	}

	@Override
	public void onAudioStop() {
		if (isBounded && mAudioService != null) {
			mAudioService.processStopRequest(true);
		}
	}

	@Override
	public void onAudioNext() {
		if (mCurrentPlayIndex < (totalAudio - 1)) {
			mImgPause.setImageResource(R.drawable.ic_media_play);
			DebugOption.info("next Audio", "next Audio" + lvAudioPath.get(mCurrentPlayIndex) + "totalAudio : "
					+ totalAudio);
			mCurrentPlayIndex = mCurrentPlayIndex + 1;
			showAudio(lvAudioPath.get(mCurrentPlayIndex));
		}
	}

	@Override
	public void onAudioPrevious() {
		if (mCurrentPlayIndex > 0) {
			mImgPause.setImageResource(R.drawable.ic_media_play);
			DebugOption.info("next Audio", "next Audio" + lvAudioPath.get(mCurrentPlayIndex) + "totalAudio : "
					+ totalAudio);
			mCurrentPlayIndex = mCurrentPlayIndex - 1;
			showAudio(lvAudioPath.get(mCurrentPlayIndex));
		}
	}

	@Override
	public void onUpdateIcon(boolean isPlaying) {
		if (isPlaying) {
			mImgPause.setImageResource(R.drawable.ic_media_pause);
		} else {
			mImgPause.setImageResource(R.drawable.ic_media_play);
		}
	}

	@Override
	public void onUpdateProgress() {
		if (isBounded && mAudioService != null) {
			long duration = mAudioService.getDuration();
			mSeekBarMusic.setMax((int) duration);
			updateProgressSong();
		}
	}

	@Override
	public void onRemoveProgressTask() {
		mHandler.removeCallbacks(mUpdateTimeTask);
	}

}
