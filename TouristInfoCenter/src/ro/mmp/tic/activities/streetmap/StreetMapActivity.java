/**
 * @author Matei Mircea
 * 
 * This class is used to create the street view map. It uses the metaioSDK and it is a subclass of ARViewActivity
 */

package ro.mmp.tic.activities.streetmap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import ro.mmp.tic.R;
import ro.mmp.tic.metaio.ARViewActivity;
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
	/**
	 * We first declare the geometries
	 * 
	 * Botanical Garden : North : 46.762737 East : 23.588569 Matei Corvin
	 * statue: North:46.772437 East:23.58799 Orthodox Cathedral: North:46.77201
	 * East:23.59634 Bastionul Croitorilor: North:46.771027 East: 23.597059
	 * 
	 * 
	 */

	private IGeometry mBotanical;
	private IGeometry mStatue;
	private IGeometry mCathedral;
	private IGeometry mBastion;

	/**
	 * We need LLACoordinates for all 4 positions
	 */

	private LLACoordinate mCoordBotanical;
	private LLACoordinate mCoordStatue;
	private LLACoordinate mCoordCathedral;
	private LLACoordinate mCoordBastion;

	private IBillboardGroup billboardGroup;

	/**
	 * Next is the radar component
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

			mBotanical = metaioSDK.createGeometryFromImage(
					createSign("Botanical Garden"), true);
			mStatue = metaioSDK.createGeometryFromImage(
					createSign("Matei Corvin Statue"), true);
			mCathedral = metaioSDK.createGeometryFromImage(
					createSign("Orthodox Cathedral"), true);
			mBastion = metaioSDK.createGeometryFromImage(
					createSign("Bastionul Croitorilor"), true);

			mBotanical.setName("Botanical");
			mStatue.setName("Statue");
			mCathedral.setName("Cathedral");
			mBastion.setName("Bastion");

			billboardGroup.addBillboard(mBotanical);
			billboardGroup.addBillboard(mStatue);
			billboardGroup.addBillboard(mCathedral);
			billboardGroup.addBillboard(mBastion);

			/**
			 * build the location of our interest points
			 */
			updateGeometriesLocation(mSensors.getLocation());

			/**
			 * Create the radar
			 */

			radar = metaioSDK.createRadar();
			String file = AssetsManager.getAssetPath("streetmap/yellow.png");
			radar.setBackgroundTexture(AssetsManager
					.getAssetPath("streetmap/radar.png"));
			radar.setObjectsDefaultTexture(file);
			radar.setRelativeToScreen(IGeometry.ANCHOR_TL);

			/**
			 * add geometries to the radar
			 */

			radar.add(mBotanical);
			radar.add(mStatue);
			radar.add(mCathedral);
			radar.add(mBastion);

			mBotanical.setVisible(false);
			mStatue.setVisible(false);
			mCathedral.setVisible(false);
			mBastion.setVisible(false);

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
		mBotanical.setVisible(true);
		mStatue.setVisible(true);
		mCathedral.setVisible(true);
		mBastion.setVisible(true);

	}

	/*
	 * @Override public boolean onTouch(View v, MotionEvent event) { int x =
	 * (int) event.getX(); int y = (int) event.getY();
	 * 
	 * if (radar != null) { if (x < 117 && y < 142) {
	 * radar.setScale(CONTEXT_RESTRICTED);
	 * 
	 * } else { radar.setScale(CONTEXT_INCLUDE_CODE);
	 * 
	 * } }
	 * 
	 * switch (event.getAction()) { case MotionEvent.ACTION_DOWN:
	 * 
	 * break;
	 * 
	 * case MotionEvent.ACTION_MOVE:
	 * 
	 * break; case MotionEvent.ACTION_UP:
	 * 
	 * break; } return false; }
	 */

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

	/**
	 * This method has the responsability of updating the location on the radar
	 * It uses an offset to get a more precise location
	 * 
	 * @param location
	 */
	private void updateGeometriesLocation(LLACoordinate location) {

		Log.i(TAG, "Update geometry locations ");
		/*
		 * Botanical Garden : North : 46.762737 East : 23.588569 Matei Corvin
		 * statue: North:46.772437 East:23.58799 Orthodox Cathedral:
		 * North:46.77201 East:23.59634 Bastionul Croitorilor: North:46.771027
		 * East: 23.597059
		 */
		LLACoordinate currPos = mSensors.getLocation();

		mCoordBotanical = new LLACoordinate(46.762737, 23.588569,
				currPos.getAltitude(), currPos.getAltitude(),
				currPos.getAccuracy());

		mCoordStatue = new LLACoordinate(46.772437, 23.58799,
				currPos.getAltitude(), currPos.getAltitude(),
				currPos.getAccuracy());

		mCoordCathedral = new LLACoordinate(46.77201, 23.59634,
				currPos.getAltitude(), currPos.getAltitude(),
				currPos.getAccuracy());

		mCoordBastion = new LLACoordinate(46.771027, 23.597059,
				currPos.getAltitude(), currPos.getAltitude(),
				currPos.getAccuracy());

		if (mBotanical != null) {
			mBotanical.setTranslationLLA(mCoordBotanical);
			mBotanical.setLLALimitsEnabled(true);
		}

		if (mStatue != null) {
			mStatue.setTranslationLLA(mCoordStatue);
			mStatue.setLLALimitsEnabled(true);
		}

		if (mCathedral != null) {
			mCathedral.setTranslationLLA(mCoordCathedral);
			mCathedral.setLLALimitsEnabled(true);
		}

		if (mBastion != null) {
			mBastion.setTranslationLLA(mCoordBastion);
			mBastion.setLLALimitsEnabled(true);
		}

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
