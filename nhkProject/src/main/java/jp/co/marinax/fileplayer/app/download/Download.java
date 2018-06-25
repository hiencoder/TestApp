package jp.co.marinax.fileplayer.app.download;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.util.ArrayList;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.widget.Toast;
import jp.co.marinax.fileplayer.R;
import jp.co.marinax.fileplayer.app.config.DebugOption;
import jp.co.marinax.fileplayer.app.config.Define;
import jp.co.marinax.fileplayer.io.db.table.BookTable;
import jp.co.marinax.fileplayer.io.db.table.FilesTable;
import jp.co.marinax.fileplayer.io.db.table.FolderTable;
import jp.co.marinax.fileplayer.io.entity.BookEntity;
import jp.co.marinax.fileplayer.io.entity.FileEntity;
import jp.co.marinax.fileplayer.io.entity.FolderEntity;
import jp.co.marinax.fileplayer.io.save.SessionData;
import jp.co.marinax.fileplayer.utils.Decompress;
import jp.co.marinax.fileplayer.utils.EncodeAndDecodeUtils;
import jp.co.marinax.fileplayer.utils.FileIO;
import jp.co.marinax.fileplayer.utils.StringUtils;
import jp.co.marinax.fileplayer.utils.TimeUtils;

/**
 * Download file from a URL and save to External storage
 * @author SONBX
 */
public class Download extends AsyncTask<String, Integer, String> {
	/** Progress Dialog */
	private ProgressDialog progressDzip;
	private ProgressDialog mPD;
	private String filePath = "";
	private String fileName = "";
	private int bookId = 0;
	/** Context */
	private Context mContext;
	// Mod Start 20151009 SonBX
//	private byte[] data = new byte[1024];
	// Mod 20151009
	private byte[] data = new byte[4096];
	// Mod End 20151009 SonBX
	
	// New Start 20151009 SonBX
	private String mURL = "";
	// New End 20151009 SonBX
	
	// New Start 20151221 SonBX
	private String mFileType = "";
	// New End 20151221 SonBX

	public Download(Context context) {
		mContext = context;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mPD = new ProgressDialog(mContext);
		mPD.setMessage(mContext.getResources().getString(R.string.downloading_file_please_wait));
		mPD.setCancelable(false);
		try {
			mPD.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected String doInBackground(String... params) {
		int count = 0;
		try {
			// New Start 20151009 SonBX
			mURL = params[0];
			// New End 20151009 SonBX
			// New Start 20151221 SonBX
			mFileType = params[1];
			// New End 20151221 SonBX
			// URL url = new URL(params[0]);
			InputStream ins = retrieveStream(params[0]);
			// read data
			
			// int fileTypeDownload = Integer.valueOf(params[1]);
			String filePath = SessionData.getFolderApp() + "/" + fileName;
			OutputStream output = new FileOutputStream(filePath);
			DebugOption.info("filePath", filePath);
			while ((count = ins.read(data)) != -1) {
				 DebugOption.info("data", "length:" + data.length);
				if (!SessionData.isDownload()) {
					return "";
				} else {
					output.write(data, 0, count);
				}

			 }

			// flushing output
			 output.flush();

			// closing streams
			output.close();

			// // change filename
			// if (filePath.endsWith(".mp3")) {
			// MediaMetadataRetriever retriever = new MediaMetadataRetriever();
			// retriever.setDataSource(filePath);
			// String title =
			// retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
			// if (title != null && !title.equals("")) {
			// // rename file
			// String newFilename;
			// if (fileName.contains("(")) {
			// newFilename = title + fileName.substring(fileName.indexOf("("));
			// } else {
			// newFilename = title + ".mp3";
			// }
			// File oriFile = new File(SessionData.getFolderApp(), fileName);
			// oriFile.renameTo(new File(SessionData.getFolderApp(),
			// newFilename));
			// fileName = newFilename;
			// filePath = SessionData.getFolderApp() + "/" + fileName;
			// }
			// }

			// Insert to DB
			String bookName = fileName;
			bookName = bookName.replace(Define.EXTENSION_AUDIO, "");
			bookName = bookName.replace(Define.EXTENSION_TEXT, "");
			if (bookName.contains("(")) {
				bookName = bookName.substring(0, bookName.lastIndexOf("("));
			}
			// New Start 20151223 SonBX
			// In case of beginnerEnglish_201511_xx.mp3
			else {
				if(fileName.contains(Define.EXTENSION_AUDIO)) {
					if(bookName.contains("_")) {
						bookName = bookName.substring(0, bookName.lastIndexOf("_"));
					}
				}
			}
			// In case of beginnerEnglish_201511_Text.zip
			if(bookName.contains("_Text")) {
				bookName = bookName.replace("_Text", "");
			} else 
			// In case of beginnerEnglish_201511_text.zip
			if(bookName.contains("_text")) {
				bookName = bookName.replace("_text", "");
			}
			// In case of beginnerEnglish_201511_Audio_1.zip
//			else {
				if(bookName.contains("_Audio")) {
					bookName = bookName.substring(0,  bookName.lastIndexOf("_Audio"));
				} else if(bookName.contains("_audio")) {
					// In case of beginnerEnglish_201511_audio_1.zip
					bookName = bookName.substring(0,  bookName.lastIndexOf("_audio"));
				}
//			}
			
			// New End 20151223 SonBX
			DebugOption.info("bookName save", "bookName = " + bookName);
			// New Start 20160106 SonBX
			if(!mFileType.equals(Define.FileTypeDownload.ZIP_MP3)) {
				// New End 20160106 SonBX
				bookId = BookTable.searchBook(SessionData.getDb(), bookName);
				if (bookId == 0) {
					// Insert new book
					// Mod Start 20151223 SonBX
	//				BookEntity bookEntity = new BookEntity(bookName, 0, "", "", "");
					// Mod 20151223
					BookEntity bookEntity = new BookEntity(bookName, bookName, 0, "", "", "");
					// Mod End 20151223 SonBX
					bookId = (int) BookTable.insert(SessionData.getDb(), bookEntity);
				}
			}
			// Insert audio file
			if (fileName.contains(Define.EXTENSION_AUDIO)) {
				if (FilesTable.searchFileExistPath(SessionData.getDb(), filePath) == Define.DEFAULT_INT) {
					FileEntity entity = new FileEntity(fileName, params[0], fileName, filePath,
							TimeUtils.getCurrenttime(), bookId, 0, Define.FileType.AUDIO, "", "",
							"");
					FilesTable.insert(SessionData.getDb(), entity);
				}

			}

			return filePath;
		} catch (IOException e) {
			return Define.DEFAULT_STRING;
		}
	}


	@Override
	protected void onPostExecute(final String result) {
		super.onPostExecute(result);
		if (mPD != null && mPD.isShowing()) {
			mPD.dismiss();
		}
		if (!result.equals(Define.DEFAULT_STRING)) {
			Toast.makeText(
					mContext,
					mContext.getResources().getString(
							R.string.download_successfully), Toast.LENGTH_SHORT)
					.show();
			DebugOption.info("FILE PATH", filePath);
			// data length
			if (data.length == 0) {
				DebugOption.info("NULL", "NULL");
			}
			
			// Mod Start 20151009 SonBX
//			if (result.contains(Define.EXTENSION_TEXT)) {
//				new DecryptData().execute(result);
//			}
			// Mod 20151009
			// Mod Start 20151221 SonBX
//			if(mURL.contains(Define.LinkType.SOUND_LINK)) {
			// Mod 20151221
			if(mFileType.equals(Define.FileTypeDownload.ZIP_MP3)) {
			// Mod End 20151221 SonBX
				// Unzip
				new UnZip().execute(result);
			// Mod Start 20151221 SonBX
//			} else if(mURL.contains(Define.LinkType.TEXT_LINK)) {
			// Mod 20151221
			} else if(mFileType.equals(Define.FileTypeDownload.ZIP_TEXT)) {
			// Mod End 20151221 SonBX
				new DecryptData().execute(result);
			}
			// Mod End 20151009 SonBX
		} else {
			Toast.makeText(
					mContext,
					mContext.getResources().getString(R.string.download_failed),
					Toast.LENGTH_SHORT).show();
		}
	}

	// get InputStream Data from URL
	public InputStream retrieveStream(String url) {

		DefaultHttpClient client = new DefaultHttpClient();

		// cookie
		String cookieStr = CookieManager.getInstance().getCookie(url);
		BasicClientCookie cookie = new BasicClientCookie("Cookie", cookieStr);
		CookieStore cookieStore = new BasicCookieStore();
		cookieStore.addCookie(cookie);
		client.setCookieStore(cookieStore);

		HttpGet httpRequest = new HttpGet(url);
		httpRequest.addHeader("X_UDID", FileIO.md5(SessionData.getmUUID()));
		httpRequest.addHeader("Cookie", cookieStr);
		DebugOption.info("RetrieveStream", "HttpRequest");
		try {
			HttpResponse httpResponse = client.execute(httpRequest);

			Header[] headers = httpResponse.getAllHeaders();
			for (Header header : headers) {
				DebugOption.info("TAG",
						"name : " + header.getName() + ", value = " + header.getValue());
				
				if (header.getName().equalsIgnoreCase("Content-Disposition")) {
					String value = header.getValue();
					fileName = value.substring(value.lastIndexOf("=") + 1);
					DebugOption.info("fileName", fileName);
					fileName = fileName.substring(fileName.lastIndexOf("'") + 1);
					DebugOption.info("fileName2", fileName);
				}
				// New Start 20151224 SonBX
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
				}
				// New End 20151224 SonBX
			}
			if(mFileType.equals(Define.FileTypeDownload.ZIP_TEXT) && (fileName.contains("_audio") || fileName.contains("_Audio"))) {
				mFileType = Define.FileTypeDownload.ZIP_MP3;
			}
			DebugOption.info("fileType", mFileType);
			// New Start 20151229 SonBX
			// In case of file name not described in header
			if(fileName.equals("")) {
				fileName = url.substring(url.lastIndexOf("/") + 1);
			}
			DebugOption.info("FileName", fileName);
			// New End 20151229 SonBX

			final int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				DebugOption.warning(getClass().getSimpleName(), "Error => " + statusCode
						+ " => for URL "
						+ url);
				return null;
			}

			HttpEntity httpEntity = httpResponse.getEntity();
			return httpEntity.getContent();

		} catch (IOException e) {
			httpRequest.abort();
		}

		return null;

	}
	
	class DecryptData extends AsyncTask<String, Void, String> {
		// private OutputStream mOutputStream;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDzip = new ProgressDialog(mContext);
			progressDzip.setTitle("");
			progressDzip.setMessage(mContext.getResources().getString(
					R.string.please_wait_decrypting));
			progressDzip.setCancelable(false);
			progressDzip.show();
		}

		@Override
		protected String doInBackground(String... params) {
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
				mOutputStream.write(encodeData, 0, encodeData.length);
				DebugOption.info("save", "save");
			 } catch (FileNotFoundException e) {
			 e.printStackTrace();
			 } catch (IOException e) {
			 e.printStackTrace();
			 }
			
			// folderTempName);
			 try {
			 mOutputStream.flush();
			 mOutputStream.close();
			 } catch (IOException e) {
			 e.printStackTrace();
			 }
			
			// folderTempName.replace("/", "-");
			 
			String folder = folderTempName.replace(".zip", "");
			DebugOption.info("folderTempName", folderTempName);
			Decompress decompress = new Decompress(folderTempName, folder);
			decompress.unzipj4();

			return folder;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			DebugOption.info("List and save to database ", "*****************************");
			ArrayList<String> list = FileIO.getListFileFromFolder(result);
			for (String file : list) {
				DebugOption.info("FileName : ", file);
				String fileName = result.substring(result.lastIndexOf("/") + 1);
				// New Start 20151224 SonBX
				String newBookName = "";
				if (file.contains(Define.FILE_NAME)) {
					// Read text file for getting book name
					File f = new File(file);
					try {
						BufferedReader br = new BufferedReader(new FileReader(f));
						String line = br.readLine();
						br.close();
						if (line != null) {
							newBookName = line;
							DebugOption.debug("NewBookName", newBookName);
						}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					// Change book name
					if (!newBookName.equals("")) {
						BookTable.changeDisplayName(SessionData.getDb(), bookId, newBookName);
					}
					continue;
				}
				// New End 20151224 SonBX
				if (FilesTable.searchFileExistPath(SessionData.getDb(), file) == Define.DEFAULT_INT) {
					FileEntity entity = new FileEntity(fileName, file, fileName, file,
							TimeUtils.getCurrenttime(), bookId, 0, Define.FileType.TEXT, "", "", "");
					FilesTable.insert(SessionData.getDb(), entity);
				} else {
					DebugOption.info("Exist", "Exist");
				}
			}
			progressDzip.dismiss();
		}
	}
	
	// New Start 20151009 SonBX
	class UnZip extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			String folder = params[0].replace(".zip", "");
			DebugOption.info("folder", folder);
			Decompress decompress = new Decompress(params[0], folder);
			// Mod Start 20161125 SonBX
			//decompress.unzipj4();
			// Mod 20161125
			decompress.unzip();
			// Mod End 20161125 SonBX
			
			// Delete zip file
			DebugOption.info("folder", "Delete zip file after the unzip: " + params[0]);
			FileIO.deleteFromInternalStorage(params[0]);
			return folder;
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			// Create new folder
			String tempFolderName = result.substring(result.lastIndexOf("/") + 1);
			int folderID = FolderTable.checkExistByTempName(SessionData.getDb(), tempFolderName);
			if(folderID == Define.DEFAULT_INT) {
				DebugOption.info("Create new folder", "*****************************");
				FolderEntity folderEntity = new FolderEntity(tempFolderName, 0, TimeUtils.getCurrenttime(), TimeUtils.getCurrenttime(), "", tempFolderName, 0);
				folderID = (int)FolderTable.newFolder(SessionData.getDb(), folderEntity, 0);
			} else {
				// This folder will can not be re-downloaded
				DebugOption.info("Folder", "Set can Redownload: 0");
				FolderTable.setCanReDownload(SessionData.getDb(), folderID, 0);
			}
			// Save mp3 files to database
			DebugOption.info("Save mp3 files to database ", "*****************************");
			ArrayList<String> list = FileIO.getListFileFromFolder(result);
//			String newFolderName = "";
			boolean renameFolderName = true;
			boolean createBook = true;
			for (String file : list) {
				DebugOption.info("FileName : ", file);
				String fileName = file.substring(file.lastIndexOf("/") + 1);
				// New Start 20160615 NamHV
				// Convert from cp1252 to utf8
				fileName = StringUtils.convertCp1252ToShiftJIS(fileName);
				// New End 20160615 NamHV
				
//				if(file.contains(Define.FILE_NAME)) {
//					// Read text file for getting folder name
//					File f = new File(file);
//					try {
//						BufferedReader br = new BufferedReader(new FileReader(f));
//						String line = br.readLine();
//						br.close();
//						if(line != null) {
//							newFolderName = line;
//						}
//					} catch (FileNotFoundException e) {
//						e.printStackTrace();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//					// Change folder name
//					if(!newFolderName.equals("")) {
//						FolderTable.updateFilename(SessionData.getDb(), folderID, newFolderName);
//					}
//					continue;
//				}
				// New Start 20160106 SonBX
				if(createBook) {
					if(file.endsWith(Define.EXTENSION_AUDIO)) {
						String bookName = fileName;
						bookName = bookName.replace(Define.EXTENSION_AUDIO, "");
						if (bookName.contains("(")) {
							bookName = bookName.substring(0, bookName.lastIndexOf("("));
						}
						// In case of beginnerEnglish_201511_xx.mp3
						else {
							if(bookName.contains("_")) {
								bookName = bookName.substring(0, bookName.lastIndexOf("_"));
							}
						}
						DebugOption.debug("BookName", bookName);
						bookId = BookTable.searchBook(SessionData.getDb(), bookName);
						if (bookId == 0) {
							// Insert new book
							BookEntity bookEntity = new BookEntity(bookName, bookName, 0, "", "", "");
							bookId = (int) BookTable.insert(SessionData.getDb(), bookEntity);
							createBook = false;
						}
					}
				}
				// New End 20160106 SonBX
				if(renameFolderName) {
					if (file.endsWith(Define.EXTENSION_AUDIO)) {
						MediaMetadataRetriever retriver = new MediaMetadataRetriever();
						retriver.setDataSource(file);
						String album = retriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
						DebugOption.debug("Album", album + "");
						if (album != null && !album.equals("")) {
							FolderTable.updateFilename(SessionData.getDb(), folderID, album);
							renameFolderName = false;
						}
					}
//					File f = new File(file);
//					MusicMetadataSet metadataSet = null;
//					try {
//						metadataSet = new MyID3().read(f);
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//					if(metadataSet != null) {
//						IMusicMetadata metadata = metadataSet.getSimplified();
//						String album = metadata.getAlbum();
//						DebugOption.debug("Album", album + "");
//						if(album != null && !album.equals("")) {
//							FolderTable.updateFilename(SessionData.getDb(), folderID, album);
//							renameFolderName = false;
//						}
//					}
				}
				
				if (FilesTable.searchFileExistPath(SessionData.getDb(), file) == Define.DEFAULT_INT) {
					FileEntity entity = new FileEntity(fileName, file, fileName, file,
							TimeUtils.getCurrenttime(), bookId, folderID, Define.FileType.AUDIO, TimeUtils.getCurrenttime(), TimeUtils.getCurrenttime(), "");
					FilesTable.insert(SessionData.getDb(), entity);
				} else {
					DebugOption.info("Exist", "Exist");
				}
			}
		}
	}
	// New End 20151009 SonBX
}
