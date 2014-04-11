/**
 * @author Matei Mircea
 * 
 * This class is used to create the street view map. It uses the metaioSDK and it is a subclass of ARViewActivity
 */

package ro.mmp.tic.activities.streetmap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import ro.mmp.tic.R;
import ro.mmp.tic.activities.ScheduleActivity;
import ro.mmp.tic.activities.streetmap.fragment.LocationFragment;
import ro.mmp.tic.activities.streetmap.util.GoogleImageUtil;
import ro.mmp.tic.activities.streetmap.util.StreetMapUtil;
import ro.mmp.tic.adapter.model.MapModel;
import ro.mmp.tic.domain.Schedule;
import ro.mmp.tic.metaio.ARViewActivity;
import ro.mmp.tic.service.sqlite.DataBaseConnection;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
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

	// database connection
	private DataBaseConnection dbc;

	// used to create the street map
	private ArrayList<MapModel> mapModel;

	// used to order the elements seen on the street map
	private IBillboardGroup billboardGroup;

	/**
	 * radar component
	 */

	private IRadar radar;

	static final int DATE_DIALOG_ID = 100;
	static final int TIME_DIALOG_ID = 999;

	// alert display
	private boolean displayAlertOnce = false;

	// util
	private GoogleImageUtil googleUtil;
	private StreetMapUtil streetMapUtil;
	private File googleImagefilePath;
	private String streetMapFile;
	// variables for downloading the image
	private Display display;
	private Point size;
	private int width;
	private int height;
	private double lat;
	private double lng;
	private static final int ZOOM = 14;

	// radar options
	private float x1, x2;
	private float y1, y2;
	private boolean radarType = true;
	private boolean hasChanged = false;

	/**
	 * We override the onCreate method to set up the tracking configuration
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	

		/**
		 * Create connection to database
		 */
		dbc = new DataBaseConnection(this);

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

			e.printStackTrace();
		}

		// map model
		mapModel = new ArrayList<MapModel>(0);
		mapModel = dbc.getMapModel(LocationFragment.getSelectedLocation());

		// create the StreetMapUtil object
		streetMapUtil = new StreetMapUtil();

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
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

	}

	@Override
	public void onSurfaceCreated() {
		// TODO Auto-generated method stub
		super.onSurfaceCreated();

	}

	@Override
	public void onLocationSensorChanged(LLACoordinate location) {
		/**
		 * we update the activity when location changes
		 */
		updateGeometriesLocation(mSensors.getLocation());

	}

	@Override
	public void onDrawFrame() {
		// TODO Auto-generated method stub
		super.onDrawFrame();
		/*
		 * Log.d("StreetMap", "Entered lat:" + lat + " lng:" + lng +
		 * " sensorLant:" + mSensors.getLocation().getLatitude() +
		 * " sensorLong:" + mSensors.getLocation().getLongitude());
		 */

		// download map based on lcoation lat and lng
		if (radar != null
				&& (lat != mSensors.getLocation().getLatitude() || lng != mSensors
						.getLocation().getLongitude())) {
			Log.d("StreetMap", "onDrawFrame");
			Log.d("StreetMapActivity", "Entering3 in onDrawFrame");
			this.deleteFile("mapImage");
			width = size.x;
			height = size.y;

			lat = mSensors.getLocation().getLatitude();
			lng = mSensors.getLocation().getLongitude();

			Log.d("StreetMapActivity", "Entering3 in onDrawFrame lat:" + lat
					+ " lng:" + lng);

			googleUtil.downloadImage(lat, lng, width, height, ZOOM);
			googleImagefilePath = this.getFileStreamPath("mapImage.png");
			if (googleImagefilePath.getAbsoluteFile() != null) {
				Log.d("StreetMapActivity", "set background in onDrawFrame");
				radar.setBackgroundTexture(googleImagefilePath.toString());
			} else {
				streetMapFile = AssetsManager
						.getAssetPath("streetmap/radar.png");
				radar.setBackgroundTexture(streetMapFile);

			}

		}
		// set up the radar or map based on user click choice
		if (radar != null && hasChanged) {
			if (radarType) {
				googleImagefilePath = this.getFileStreamPath("mapImage.png");
				if (googleImagefilePath.getAbsoluteFile() != null) {
					Log.d("StreetMapActivity", "set background in onDrawFrame");
					radar.setBackgroundTexture(googleImagefilePath.toString());
				}

			} else {
				streetMapFile = AssetsManager
						.getAssetPath("streetmap/radar.png");
				radar.setBackgroundTexture(streetMapFile);

				/**
				 * add geometries to the radar
				 */

				ArrayList<Schedule> schedules = dbc.getAllSchedule();

				for (MapModel m : mapModel) {

					if (isInSchedule(m, schedules)) {

						m.setColor("green");
						streetMapFile = AssetsManager
								.getAssetPath("streetmap/green.png");
						radar.setObjectTexture(m.getGeometry(), streetMapFile);

					} else {

						streetMapFile = AssetsManager.getAssetPath("streetmap/"
								+ m.getColor() + ".png");
						radar.setObjectTexture(m.getGeometry(), streetMapFile);

					}
					m.getGeometry().setVisible(true);
					m.getGeometry().setPickingEnabled(true);

				}
				googleUtil.setMapModel(mapModel);
			}

			hasChanged = false;
		}
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
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void loadGPSInformation() {

		try {

			// set up the bilboard in order for geometries not to overlap
			billboardGroup = metaioSDK.createBillboardGroup(720, 850);
			billboardGroup.setBillboardExpandFactors(1, 5, 30);
			metaioSDK.setRendererClippingPlaneLimits(50, 100000000);
			metaioSDK.setLLAObjectRenderingLimits(10, 10000); // to reduce
																// flickering

			// create geometry
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

			Log.d("StreetMapActivity",
					"Create googleUtil object in loadGPSInformation");
			googleUtil = new GoogleImageUtil(mapModel, this);

			display = getWindowManager().getDefaultDisplay();
			size = new Point();
			display.getSize(size);
			width = size.x;
			height = size.y;

			lat = mSensors.getLocation().getLatitude();
			lng = mSensors.getLocation().getLongitude();
			// st up the color for those lcoations that are in the schedule
			ArrayList<Schedule> schedules = dbc.getAllSchedule();
			for (MapModel m : mapModel) {
				if (isInSchedule(m, schedules)) {
					m.setColor("green");
				}
			}

			// download the first map from google maps
			googleUtil.downloadImage(lat, lng, width, height, ZOOM);
			Log.d("StreetMap", "Saved image");
			googleImagefilePath = this.getFileStreamPath("mapImage.png");

			// ccreate the radar
			radar = metaioSDK.createRadar();
			Log.d("StreetMapActivity",
					"set background in loadGPSInformation lat:" + lat + "lng:"
							+ lng);
			// set radar background
			radar.setBackgroundTexture(googleImagefilePath.toString());
			radar.setRelativeToScreen(IGeometry.ANCHOR_TL);
			radar.setVisible(true);

			Log.i(TAG, "Set up everything ");

		} catch (Exception e) {

		}

		// set up the tracking configuration
		@SuppressWarnings("unused")
		boolean result = metaioSDK.setTrackingConfiguration("GPS");

	}

	// detect if a specific lcoation is set up in schedule
	private boolean isInSchedule(MapModel m, ArrayList<Schedule> schedules) {

		for (Schedule s : schedules) {
			if (s.getPlace().contains(m.getGeometry().getName())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * This method has the responsability of updating the location on the radar
	 * It uses an offset to get a more precise location
	 * 
	 * @param location
	 */
	@SuppressLint("NewApi")
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

	// we display the alert diualog prompt for setting schedules when the user
	// presses on the geometry

	@Override
	protected void onGeometryTouched(IGeometry geometry) {

		if (displayAlertOnce) {

			Log.d("StreetMap", "Enter onGeometry Touched");
			// We create the view
			LayoutInflater layoutInflater = LayoutInflater.from(this);
			View dialogView = layoutInflater.inflate(
					R.layout.schedule_prompt_layout, null); // set up the dialog
															// builder
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					this);
			alertDialogBuilder.setView(dialogView);

			alertDialogBuilder.setInverseBackgroundForced(true);

			Log.d("StreetMap", "set the view");

			// setUpAlert(geometry.getName()); // say what the buttons do
			alertDialogBuilder
					.setCancelable(true)
					// set the presenation button
					.setPositiveButton("Presentation",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Intent i = getIntent();
									String username = i
											.getStringExtra("loggedUser");
									Intent intent = new Intent(
											StreetMapActivity.this,
											PresentationActivity.class);
									intent.putExtra("loggedUser", username);
									intent.putExtra(
											"name",
											mapModel.get(
													streetMapUtil
															.getCurrentPosition())
													.getTopic().getName());
									startActivityForResult(intent, 0);

								}
							})
					// set the cancel button
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();

								}
							});

			// set up the schedule alert prompt gui
			streetMapUtil.initiateDialogViewElements(dialogView);
			streetMapUtil.setUpAlertDialogBox(mapModel, geometry.getName(),
					getDistance());
			streetMapUtil.setCurrentDate();
			streetMapUtil.setCurrentTime();

			// create the alert dialog
			AlertDialog alert = alertDialogBuilder.create();
			// display the dialog
			Log.d("StreetMap", "Show dialog");
			alert.show();
			displayAlertOnce = false;
		}

	}

	private String getDistance() {
		LLACoordinate target = new LLACoordinate();
		target.setLatitude(mSensors.getLocation().getLatitude());
		target.setLongitude(mSensors.getLocation().getLongitude());
		double distance = mapModel.get(streetMapUtil.getCurrentPosition())
				.getCoordinate().distanceTo(target);
		distance = (double) distance / 1000;
		String result = "";
		result = new DecimalFormat("##.##").format(distance) + " KM";
		return result;
	}

	// calculates the previouse location and sets up the GUI
	public void goToPreviouse(View v) {

		streetMapUtil.goToPreviouse(mapModel, getDistance());
	}

	// calculates the next location and sets up the GUI
	public void goToNext(View v) {
		streetMapUtil.goToNext(mapModel, getDistance());
	}

	// this method is called when the user uses showDialog(int id)
	@Override
	protected Dialog onCreateDialog(int id) {

		switch (id) {
		case DATE_DIALOG_ID:

			// set date picker as current date

			return new DatePickerDialog(this, datePickerListener,
					streetMapUtil.getYear(), streetMapUtil.getMonth(),
					streetMapUtil.getDay());

		case TIME_DIALOG_ID:
			return new TimePickerDialog(this, timePickerListener,
					streetMapUtil.getHour(), streetMapUtil.getMinute(), true);

		}

		return null;

	}

	/**
	 * SET DATE
	 * 
	 * @param view
	 */
	@SuppressWarnings("deprecation")
	public void setDate(View view) {

		showDialog(DATE_DIALOG_ID);

	}

	// the date picker listener
	private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

		// when dialog box is closed, below method will be called

		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {

			streetMapUtil.setSelectedDate(selectedYear, selectedMonth,
					selectedDay);

		}

	};

	// we set the current date in the text view
	public void setCurrentDate() {

		streetMapUtil.setCurrentDate();

	}

	/**
	 * SET TIME
	 * 
	 * @param view
	 */
	@SuppressWarnings("deprecation")
	public void setTime(View view) {

		showDialog(TIME_DIALOG_ID);

	}

	// time picker listener
	private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {

		public void onTimeSet(TimePicker view, int selectedHour,
				int selectedMinute) {

			streetMapUtil.setSelectedTime(selectedHour, selectedMinute);
		}

	};

	public void setCurrentTime() {
		streetMapUtil.setCurrentTime();

	}

	// save schedule
	public void saveSchedule(View view) {

		

		Schedule schedule = new Schedule();
		schedule.setTime(streetMapUtil.getTimeText().getText().toString());
		schedule.setDate(streetMapUtil.getDateText().getText().toString());
		schedule.setPlace(mapModel.get(streetMapUtil.getCurrentPosition())
				.getTopic().getName()
				+ "\n"
				+ mapModel.get(streetMapUtil.getCurrentPosition()).getTopic()
						.getAddress());

		streetMapUtil.setScheduledAllarm(getApplicationContext(),schedule);
		toastMessage("date: " + schedule.getDate() + " time: "
				+ schedule.getTime() + " place " + schedule.getPlace());

		dbc.saveSchedule(schedule);
		mapModel.get(streetMapUtil.getCurrentPosition()).setColor("green");

		if (radarType) {
			radar.remove(mapModel.get(streetMapUtil.getCurrentPosition())
					.getGeometry());
		} else {
			radar.remove(mapModel.get(streetMapUtil.getCurrentPosition())
					.getGeometry());
			radar.add(mapModel.get(streetMapUtil.getCurrentPosition())
					.getGeometry());
			streetMapFile = AssetsManager.getAssetPath("streetmap/green.png");
			radar.setObjectTexture(
					mapModel.get(streetMapUtil.getCurrentPosition())
							.getGeometry(), streetMapFile);

		}

		googleUtil.setMapModel(mapModel);

	}

	// schedule button
	public void onScheduleButtonClick(View view) {

		Intent intent = new Intent(this, ScheduleActivity.class);
		startActivityForResult(intent, 0);

	}

	// legend button
	public void onLegendButtonClick(View view) {

		streetMapUtil.setUpLegendAlertDialog(this);

	}

	@Override
	public void onGravitySensorChanged(float[] translation) {

	}

	@Override
	public void onHeadingSensorChanged(float[] orientation) {

	}

	/**
	 * Creates andSends a Toast message
	 * 
	 * @param text
	 */

	private void toastMessage(String text) {

		Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT)
				.show();

	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	@Override
	public boolean onTouch(View v, MotionEvent event) {

		super.onTouch(v, event);

		switch (event.getAction()) { // when user first touches the screen we
										// get x and y coordinate
		case MotionEvent.ACTION_DOWN:

			x1 = event.getX();
			y1 = event.getY();

			break;

		case MotionEvent.ACTION_UP:

			x2 = event.getX();
			y2 = event.getY();
			IGeometry geometry = metaioSDK.getGeometryFromScreenCoordinates(
					(int) x1, (int) y1, false);
			// if UP to Down sweep event on screen
			if (y2 - y1 > 300) {

				radar.setScale(CONTEXT_RESTRICTED);
				radar.setTranslation(mapModel.get(0).getGeometry()
						.getTranslation());

			}

			// if Down to UP sweep event on screen
			else if (y1 - y2 > 300) {

				radar.setScale(RESULT_FIRST_USER);
				radar.setTranslation(mapModel.get(0).getGeometry()
						.getTranslation());
			} else if (x2 < 155 && y2 < 152) {

				if (radarType) {

					for (MapModel m : mapModel) {
						radar.add(m.getGeometry());
					}

					radarType = false;
				} else {

					for (MapModel m : mapModel) {
						radar.remove(m.getGeometry());
					}

					radarType = true;
				}
				hasChanged = true;

			}

			else if (geometry != null) {
				try {
					Log.d("StreetMap", "On touch we detect geometry");

					Log.d("StreetMap",
							"Geometry is not null we call on geometery touched");
					displayAlertOnce = true;
					onGeometryTouched(geometry);

				} catch (Exception e) {

				}
			}

			break;

		}

		return true;
	}

	/**
	 * This method creates the sign the suer sees on the device screen while
	 * using this activity
	 * 
	 * @param title
	 * @return
	 */
	public String createSign(String title) {

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

}
