/**
 * @author Matei Mircea
 * 
 * Used to help with the download of the google iamge
 */

package ro.mmp.tic.activities.streetmap.util;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import ro.mmp.tic.adapter.model.MapModel;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class GoogleImageUtil extends ImageUtil {

	private Context context;
	private ArrayList<MapModel> mapModel;

	public GoogleImageUtil(ArrayList<MapModel> mapModel, Context context) {
		this.context = context;
		this.mapModel = mapModel;
	}

	public void downloadImage(double lat, double lng, int width, int height,
			int zoom) {

		try {
			String url = "http://maps.google.com/maps/api/staticmap?center="
					+ createMarkers(lat, lng) + "&zoom=" + zoom + "&size="
					+ width + "x" + height
					+ "&maptype=roadmap&sensor=false&path="
					+ createPath(lat, lng);

			Log.d("URl is", url);
			Drawable urlImage = LoadImageFromWebOperations(url);
			Log.d("GoogleimageUtil", "DownloadImage");
			Bitmap bmp = ((BitmapDrawable) urlImage).getBitmap();
			Log.d("GoogleimageUtil", "Create bitmap");
			if (bmp != null) {
				super.context = context;
				saveImageToInternalStorage(bmp, "mapImage");
			}
			Log.d("GoogleimageUtil", "Save to internal storage");
		} catch (Exception e) {
			Log.d("GoogleImageUtil ERROR", "ERROR");
		}
	}

	public static Drawable LoadImageFromWebOperations(String url) {
		try {
			InputStream is = (InputStream) new URL(url).getContent();
			Drawable d = Drawable.createFromStream(is, "src name");
			return d;
		} catch (Exception e) {
			return null;
		}
	}

	private String createPath(double lat, double lng) {
		String path = "";
		path = lat + "," + lng;
		for (MapModel m : mapModel) {
			path = path + "|" + m.getCoordinate().getLatitude() + ","
					+ m.getCoordinate().getLongitude() + "|" + lat + "," + lng;
		}

		return path;
	}

	private String createMarkers(double lat, double lng) {

		String markers = lat + "," + lng;

		for (MapModel m : mapModel) {
			markers = markers + "&markers=color:" + m.getColor() + "%7Clabel:"
					+ m.getTopic().getName().substring(0, 1) + "%7C"
					+ m.getCoordinate().getLatitude() + ","
					+ m.getCoordinate().getLongitude();
		}

		return markers;
	}

	public ArrayList<MapModel> getMapModel() {
		return mapModel;
	}

	public void setMapModel(ArrayList<MapModel> mapModel) {
		this.mapModel = mapModel;
	}

}
