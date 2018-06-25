package jp.co.marinax.fileplayer.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import jp.co.marinax.fileplayer.app.config.DebugOption;
import jp.co.marinax.fileplayer.app.config.Define;
import jp.co.marinax.fileplayer.io.db.table.BookTable;
import jp.co.marinax.fileplayer.io.db.table.FilesTable;
import jp.co.marinax.fileplayer.io.entity.BookEntity;
import jp.co.marinax.fileplayer.io.entity.FileEntity;
import jp.co.marinax.fileplayer.io.save.SessionData;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.telephony.TelephonyManager;

public class FileIO {

	// save byte[] to file
	public static boolean saveFileToSdcard(String filePath, byte[] data) {
		try {
			FileOutputStream mOut = new FileOutputStream(filePath);
			mOut.write(data, 0, data.length);
			mOut.flush();
			mOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	// get list file from folder
	public static ArrayList<String> getListFile(String folderPath) {
		ArrayList<String> nameList = new ArrayList<String>();
		File folder = new File(folderPath);
		for (File f : folder.listFiles()) {
			if (f.isFile()) {
				nameList.add(f.getAbsolutePath());
			}
		}
		return nameList;
	}

	// get list File from folder have a subFolder
	
	public static ArrayList<String> getListFileFromFolder(String folderPath) {
		ArrayList<String> nameList = new ArrayList<String>();
		
		File folder = new File(folderPath);
		for (File file : folder.listFiles()) {
			if(file.isFile()){
				nameList.add(file.getAbsolutePath());
			} else if (file.isDirectory()) {
				nameList.addAll(getListFile(file.getAbsolutePath()));
			}
		}
		return nameList;
	}

	// get uuid
	public static String getUUId(Context context) {
		TelephonyManager tManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String uuid = tManager.getDeviceId();
		return uuid;
	}

	public static ArrayList<Bitmap> getAllImages(String folder) {
		ArrayList<Bitmap> arrBitmap = new ArrayList<Bitmap>();
		File file = new File(SessionData.getFolderApp());
		File[] list = file.listFiles();
		for (int i = 0; i < list.length; i++) {
			DebugOption.info("Image: " + i + ": path",
					list[i].getAbsolutePath());

			Bitmap b = BitmapFactory.decodeFile(list[i].getAbsolutePath());
			arrBitmap.add(b);
		}
		return arrBitmap;
	}

	public static boolean createFolderApp(Context context) {
		File myFilesDir = new File(SessionData.getFolderApp());
		DebugOption.info("exist", SessionData.getFolderApp());
		if (!myFilesDir.exists()) {
			DebugOption.info("not exist", "not exist");
			return myFilesDir.mkdirs();
		}
		return false;
	}

	/**
	 * Copy all files in "assetFolder" to a folder in SDCard
	 * 
	 * @param assetFolder
	 * @param desPath
	 * @param context
	 * @return
	 */
//	public static boolean copyAssetsToSDCard(String assetFolder,
//			String desPath, Context context) {
//		AssetManager assetManager = context.getAssets();
//		String[] arrData;
//		try {
//			arrData = assetManager.list(assetFolder);
//			int size = arrData.length;
//
//			// Add 2 book to DB
//			BookEntity bookEntity1 = new BookEntity(1, "english-20140110", 0,
//					"", "", "");
//			BookTable.insert(SessionData.getDb(), bookEntity1);
//			BookEntity bookEntity2 = new BookEntity(2, "vietnamese-20140215",
//					0, "", "", "");
//			BookTable.insert(SessionData.getDb(), bookEntity2);
//
//			for (int i = 0; i < size; i++) {
//				String fileName = arrData[i];
//				InputStream in = assetManager
//						.open(assetFolder + "/" + fileName);
//				File to = new File(desPath, fileName);
//				OutputStream out = new FileOutputStream(to);
//				byte[] buff = new byte[1024];
//				int len;
//				while ((len = in.read(buff)) > 0) {
//					out.write(buff, 0, len);
//				}
//				in.close();
//				out.close();
//
//				if (fileName.contains("english-20140110_text")) {
//					FileEntity entity = new FileEntity("english-20140110",
//							desPath + "/" + fileName, fileName, desPath + "/"
//									+ fileName, TimeUtils.getCurrenttime(), 1,
//							0, Define.FileType.TEXT, "", "", "");
//					FilesTable.insert(SessionData.getDb(), entity);
//				} else if (fileName.contains("english-20140110_audio")) {
//					FileEntity entity = new FileEntity("english-20140110",
//							desPath + "/" + fileName, fileName, desPath + "/"
//									+ fileName, TimeUtils.getCurrenttime(), 1,
//							0, Define.FileType.AUDIO, "", "", "");
//					FilesTable.insert(SessionData.getDb(), entity);
//				} else if (fileName.contains("vietnamese-20140215_text")) {
//					FileEntity entity = new FileEntity("vietnamese-20140215",
//							desPath + "/" + fileName, fileName, desPath + "/"
//									+ fileName, TimeUtils.getCurrenttime(), 2,
//							0, Define.FileType.TEXT, "", "", "");
//					FilesTable.insert(SessionData.getDb(), entity);
//				} else if (fileName.contains("vietnamese-20140215_audio")) {
//					FileEntity entity = new FileEntity("vietnamese-20140215",
//							desPath + "/" + fileName, fileName, desPath + "/"
//									+ fileName, TimeUtils.getCurrenttime(), 2,
//							0, Define.FileType.AUDIO, "", "", "");
//					FilesTable.insert(SessionData.getDb(), entity);
//				}
//			}
//			return true;
//		} catch (IOException e1) {
//			e1.printStackTrace();
//			return false;
//		}
//	}

	/**
	 * Delete file by File Path
	 * 
	 * @param filePath
	 */
	public static void deleteFile(String filePath) {
		File file = new File(filePath);
		if (file.isDirectory()) {
			String[] children = file.list();
			for (int i = 0; i < children.length; i++) {
				deleteFile(filePath + "/" + children[0]);
			}
		} else {
			DebugOption.info("delete", filePath);
			file.delete();
		}
	}
	
	public static void deleteFolder(File dir) {
		for (File file : dir.listFiles()) {
			if (file.isDirectory()) deleteFolder(file);
			file.delete();
		}
		dir.delete();
	}
	
	public static byte[] getByteFromfile(String path) {
		File file = new File(path);
		int size = 0;
		if (file.exists()) {
			size = (int) file.length();

			byte[] bytes = new byte[size];

			try {
				BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
				buf.read(bytes, 0, bytes.length);
				buf.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return bytes;
		}
		return new byte[0];
	}

	public static String md5(String s) {
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++)
				hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	/*************************************************************
	 * get and insert internal memory
	 *************************************************************/
	public static void saveInternalStorage(Context mContext, byte[] data, String fileName) {
		fileName = fileName.replace("/", "-");
				
		FileOutputStream  outputStream;
		try {
			outputStream = mContext.openFileOutput(fileName, Context.MODE_PRIVATE);
			outputStream.write(data);
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static File getFromInternalStorage(Context context, String url) {
		File file = null;
		try {
			String fileName = Uri.parse(url).getLastPathSegment();
			file = File.createTempFile(fileName, null, context.getCacheDir());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}

	public static boolean deleteFromInternalStorage(String fileName) {
		File file = new File(fileName);
		boolean deleted = file.delete();
		return deleted;
	}
}
