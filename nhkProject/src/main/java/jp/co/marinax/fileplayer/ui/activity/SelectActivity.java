package jp.co.marinax.fileplayer.ui.activity;

import java.util.ArrayList;

import jp.co.marinax.fileplayer.app.config.DebugOption;
import jp.co.marinax.fileplayer.app.config.Define;
import jp.co.marinax.fileplayer.io.db.table.FilesTable;
import jp.co.marinax.fileplayer.io.entity.FileEntity;
import jp.co.marinax.fileplayer.io.save.SessionData;
import jp.co.marinax.fileplayer.ui.activity.base.BaseActivity;
import jp.co.marinax.fileplayer.view.adapter.FileAdapter;
import jp.co.marinax.fileplayer.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.testflightapp.lib.TestFlight;

public class SelectActivity extends BaseActivity {
	private String TAG = SelectActivity.class.getSimpleName();
	private ListView lvListFiles;
	private ArrayList<FileEntity> mListFile = new ArrayList<FileEntity>();
	private FileAdapter adapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select);
		TestFlight.passCheckpoint(TAG);
		TestFlight.log("come in " + TAG);

		// get value from top
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			int fileType = extras.getInt(Define.FILE_TYPE);
			SessionData.setFileType(fileType);
		}

		lvListFiles = (ListView) findViewById(R.id.lvListfile);
		adapter = new FileAdapter(this, mListFile);
		lvListFiles.setAdapter(adapter);
		setOnItemClickListener();
	}

	@Override
	protected void onResume() {
		super.onResume();
		int fileType = SessionData.getFileType();
		mListFile.clear();
		mListFile.addAll(getListFiles(fileType));
		DebugOption.info(" size : ", " size : " + mListFile.size());
		adapter.notifyDataSetChanged();
		setResult(RESULT_OK);
	}

	/**
	 * Get list files
	 * 
	 * @return List of FileEntitys
	 */
	private ArrayList<FileEntity> getListFiles(int fileType) {
		DebugOption.info(TAG, fileType + "");
		if (fileType == Define.FileType.AUDIO) {
			return FilesTable.getFilesByType(SessionData.getDb(), fileType);
		} else {
			return FilesTable.getBooksFromTextFiles(SessionData.getDb(), -1);
		}
	}

	/****************************
	 * Set on item click listener
	 ****************************/
	private void setOnItemClickListener() {
		lvListFiles
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						if (DisplayActivity.sActive) {
							DebugOption.info(TAG, "set result");
							Intent intent = new Intent();
							intent.putExtra(Define.BOOK_ID,
									mListFile.get(position).getBook_id());
							intent.putExtra(Define.FILE_ID,
									mListFile.get(position).getId());
							intent.putExtra(Define.FILE_TYPE,
									mListFile.get(position).getType());
							setResult(RESULT_OK, intent);
						} else {
							Intent intent = new Intent(SelectActivity.this,
									DisplayActivity.class);
							intent.putExtra(Define.BOOK_ID,
									mListFile.get(position).getBook_id());
							intent.putExtra(Define.FILE_ID,
									mListFile.get(position).getId());
							intent.putExtra(Define.FILE_TYPE,
									mListFile.get(position).getType());
							startActivity(intent);
						}
						finish();
					}
				});
	}
}
