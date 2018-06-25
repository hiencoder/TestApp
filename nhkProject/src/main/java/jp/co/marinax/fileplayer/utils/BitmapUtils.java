package jp.co.marinax.fileplayer.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapUtils {
	public static Bitmap decodeBitmapWithScale(String path, int requiredSize) {
		File file = new File(path);
		if (file.exists()) {
			FileInputStream fis;
			try {
				fis = new FileInputStream(file);

				// Decode image size
				BitmapFactory.Options o = new BitmapFactory.Options();
				o.inJustDecodeBounds = true;

				BitmapFactory.decodeStream(fis, null, o);
				fis.close();

				int scale = 1;
				// final int REQUIRED_SIZE = requiredSize;
				// if (o.outHeight > REQUIRED_SIZE || o.outWidth >
				// REQUIRED_SIZE) {
				// scale = (int) Math.pow(
				// 2,
				// (int) Math.round(Math.log(REQUIRED_SIZE
				// / (double) Math.max(o.outHeight, o.outWidth))
				// / Math.log(0.5)));
				// }

				// Decode with inSampleSize
				BitmapFactory.Options o2 = new BitmapFactory.Options();
				o2.inSampleSize = scale;
				System.out.println("Scale: " + scale);
				fis = new FileInputStream(file);
				Bitmap thumbnail = BitmapFactory.decodeStream(fis, null, o2);
				fis.close();
				return thumbnail;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return null;
	}
}
