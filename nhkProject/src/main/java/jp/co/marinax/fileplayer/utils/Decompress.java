package jp.co.marinax.fileplayer.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.util.Log;
import jp.co.marinax.fileplayer.app.config.DebugOption;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class Decompress {
	private String _zipFile;
	private String _location;

	public Decompress(String zipFile, String location) {
		DebugOption.info("zipFile", zipFile + "+++" + location);
		_zipFile = zipFile;
		_location = location;
		_dirChecker("");
	}

	// public void unzip() {
	// try {
	// FileInputStream fin = new FileInputStream(_zipFile);
	// ZipInputStream zin = new ZipInputStream(fin);
	// ZipEntry ze = null;
	// while ((ze = zin.getNextEntry()) != null) {
	// Log.v("Decompress", "Unzipping " + ze.getName());
	//
	// if (ze.isDirectory()) {
	// // _dirChecker(ze.getName());
	// } else {
	// String name = ze.getName();
	// File outputFile = new File(_location + "/" + name);
	// String ouputPath = outputFile.getCanonicalPath();
	// name = ouputPath.substring(ouputPath.lastIndexOf("/") + 1);
	// ouputPath = ouputPath.substring(0, ouputPath.lastIndexOf("/"));
	// File outputDir = new File(ouputPath);
	//
	// outputDir.mkdirs();
	// outputFile = new File(ouputPath + "/" + name);
	// outputFile.createNewFile();
	//
	// FileOutputStream fout = new FileOutputStream(outputFile);
	// // for (int c = zin.read(); c != -1; c = zin.read()) {
	// // fout.write(c);
	// // }
	//
	// byte[] buffer = new byte[256];
	// int length;
	// // replace for loop with:
	// while ((length = zin.read(buffer)) > 0) {
	// fout.write(buffer, 0, length);
	// }
	//
	//
	// zin.closeEntry();
	// fout.close();
	// }
	//
	// }
	// zin.close();
	// } catch (Exception e) {
	// Log.e("Decompress", "unzip", e);
	// }
	//
	// }

	public void unzipj4() {
		// String source = "folder/source.zip";
		// String destination = "folder/source/";
		try {
			ZipFile zipFile = new ZipFile(_zipFile);
			zipFile.extractAll(_location);
		} catch (ZipException e) {
			e.printStackTrace();
		}
	}

	public void unzip() { 
	    try  { 
	      FileInputStream fin = new FileInputStream(_zipFile); 
	      ZipInputStream zin = new ZipInputStream(fin); 
	      ZipEntry ze = null; 
	      while ((ze = zin.getNextEntry()) != null) { 
	        DebugOption.info("Decompress", "Unzipping " + ze.getName()); 
	 
	        if(ze.isDirectory()) { 
	          _dirChecker(ze.getName()); 
	        } else { 
	          FileOutputStream fout = new FileOutputStream(_location + "/" + ze.getName());
	          byte[] buffer = new byte[32 * 1024]; // play with sizes..
	          int readCount;
	          while ((readCount = zin.read(buffer)) != -1) {
	              fout.write(buffer, 0, readCount);
	          }
	 
	          zin.closeEntry(); 
	          fout.close(); 
	        } 
	         
	      } 
	      zin.close(); 
	    } catch(Exception e) { 
	      Log.e("Decompress", "unzip", e); 
	    } 
	 
	  } 

	private void _dirChecker(String dir) {
		File f = new File(_location + "/" + dir);

		if (!f.isDirectory()) {
			f.mkdir();
			Log.v("Decompress", "Mkdir: " + f.getAbsolutePath()); 
		}
	}
}