package jp.co.marinax.fileplayer.ui.activity;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import jp.co.marinax.fileplayer.R;
import jp.co.marinax.fileplayer.app.config.DebugOption;
import jp.co.marinax.fileplayer.app.config.Define;
import jp.co.marinax.fileplayer.app.download.Download;
import jp.co.marinax.fileplayer.io.db.table.BookMarksTable;
import jp.co.marinax.fileplayer.io.db.table.BookTable;
import jp.co.marinax.fileplayer.io.db.table.FilesTable;
import jp.co.marinax.fileplayer.io.db.table.FolderTable;
import jp.co.marinax.fileplayer.io.entity.BookMarkEntity;
import jp.co.marinax.fileplayer.io.save.SessionData;
import jp.co.marinax.fileplayer.ui.activity.base.BaseActivity;
import jp.co.marinax.fileplayer.utils.FileIO;
import jp.co.marinax.fileplayer.utils.ShowDialogUtils;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.testflightapp.lib.TestFlight;

public class WebViewActivity extends BaseActivity {
	/** Context */
	private Context mContext;
	private String TAG = WebViewActivity.class.getSimpleName();
	private String mUrl = Define.WEBVIEW_URL;
	WebView webview;
	private Stack<String> mUndoStack;
	private Stack<String> mRedoStack;
	private ImageView mUndoId;
	private ImageView mRedoId;
	private EditText mEtInputTextId;
	private ProgressDialog prDgCheckExist;
	
	ImageView mIvReload;
	Animation mReloadAnim;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DebugOption.info("onCreate", "onCreate");
		mContext = this;
		// prgLoadingWeb = new ProgressDialog(mContext);

		// initialization stack
		mUndoStack = new Stack<String>();
		mRedoStack = new Stack<String>();

		prDgCheckExist = new ProgressDialog(mContext);
		// test flight
		TestFlight.passCheckpoint(TAG);
		TestFlight.log("come in " + TAG);
		setContentView(R.layout.activity_webview);
		mEtInputTextId = (EditText) findViewById(R.id.etInputTextId);
		mUndoId = (ImageView) findViewById(R.id.imgvUndoId);
		mRedoId = (ImageView) findViewById(R.id.imgvRedoId);
		
		mIvReload = (ImageView) findViewById(R.id.doneConfirmTab);
		mReloadAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
		mIvReload.setAnimation(mReloadAnim);
		
		// set on action search 
		mEtInputTextId.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					loadUrlNow(changeUrl(mEtInputTextId.getText().toString()));
				}
				return false;
			}
		});
		
		// change status
		changeStatus();
		webview = (WebView) findViewById(R.id.webview);
		// New Start 20151224 SonBX
		// Use Chrome components
		webview.setWebChromeClient(new WebChromeClient());
		// New End 20151224 SonBX
		WebSettings settings = webview.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setSupportZoom(false);
		// New Start 20151204 SonBX
		// WebView の UserAgent末尾に「 FilePlayer」 を追加
		settings.setUserAgentString(settings.getUserAgentString() + " FilePlayer");
		// New End 20151204 SonBX
		// settings.

		Bundle extra = getIntent().getExtras();
		if (extra != null) {
			String url = extra.getString(Define.WEB_BOOKMARK_URL);
			DebugOption.info("url", url);
			if (!url.equals(Define.DEFAULT_STRING)) {
				loadUrlNow(changeUrl(url));
				mUndoStack.push(url);
				mEtInputTextId.setText(url);
			} else {
				loadUrlNow(changeUrl(Define.WEBVIEW_URL));
				mUndoStack.push(Define.WEBVIEW_URL);
				mEtInputTextId.setText(Define.WEBVIEW_URL);
			}
		} else {
			// String doc = Jsoup.connect("http://....").get();
			loadUrlNow(changeUrl(Define.WEBVIEW_URL));
			mUndoStack.push(Define.WEBVIEW_URL);
			mEtInputTextId.setText(Define.WEBVIEW_URL);
		}

		webview.setDownloadListener(new DownloadListener() {

			@Override
			public void onDownloadStart(String url, String userAgent, String contentDisposition,
					String mimetype, long contentLength) {
				// push to url
				// mUndoStack.push(url);

				// clear redo
				// if (mRedoStack.size() > 0) {
				// mRedoStack.clear();
				// }

				mUrl = url;
				// url = url + "&key=" + uuid;
				DebugOption.info("UUID_URL", url);
				DebugOption.info(TAG, "UserAgent: " + userAgent);
				DebugOption.info(TAG, "ContentDisposition: " + contentDisposition);
				DebugOption.info(TAG, "MimeType: " + mimetype);
				DebugOption.info(TAG, "ContentLength: " + contentLength);
				new GetFileName().execute(url);
			}
		});

		webview.setWebViewClient(new WebViewClient() {
			ProgressDialog prgLoadingWeb;
			
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// New Start 170816 SonBX
				if(url.startsWith("gogaku://")) {
					DebugOption.info(TAG, "Start with gogaku://");
					
					AlertDialog.Builder builder = new AlertDialog.Builder(WebViewActivity.this);
					builder.setMessage(R.string.fileplayer_existed);
					// Add the buttons
					builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					           public void onClick(DialogInterface dialog, int id) {
					               // User clicked OK button
					           }
					       });

					// Create the AlertDialog
					AlertDialog dialog = builder.create();
					dialog.show();
					return true;
				}
				// New End 170816 SonBX
				
				mEtInputTextId.setText(url);
				mUndoStack.push(url);
				mUrl = url;
				DebugOption.info(" URL :", "url = " + url);
				changeStatus();
				
				if (mLastUrl != null && mLastUrl.equals(url)) {
					return super.shouldOverrideUrlLoading(view, url);
				}
				
				if(url.endsWith(".mp3")) {
					mUrl = url;
					new GetFileName().execute(url);
					return true;
				}
				
				final Map<String, String> extraHeaders = new HashMap<String, String>();
				extraHeaders.put("X_UDID", FileIO.md5(SessionData.getmUUID()));
				webview.loadUrl(url, extraHeaders);
				mLastUrl = url;

				mEtInputTextId.setText(url);
				return true;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				if (prgLoadingWeb != null) {
					prgLoadingWeb.dismiss();
				}
				prgLoadingWeb = new ProgressDialog(WebViewActivity.this);
				prgLoadingWeb.show();
				prgLoadingWeb.setContentView(R.layout.progress_dialog);
				mReloadAnim.start();
			}

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
				if (prgLoadingWeb != null) {
					prgLoadingWeb.dismiss();
					prgLoadingWeb = null;
				}
				mReloadAnim.cancel();
				
				AlertDialog.Builder builder = new AlertDialog.Builder(WebViewActivity.this);
				builder.setMessage(R.string.load_webpage_fail);
				// Add the buttons
				builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				               // User clicked OK button
				           }
				       });

				// Create the AlertDialog
				AlertDialog dialog = builder.create();
				dialog.show();
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				if (prgLoadingWeb != null) {
					prgLoadingWeb.dismiss();
					prgLoadingWeb = null;
				}
				mReloadAnim.cancel();
			}
		});
		
	}

	String mLastUrl;

	// load url with header

	void loadUrlNow(String url) {
		DebugOption.debug(TAG, "Load url: " + url);
		webview.stopLoading();
		// New Start 170808 SonBX
		if(url.startsWith("gogaku://")) {
			DebugOption.info(TAG, "Start with gogaku://");
			
			// Hide soft-keyboard
			View view = this.getCurrentFocus();
			if (view != null) {  
			    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
			}
			
			AlertDialog.Builder builder = new AlertDialog.Builder(WebViewActivity.this);
			builder.setMessage(R.string.fileplayer_existed);
			// Add the buttons
			builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			               // User clicked OK button
			           }
			       });

			// Create the AlertDialog
			AlertDialog dialog = builder.create();
			dialog.show();
			return;
		}
		// New End 170808 SonBX
		
//		 Map<String, String> extraHeaders = new HashMap<String, String>();
//		 extraHeaders.put("HTTP_X_UDID", FileIO.md5(SessionData.getmUUID()));
//		 webview.loadUrl(url, extraHeaders);
		mUrl = url;
		mEtInputTextId.setText(url);
		new LoadUrlWebview().execute(url);
		}

	/**
	 * @param fileName
	 * @param url check and continue download
	 */
	// Mod Start 20151221 SonBX
//	public void CheckFileNameExist(String fileName, String url) {
	// Mod 20151221
	public void CheckFileNameExist(String fileName, String url, String fileType) {
	// Mod End 20151221 SonBX
		// Del Start 20151009 SonBX
//		boolean check = fileName.contains(Define.EXTENSION_TEXT);
		// Del End 20151009 SonBX
		// New Start 20151008 SONBX
		// Mod Start 20151221 SonBX
//		if(url.contains(Define.LinkType.SOUND_LINK)) {
		// Mod 20151221
		if(fileType.equals(Define.FileTypeDownload.ZIP_MP3)) {
		// Mod End 20151221 SonBX
			DebugOption.info("FILE_PATH", fileName);
			String tempFolderName = fileName.replace(Define.EXTENSION_TEXT, "");
			if(FolderTable.checkIfCanRedownload(SessionData.getDb(), tempFolderName)) {
				// Mod Start 20151221 SonBX
//				String[] params = { url };
				// Mod 20151221
				String[] params = { url, fileType };
				// Mod End 20151221 SonBX
				// Download zip file
				new Download(mContext).execute(params);
			} else {
				Toast.makeText(mContext, mContext.getResources().getString(R.string.file_is_exist),
						Toast.LENGTH_SHORT).show();
			}
		} else {
		// New End 20151009 SONBX
			// Mod Start 20151009 SonBX
//		if (check) {
			// Mod 20151009
			// Mod Start 20151221 SonBX
//			if (url.contains(Define.LinkType.TEXT_LINK)) {
			// Mod 20151221
			if (fileType.equals(Define.FileTypeDownload.ZIP_TEXT)) {
			// Mod End 20151221 SonBX
			// Mod End 20151009 SonBX
				DebugOption.info("FILE_PATH", fileName);
	
				String bookName = fileName;
				bookName = bookName.replace(Define.EXTENSION_AUDIO, "");
				bookName = bookName.replace(Define.EXTENSION_TEXT, "");
				if (bookName.contains("(")) {
					bookName = bookName.substring(0, bookName.lastIndexOf("("));
				}
				// New Start 20151223 SonBX
				// In case of beginnerEnglish_201511_Text.zip
				if(bookName.contains("_Text")) {
					bookName = bookName.replace("_Text", "");
				} else 
				// In case of beginnerEnglish_201511_text.zip
				if(bookName.contains("_text")) {
					bookName = bookName.replace("_text", "");
				}
				// New End 20151223 SonBX
	
				DebugOption.info("bookName", "bookName : " + bookName);
	
				// download file zip text
				int bookId = BookTable.searchBook(SessionData.getDb(), bookName);
	
				File file = new File(SessionData.getFolderApp() + "/" + fileName);
	
				if (FilesTable.searchTextBook(SessionData.getDb(), bookId) == Define.DEFAULT_INT
						|| !file.exists()) {
					// display download
	
					DebugOption.info("CHECK", "CHECK");
					// Mod Start 20151221 SonBX
//					String[] params = { url };
					// Mod 20151221
					String[] params = { url, fileType };
					// Mod End 20151221 SonBX
					new Download(mContext).execute(params);
				} else {
					Toast.makeText(mContext, mContext.getResources().getString(R.string.file_is_exist),
							Toast.LENGTH_SHORT).show();
				}
			} else {
				DebugOption.info("FILE_PATH", fileName);
				File file = new File(SessionData.getFolderApp() + "/" + fileName);
				if (FilesTable.searchByName(SessionData.getDb(), fileName) == Define.DEFAULT_INT
						|| !file.exists()) {
					// Mod Start 20151221 SonBX
//					String[] params = { url };
					// Mod 20151221
					String[] params = { url, fileType };
					// Mod End 20151221 SonBX
					new Download(mContext).execute(params);
				} else {
					Toast.makeText(mContext, mContext.getResources().getString(R.string.file_is_exist),
							Toast.LENGTH_SHORT).show();
				}
			}
		}
		setResult(RESULT_OK);
	}

	// this Asyn get file name from url by header
	private class GetFileName extends AsyncTask<String, Void, String> {
		private String mFileType = "";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			prDgCheckExist.setMessage(mContext.getResources().getString(R.string.checking_exist));
			prDgCheckExist.show();
		}
		@Override
		protected String doInBackground(String... params) {
			String fileName = "";
			DefaultHttpClient client = new DefaultHttpClient();

			// cookie
			String cookieStr = CookieManager.getInstance().getCookie(params[0]);
			BasicClientCookie cookie = new BasicClientCookie("Cookie", cookieStr);
			CookieStore cookieStore = new BasicCookieStore();
			cookieStore.addCookie(cookie);
			client.setCookieStore(cookieStore);

			HttpGet httpRequest = new HttpGet(params[0]);
			httpRequest.addHeader("X_UDID", FileIO.md5(SessionData.getmUUID()));
			httpRequest.addHeader("Cookie", cookieStr);
			
			DebugOption.debug(TAG, "Download request");
			try {
				HttpResponse httpResponse = client.execute(httpRequest);

				Header[] headers = httpResponse.getAllHeaders();
				for (Header header : headers) {
					Log.e("Header", "header: " + header.toString());
					// Mod Start 20151008 SonBX 
//					if (header.getName().equals("Content-Disposition")) {
					// Mod 20151009
					if (header.getName().equalsIgnoreCase("Content-Disposition")) {
					// Mod End 20151008 SonBX
						String value = header.getValue();
						fileName = value.substring(value.lastIndexOf("=") + 1);
						DebugOption.info("fileName", fileName);
						// New Start 20151009 SonBX Just in case, remove special character (') if existed
						fileName = fileName.substring(fileName.lastIndexOf("'") + 1);
						DebugOption.info("fileName2", fileName);
						// New End 20151009 SonBX
					}
					
					// New Start 20151221 SonBX
					if(header.getName().equalsIgnoreCase("Content-Type")) {
						String value = header.getValue();
						if(value.contains("filetype")) {
							mFileType = value.substring(value.lastIndexOf("=") + 1);
						} else {
							if(value.contains("audio") || value.contains("mp3")) {
								mFileType = Define.FileTypeDownload.MP3;
							} else {
								mFileType = Define.FileTypeDownload.ZIP_TEXT;
							}
						}
						DebugOption.info("fileType", mFileType);
					}
					// New End 20151221 SonBX
				}
				// New Start 20151229 SonBX
				// In case of file name not described in header
				if(fileName.equals("")) {
					fileName = mUrl.substring(mUrl.lastIndexOf("/") + 1);
				}
				// New End 20151229 SonBX
			} catch (IOException e) {
				e.printStackTrace();
			}

			client.getConnectionManager().shutdown();
			return fileName;

		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			DebugOption.info("RESULT", "result:" + result);
			prDgCheckExist.dismiss();
			// changeStatus();
			CheckFileNameExist(result, mUrl, mFileType);
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		DebugOption.info("new intent", "new intent");

	}

	// onResume
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	/**
	 * Back to previous screen
	 * 
	 * @param view
	 */
	public void imgvBackOnclick(View view) {
		DebugOption.info(TAG, "imgvBackOnclick");
		finish();
	}
	
	// Done onClick
	public void doneConfimTab(View view) {
		String url = mEtInputTextId.getText().toString();
		if (!url.equals(Define.DEFAULT_STRING)) {
			loadUrlNow(changeUrl(url));
			mUndoStack.add(url);
			// mRedoStack.clear();
			changeStatus();
		}
		hideKeyboard();
	}
	

	// hide Keyboard
	public void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
	}

	// show book mark onClick
	public void showBookMarkOnclick(View view) {
		Intent intent = new Intent(WebViewActivity.this, BookMarksActivity.class);
		startActivity(intent);
	}

	/******************************************
	 * on Tab Event
	 ****************************/
	public void beforePageTab(View view) {
		if (mUndoStack.size() > 1) {
			String url = mUndoStack.pop();
			mRedoStack.push(url);
			if (!mUndoStack.isEmpty()) {
				loadUrlNow(changeUrl(mUndoStack.peek()));
			}
		}
		changeStatus();
	}

	// next pageTab
	public void nextPageTab(View view) {
		if (mRedoStack.size() > 0) {
			String url = mRedoStack.pop();
			DebugOption.info("nextPageTab", "nextPageTab : " + url);
			loadUrlNow(changeUrl(url));
			mUndoStack.push(url);
		}
		changeStatus();
	}

	// change status
	public void changeStatus() {
		if (mUndoStack.size() > 1) {
			Drawable drawable = getResources().getDrawable(R.drawable.arrow_left);
			drawable.setAlpha(255);
			mUndoId.setImageDrawable(drawable);
		} else {
			Drawable drawable = getResources().getDrawable(R.drawable.arrow_left);
			drawable.setAlpha(100);
			mUndoId.setImageDrawable(drawable);
		}

		if (mRedoStack.size() > 0) {
			Drawable drawable = getResources().getDrawable(R.drawable.arrow_right);
			drawable.setAlpha(255);
			mRedoId.setImageDrawable(drawable);
		} else {
			Drawable drawable = getResources().getDrawable(R.drawable.arrow_right);
			drawable.setAlpha(100);
			mRedoId.setImageDrawable(drawable);
		}
	}

	// save BookMarks tab
	public void saveBookMarkTab(View view) {
		Log.e("saveBookMarkTab", "mUrl: " + mUrl);
		if (BookMarksTable.searchLink(SessionData.getDb(), mUrl) == Define.DEFAULT_INT) {
			BookMarkEntity entity = new BookMarkEntity(webview.getTitle(),
					mUrl);
			BookMarksTable.insert(SessionData.getDb(), entity);
			ShowDialogUtils.showDialog(mContext, getResources().getString(R.string.book_mark));
		} else {
			ShowDialogUtils.showDialog(mContext, getResources()
					.getString(R.string.book_mark_signed));
		}
	}
	
	public void lvBookMarkIntent(View view) {
		Intent intent = new Intent(WebViewActivity.this, BookMarksActivity.class);
		startActivity(intent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void setCancel(boolean cancel) {
		if (cancel) {
			DebugOption.info("STATUS", "not connect network");
		}
	}
	
	public String changeUrl(String url) {
		// if (!url.startsWith("www.") && !url.startsWith("http://")) {
		// url = "www." + url;
		// }
		// New Start 170808 SonBX
		if(url.startsWith("gogaku://")) {
			DebugOption.info(TAG, "Start with gogaku://");
			return url;
		}
		// New End 170808 SonBX
		if (!url.startsWith("http://") && !url.startsWith("https://")) {
			url = "http://" + url;
		}
		return url;
	}

	// loading web url
	private class LoadUrlWebview extends AsyncTask<String, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(final String... params) {
			final Map<String, String> extraHeaders = new HashMap<String, String>();
			extraHeaders.put("X_UDID", FileIO.md5(SessionData.getmUUID()));
			DebugOption.debug(TAG, "LoadURL on UI thread");
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					webview.loadUrl(params[0], extraHeaders);
				}
			});

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		}

	}
}

