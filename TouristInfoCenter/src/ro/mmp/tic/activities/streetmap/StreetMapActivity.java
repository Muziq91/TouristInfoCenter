/**
 * @author Matei Mircea
 * 
 * This class is used to create the street view map. It uses the metaioSDK and it is a subclass of ARViewActivity
 */

package ro.mmp.tic.activities.streetmap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import ro.mmp.tic.R;
import ro.mmp.tic.activities.streetmap.fragment.LocationFragment;
import ro.mmp.tic.adapter.model.MapModel;
import ro.mmp.tic.metaio.ARViewActivity;
import ro.mmp.tic.service.sqlite.DataBaseConnection;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.metaio.sdk.SensorsComponentAndroid;
import com.metaio.sdk.jni.IBillboardGroup;
import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.IMetaioSDKCallback;
import com.metaio.sdk.jni.IRadar;
import com.metaio.sdk.jni.LLACoordinate;
import com.metaio.tools.io.AssetsManager;

public class StreetMapActivity extends ARViewActivity implements
		SensorsComponentAndroid.Callback {

	private final String TAG = "StreetMapActivity";
	private ArrayList<MapModel> mapModel;
	private IBillboardGroup billboardGroup;

	/**
	 * radar component
	 */

	private IRadar radar;

	/**
	 * We override the onCreate method to set up the tracking configuration
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/**
		 * The gps tracking configuration will be set on the user interface
		 * thread
		 */

		// create directories before extracting the assets

		/**
		 * We need first to extract all assets so we can access them. Otherwise
		 * metaio's AssetsManager will give out a null value when trying to
		 * access a file
		 */
		try {
			String path = getApplicationContext().getFilesDir()
					.getAbsolutePath() + "/streetmap";
			File f = new File(path);
			f.mkdirs();
			AssetsManager.extractAllAssets(getApplicationContext(),
					"streetmap", true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mapModel = new ArrayList<MapModel>(0);
		DataBaseConnection dbc = new DataBaseConnection(this);
		mapModel = dbc.getMapModel(LocationFragment.getSelectedLocation());

	}

	/**
	 * We determine what happens when the activity is paused
	 */

	@Override
	protected void onPause() {
		super.onPause();

		if (mSensors != null) {
			mSensors.registerCallback(null);

		}
	}

	/**
	 * When the applciation resumes
	 */

	@Override
	protected void onResume() {
		super.onResume();

		/**
		 * we want to receive sensor updates
		 */
		if (mSensors != null) {
			mSensors.registerCallback(this);
		}
	}

	@Override
	public void onLocationSensorChanged(LLACoordinate location) {
		/**
		 * we update the activity when location changes
		 */
		updateGeometriesLocation(mSensors.getLocation());
	}

	@Override
	protected int getGUILayout() {

		/**
		 * here we load the gui for the application
		 */
		return R.layout.activity_streetmap;
	}

	@Override
	protected IMetaioSDKCallback getMetaioSDKCallbackHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * we override this class to load our assets
	 */
	@Override
	protected void loadContents() {

		/**
		 * We use this method to load all the necessary content
		 */
		loadGPSInformation();

	}

	/**
	 * This method will load all geometrical coordinates and the necessary GPS
	 * content
	 */
	private void loadGPSInformation() {

		try {

			/**
			 * set the sign for each geometry
			 */

			billboardGroup = metaioSDK.createBillboardGroup(720, 850);
			billboardGroup.setBillboardExpandFactors(1, 5, 30);
			metaioSDK.setRendererClippingPlaneLimits(50, 100000000);
			metaioSDK.setLLAObjectRenderingLimits(10, 10000); // to reduce
																// flickering

			for (MapModel m : mapModel) {

				m.setGeometry(metaioSDK.createGeometryFromImage(createSign(m
						.getTopic().getName()), true));
				m.getGeometry().setName(m.getTopic().getName());
				billboardGroup.addBillboard(m.getGeometry());
			}

			/**
			 * build the location of our interest points
			 */
			updateGeometriesLocation(mSensors.getLocation());

			/**
			 * Create the radar
			 */

			radar = metaioSDK.createRadar();
			radar.setBackgroundTexture(AssetsManager
					.getAssetPath("streetmap/radar.png"));

			radar.setRelativeToScreen(IGeometry.ANCHOR_TL);

			/**
			 * add geometries to the radar
			 */

			for (MapModel m : mapModel) {
				radar.add(m.getGeometry());

				String file = AssetsManager.getAssetPath("streetmap/"
						+ m.getColor() + ".png");
				radar.setObjectTexture(m.getGeometry(), file);

				m.getGeometry().setVisible(false);

			}

			Log.i(TAG, "Set up everything ");

		} catch (Exception e) {

		}

	}

	/**
	 * When the user click on the gps button we make everything visible
	 * 
	 * @param view
	 */

	public void onGPSBUttonClick(View view) {

		@SuppressWarnings("unused")
		boolean result = metaioSDK.setTrackingConfiguration("GPS");

		radar.setVisible(true);

		for (MapModel m : mapModel) {
			m.getGeometry().setVisible(true);
		}

	}

	/**
	 * This method has the responsability of updating the location on the radar
	 * It uses an offset to get a more precise location
	 * 
	 * @param location
	 */
	private void updateGeometriesLocation(LLACoordinate location) {

		Log.i(TAG, "Update geometry locations ");

		LLACoordinate currPos = mSensors.getLocation();

		for (MapModel mm : mapModel) {
			mm.setCoordinate(new LLACoordinate(mm.getTopic().getLat(), mm
					.getTopic().getLng(), currPos.getAltitude(), currPos
					.getAltitude(), currPos.getAccuracy()));

			if (mm.getGeometry() != null) {
				mm.getGeometry().setTranslationLLA(mm.getCoordinate());
				mm.getGeometry().setLLALimitsEnabled(true);
			}

		}

	}

	@Override
	protected void onGeometryTouched(IGeometry geometry) {

		Intent i = getIntent();
		String username = i.getStringExtra("loggedUser");

		if (geometry.getName().equals("Botanical")) {
			toastMessage("You accessed the Botanical Garden Page");
			Intent intent = new Intent(StreetMapActivity.this,
					PresentationActivity.class);
			intent.putExtra("loggedUser", username);
			intent.putExtra("name", "Botanical Garden");
			startActivity(intent);

		} else if (geometry.getName().equals("Statue")) {
			toastMessage("You accessed the Matei Corvin Statue page");
			Intent intent = new Intent(StreetMapActivity.this,
					PresentationActivity.class);
			intent.putExtra("loggedUser", username);
			intent.putExtra("name", "Matei Corvin Statue");
			startActivity(intent);
		} else if (geometry.getName().equals("Cathedral")) {
			toastMessage("You accessed the Orthodox Cathedral Page \n Accros from it you can see the National Theatre");
			Intent intent = new Intent(StreetMapActivity.this,
					PresentationActivity.class);
			intent.putExtra("loggedUser", username);
			intent.putExtra("name", "Orthodox Cathedral");
			startActivity(intent);
		} else if (geometry.getName().equals("Bastion")) {
			toastMessage("You accessed the Bastionul Croitorilor page");
			Intent intent = new Intent(StreetMapActivity.this,
					PresentationActivity.class);
			intent.putExtra("loggedUser", username);
			intent.putExtra("name", "Bastionul Croitorilor");
			startActivity(intent);
		}

	}

	/**
	 * This method creates the sign the suer sees on the device screen while
	 * using this activity
	 * 
	 * @param title
	 * @return
	 */
	private String createSign(String title) {

		try {

			final String texture = getCacheDir() + "/" + title + ".png";
			Paint paint = new Paint();
			/**
			 * The background image is POI_bg2
			 */

			Bitmap sign = null;
			/**
			 * Get the image from the assets folder
			 */

			String file = AssetsManager.getAssetPath("streetmap/POI_bg2.png");
			Bitmap background = BitmapFactory.decodeFile(file);

			sign = background.copy(Bitmap.Config.ARGB_8888, true);

			Canvas canvas = new Canvas(sign);

			paint.setColor(Color.WHITE);
			paint.setTextSize(24);
			paint.setTypeface(Typeface.DEFAULT);

			float x = 30, y = 40;
			/**
			 * Now we draw the name onto the sign
			 */

			if (title.length() > 0) {

				/**
				 * it removes the white spaces from the initial String
				 */
				String trim = title.trim();

				final int width = 200;
				/**
				 * we make sure that no text extends outside the rectangle
				 */
				int extend = paint.breakText(trim, true, width, null);

				canvas.drawText(trim.substring(0, extend), x, y, paint);
				/**
				 * if valid we will draw the second line
				 */

				if (extend < trim.length()) {
					trim = trim.substring(extend);
					y += 20;
					extend = paint.breakText(trim, true, width, null);

					if (extend < trim.length()) {
						extend = paint.breakText(trim, true, width - 20, null);
						canvas.drawText(trim.substring(0, extend) + "...", x,
								y, paint);
					} else {
						canvas.drawText(trim.substring(0, extend), x, y, paint);
					}
				}

			}
			/**
			 * We will be saving the new texture
			 */

			try {
				FileOutputStream outputStream = new FileOutputStream(texture);
				sign.compress(Bitmap.CompressFormat.PNG, 90, outputStream);
				return texture;

			} catch (Exception e) {

			}

			sign.recycle();
			sign = null;

		} catch (Exception e) {

			return null;
		}
		return null;

	}

	@Override
	public void onGravitySensorChanged(float[] arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onHeadingSensorChanged(float[] orientation) {
		// TODO Auto-generated method stub

	}

	/**
	 * Creates andSends a Toast message
	 * 
	 * @param text
	 */
	private void toastMessage(String text) {

		Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();

	}

}
