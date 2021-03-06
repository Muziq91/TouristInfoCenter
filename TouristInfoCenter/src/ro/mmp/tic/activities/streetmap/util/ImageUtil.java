package ro.mmp.tic.activities.streetmap.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class ImageUtil {

	protected Context context;

	public ImageUtil() {
	}

	public ImageUtil(Context context) {
		this.context = context;

	}

	// saves image to internal storage
	public boolean saveImageToInternalStorage(Bitmap image, String imageName) {

		try {
			// Use the compress method on the Bitmap object to write image to
			// the OutputStream
			@SuppressWarnings("static-access")
			FileOutputStream fos = context.openFileOutput(imageName + ".png",
					context.MODE_PRIVATE);

			// Writing the bitmap to the output stream
			image.compress(Bitmap.CompressFormat.PNG, 100, fos);

			fos.close();
			return true;
		} catch (Exception e) {
			Log.e("ImageUtil", e.getMessage());
			return false;
		}
	}

	// gets the iamge from internal storage
	public Bitmap getThumbnail(String filename) {

		Bitmap thumbnail = null;

		// If no file on external storage, look in internal storage
		if (thumbnail == null) {
			try {
				File filePath = context.getFileStreamPath(filename);
				FileInputStream fi = new FileInputStream(filePath);
				thumbnail = BitmapFactory.decodeStream(fi);
			} catch (Exception ex) {
				String err = (ex.getMessage() == null) ? "SD Card failed" : ex
						.getMessage();
				Log.e("ImageUtil", err);
			}
		}
		return thumbnail;
	}

}
