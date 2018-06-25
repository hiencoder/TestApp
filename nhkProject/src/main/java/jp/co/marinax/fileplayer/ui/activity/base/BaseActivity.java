package jp.co.marinax.fileplayer.ui.activity.base;

import jp.co.marinax.fileplayer.app.check.CheckInternet;
import jp.co.marinax.fileplayer.app.check.CheckSdCard;
import jp.co.marinax.fileplayer.app.config.DebugOption;
import jp.co.marinax.fileplayer.app.config.Define;
import jp.co.marinax.fileplayer.io.save.SessionData;
import jp.co.marinax.fileplayer.io.save.SharePreference;
import jp.co.marinax.fileplayer.ui.activity.FolderActivity;
import jp.co.marinax.fileplayer.ui.activity.TopActivity;
import jp.co.marinax.fileplayer.ui.activity.WebViewActivity;
import jp.co.marinax.fileplayer.utils.FileIO;
import jp.co.marinax.fileplayer.utils.ShowToastUtils;
import jp.co.marinax.fileplayer.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.testflightapp.lib.TestFlight;

@SuppressLint("NewApi")
public abstract class BaseActivity extends Activity {
	public static CheckInternet mChkInternet = new CheckInternet();
	public static SharePreference mSPreference = new SharePreference();
	private Context mContext;
	// Dialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TestFlight.takeOff(getApplication(), Define.TESTFILGHT_ID);
		TestFlight.passCheckpoint("WeMobile");
		mChkInternet.init(this);
		mSPreference.init(this);
		mContext = this;

		boolean isExternalStorageWritable = CheckSdCard
				.isExternalStorageWritable();
		boolean isExternalStorageReadable = CheckSdCard
				.isExternalStorageReadable();

		boolean checkSdCard = true;
		if (isExternalStorageWritable == true
				&& isExternalStorageReadable == true) {
			checkSdCard = true;
		} else {
			ShowToastUtils.showT(getApplicationContext(), getResources()
					.getString(R.string.Sdcard_is_not_available));
			checkSdCard = false;
		}

		if (checkSdCard) {
			if (SessionData.getFolderApp().equals("")) {
				String packageName = Environment.getExternalStorageDirectory()
						.getAbsolutePath()
						+ "/Android/data/"
						+ getApplicationContext().getPackageName() + "/files";
				SessionData.setFolderApp(packageName);
			}
		}

		// set UUID
		if (mSPreference.getString(Define.UUID).equals(Define.DEFAULT_STRING)) {
			String uuid = FileIO.getUUId(this);
			mSPreference.saveString(Define.UUID, uuid);
		}
		
		SessionData.setmUUID(mSPreference.getString(Define.UUID));
		DebugOption.info("UUID", "UUID = " + FileIO.md5(SessionData.getmUUID()));
	}

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		DebugOption.info("startActivity", "startActivity");
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}

	@Override
	public void finish() {
		super.finish();
		DebugOption.info("finish", "finish");
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}

	// on KeyBoard menu
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (findViewById(R.id.imgvMenuId) != null && keyCode == KeyEvent.KEYCODE_MENU) {
				menuData();
		}
		return super.onKeyDown(keyCode, event);
	}

	/*********************************************************
	 * Menu OnClick
	 * 
	 * @param view
	 */
	
	public void MenuOnclick(View view) {
		menuData();
	}

	public void menuData() {
		final Dialog dialog = new Dialog(mContext,
				android.R.style.Theme_Translucent_NoTitleBar);
		dialog.setContentView(R.layout.menu_dialog);
		dialog.show();

		LinearLayout llMenu = (LinearLayout) dialog
				.findViewById(R.id.llMenuBorder);
		TextView browser = (TextView) dialog.findViewById(R.id.browser_startId);
		browser.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				browserStart();
				dialog.dismiss();
			}
		});

		TextView fileList = (TextView) dialog.findViewById(R.id.file_listId);
		fileList.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				fileListOnclick();
				dialog.dismiss();
			}
		});

		TextView AudioList = (TextView) dialog.findViewById(R.id.list_audioId);
		AudioList.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				AudioListOnclick();
				dialog.dismiss();
			}
		});

		TextView selectShowFile = (TextView) dialog
				.findViewById(R.id.select_show_file);
		selectShowFile.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				selectShowFile();
				dialog.dismiss();
			}
		});

		TextView sortTitle = (TextView) dialog.findViewById(R.id.sort_title);
		sortTitle.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				sortTitle();
				dialog.dismiss();
			}
		});

		TextView sortDownloadTime = (TextView) dialog
				.findViewById(R.id.sort_download_time);
		sortDownloadTime.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				sortDownloadTime();
				dialog.dismiss();
			}
		});

		TextView createOrDelFolder = (TextView) dialog
				.findViewById(R.id.create_delete_folderId);
		createOrDelFolder.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				createOrDelFolder();
				dialog.dismiss();
			}
		});

		llMenu.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				dialog.dismiss();
				return false;
			}
		});
	}

	/********************************************
	 * Set function for menu
	 ********************************************/
	public void browserStart() {
		Intent intent = new Intent(this, WebViewActivity.class);
		startActivityForResult(intent, Define.REQUEST_CODE_BROWSING);
	}

	public void fileListOnclick() {
		SessionData.setTitleSort(false);
		Intent intent = new Intent(this, TopActivity.class);
		startActivityForResult(intent, Define.NEW_OTHER_ACTIVITY);
	}

	public void AudioListOnclick() {
		Intent intent = new Intent(this, TopActivity.class);
		intent.putExtra(Define.FILE_TYPE, Define.FileType.AUDIO);
		startActivityForResult(intent, Define.NEW_OTHER_ACTIVITY);
	}

	public void selectShowFile() {
		Intent intent = new Intent(this, TopActivity.class);
		intent.putExtra(Define.FILE_TYPE, Define.FileType.TEXT);
		startActivityForResult(intent, Define.NEW_OTHER_ACTIVITY);
	}

	public void sortTitle() {
		SessionData.setTitleSort(true);
		if (this.getCurrentActivity().equals(".ui.activity.TopActivity")) {
			((SortClass) this).sort(SessionData.isTitleSort());
		} else {
			Intent intent = new Intent(this, TopActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(intent, Define.NEW_OTHER_ACTIVITY);
		}
	}

	public void sortDownloadTime() {
		SessionData.setTitleSort(false);
		if (this.getCurrentActivity().equals(".ui.activity.TopActivity")) {
			((SortClass) this).sort(SessionData.isTitleSort());
		} else {
			Intent intent = new Intent(this, TopActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(intent, Define.NEW_OTHER_ACTIVITY);
		}
	}

	public void createOrDelFolder() {
		Intent intent = new Intent(this, FolderActivity.class);
		startActivityForResult(intent, Define.REQUEST_CODE_FOLDER_ACTION);
	}

	public String getCurrentActivity() {
		String str = this.getComponentName().getShortClassName();
		DebugOption.info("TAG", "TAG = " + str);
		return str;
	}

	public interface SortClass {
		public void sort(boolean flag);
	}
}
