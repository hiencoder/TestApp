package jp.co.marinax.fileplayer.ui.activity;

import java.util.ArrayList;

import jp.co.marinax.fileplayer.R;
import jp.co.marinax.fileplayer.app.config.DebugOption;
import jp.co.marinax.fileplayer.app.config.Define;
import jp.co.marinax.fileplayer.io.db.table.BookMarksTable;
import jp.co.marinax.fileplayer.io.entity.BookMarkEntity;
import jp.co.marinax.fileplayer.io.save.SessionData;
import jp.co.marinax.fileplayer.ui.activity.base.BaseActivity;
import jp.co.marinax.fileplayer.view.adapter.BookMarkAdapter;
import jp.co.marinax.fileplayer.view.adapter.BookMarkAdapter.ChangeStatus;
import jp.co.marinax.fileplayer.view.adapter.BookMarkAdapter.DeleteItem;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;

public class BookMarksActivity extends BaseActivity implements DeleteItem, ChangeStatus {
	private ListView lvbookMark;
	private ArrayList<BookMarkEntity> listbookMark = new ArrayList<BookMarkEntity>();
	private BookMarkAdapter mAdapter;
	private Button btnActionId;
	private boolean mFlag = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book_mark);
		lvbookMark = (ListView) findViewById(R.id.lvBookMarkId);
		btnActionId = (Button) findViewById(R.id.btnActionId);
		mAdapter = new BookMarkAdapter(this, listbookMark);
		lvbookMark.setAdapter(mAdapter);
		loadData();
		setAction();
	}
	
	// load data
	public void loadData() {
		listbookMark.clear();
		// New Start 20160112 SonBX
		if (!mSPreference.getString(Define.BOOK_MARK_SECOND).equals(Define.BOOK_MARK_SECOND)) {
			BookMarkEntity entityGoogle = new BookMarkEntity("Google", "https://www.google.co.jp/");
			BookMarkEntity entityYahoo = new BookMarkEntity("Yahoo", "http://www.yahoo.co.jp/");
			BookMarkEntity entityNHKStore = new BookMarkEntity("NHKサービスセンター　ダウンロードストア", "https://dls.nhk-sc.or.jp/");
			BookMarkEntity entityNHK = new BookMarkEntity("語学便", "http://r-gogaku.jp/sp/");
			BookMarksTable.deleteLink(SessionData.getDb(), entityGoogle);
			BookMarksTable.deleteLink(SessionData.getDb(), entityYahoo);
			BookMarksTable.deleteLink(SessionData.getDb(), entityNHK);
			BookMarksTable.insert(SessionData.getDb(), entityNHK);
			BookMarksTable.insert(SessionData.getDb(), entityNHKStore);
			BookMarksTable.insert(SessionData.getDb(), entityYahoo);
			BookMarksTable.insert(SessionData.getDb(), entityGoogle);
			mSPreference.saveString(Define.BOOK_MARK_SECOND, Define.BOOK_MARK_SECOND);
			mSPreference.saveString(Define.BOOK_MARK_FIRST, Define.BOOK_MARK_FIRST);
		}
		// New End 20160112 SonBX
		listbookMark.addAll(BookMarksTable.getAllEntity(SessionData.getDb()));
		addBasicLink();
		mAdapter.notifyDataSetChanged();
	}
	
	public void setAction() {
		// OnClick item to show
	lvbookMark.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
				Intent intent = new Intent(BookMarksActivity.this, WebViewActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra(Define.WEB_BOOKMARK_URL, listbookMark.get(position).getUrl());
				startActivity(intent);
				
			}
		});

		// on hole item
	lvbookMark.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				mAdapter.setShowDelete(true);
				mAdapter.notifyDataSetChanged();
				return false;
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	// add some basic link
	public void addBasicLink() {
		DebugOption.info("WEB", mSPreference.getString(Define.BOOK_MARK_FIRST));
		if (!mSPreference.getString(Define.BOOK_MARK_FIRST).equals(Define.BOOK_MARK_FIRST)) {
			BookMarkEntity entityGoogle = new BookMarkEntity("Google", "https://www.google.co.jp/");
			BookMarkEntity entityYahoo = new BookMarkEntity("Yahoo", "http://www.yahoo.co.jp/");
			// New Start 20160112
			BookMarkEntity entityNHKStore = new BookMarkEntity("NHKサービスセンター　ダウンロードストア", "https://dls.nhk-sc.or.jp/");
			// New End 20160112
			BookMarkEntity entityNHK = new BookMarkEntity("語学便", "http://r-gogaku.jp/sp/");
			listbookMark.add(entityNHK);
			// New Start 20160112
			listbookMark.add(entityNHKStore);
			// New End 20160112
			listbookMark.add(entityYahoo);
			listbookMark.add(entityGoogle);
			BookMarksTable.insert(SessionData.getDb(), entityNHK);
			// New Start 20160112
			BookMarksTable.insert(SessionData.getDb(), entityNHKStore);
			// New End 20160112
			BookMarksTable.insert(SessionData.getDb(), entityYahoo);
			BookMarksTable.insert(SessionData.getDb(), entityGoogle);
			mSPreference.saveString(Define.BOOK_MARK_FIRST, Define.BOOK_MARK_FIRST);
			// New Start 20160112
			mSPreference.saveString(Define.BOOK_MARK_SECOND, Define.BOOK_MARK_SECOND);
			// New End 20160112
		} else {
			// Do nothing
		}
	}

	// change status tab
	public void changeStatusTab(View view) {
		mAdapter.setShowDelete(!mFlag);
		mAdapter.notifyDataSetChanged();
	}

	// on Back tab
	public void onBackTab(View view) {
		finish();
	}

	@Override
	public void position(int position) {
		// remove database
		BookMarksTable.deleteLink(SessionData.getDb(), listbookMark.get(position).getId());
		listbookMark.remove(position);
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void change(boolean flag) {
		mFlag = flag;
		if (flag) {
			btnActionId.setText(getResources().getString(R.string.done));
		} else {
			btnActionId.setText(getResources().getString(R.string.action));
		}
	}
}
