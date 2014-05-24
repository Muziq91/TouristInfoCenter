/**
 * @author Matei Mircea
 * 
 * This class is used to create the street view map. It uses the metaioSDK and it is a subclass of ARViewActivity
 */

package ro.mmp.tic.activities.streetmap;

import java.io.File;
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
import android.graphics.Point;
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

	// These id's are used to know which picker to display
	static final int DATE_DIALOG_ID = 100;
	static final int TIME_DIALOG_ID = 200;

	// alert display
	private boolean displayAlertOnce = false;

	// util
	private GoogleImageUtil googleUtil;
	private StreetMapUtil streetMapUtil;
	private File googleImagefilePath;
	private String streetMapFile;
	private String cameraCalibrationFile;

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
	// this decides which radar is displayed, true is the gogole image as radar,
	// false is the default radar
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
		super.onDestroy();

	}

	@Override
	public void onSurfaceCreated() {
		super.onSurfaceCreated();

	}

	@Override
	public void onLocationSensorChanged(LLACoordinate location) {
		/**
		 * we update the activity when location changes
		 */
		updateGeometriesLocation(mSensors.getLocation());

	}

	/**
	 * This method is overriden in order to update the interface based on new
	 * information
	 */
	@Override
	public void onDrawFrame() {
		super.onDrawFrame();

		// download map based on location lat and lng
		// detect if the lattitude or longitude has changed and if the radar has
		// been created
		if (radar != null
				&& (lat != mSensors.getLocation().getLatitude() || lng != mSensors
						.getLocation().getLongitude())) {
			// delete the last image downloaded
			this.deleteFile("mapImage");
			// get width and height of the screen
			width = size.x;
			height = size.y;

			// get new lattitude and longitude
			lat = mSensors.getLocation().getLatitude();
			lng = mSensors.getLocation().getLongitude();

			// download the new iamge with the new properties
			googleUtil.downloadImage(lat, lng, width, height, ZOOM);
			googleImagefilePath = this.getFileStreamPath("mapImage.png");

			// if the image was successfully donwloaded
			if (googleImagefilePath.getAbsoluteFile() != null) {
				radar.setBackgroundTexture(googleImagefilePath.toString());
			}
			// if the image could not be downloaded, display the default radar
			else {

				streetMapFile = AssetsManager
						.getAssetPath("streetmap/radar.png");
				radar.setBackgroundTexture(streetMapFile);

			}

		}
		// set up the radar or map based on user click choice
		if (radar != null && hasChanged) {

			// this determines which radar to select based on user interactions
			if (radarType) {
				// this is the google image displayed as the radar
				googleImagefilePath = this.getFileStreamPath("mapImage.png");
				if (googleImagefilePath.getAbsoluteFile() != null) {
					Log.d("StreetMapActivity", "set background in onDrawFrame");
					radar.setBackgroundTexture(googleImagefilePath.toString());
				}

			} else {
				// this is the default radar displayed
				streetMapFile = AssetsManager
						.getAssetPath("streetmap/radar.png");
				radar.setBackgroundTexture(streetMapFile);

				/**
				 * add geometries to the radar
				 */

				ArrayList<Schedule> schedules = dbc.getAllSchedule();

				// we color green all locations that are currently in the
				// schedule
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
			// influences how much the bilboard will expand relative to the
			// center of the camera
			billboardGroup.setBillboardExpandFactors(1, 5, 30);
			// sets the near and far clipping planes of the renderer
			metaioSDK.setRendererClippingPlaneLimits(50, 100000000);
			// reduces flickering
			metaioSDK.setLLAObjectRenderingLimits(10, 10000);

			// create geometry
			for (MapModel m : mapModel) {

				m.setGeometry(metaioSDK.createGeometryFromImage(
						streetMapUtil.createSign(this, m.getTopic().getName()),
						true));
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

			googleUtil = new GoogleImageUtil(mapModel, this);

			display = getWindowManager().getDefaultDisplay();
			size = new Point();
			display.getSize(size);
			width = size.x;
			height = size.y;

			// set the latitude and longitude of the current suer location
			lat = mSensors.getLocation().getLatitude();
			lng = mSensors.getLocation().getLongitude();
			// set up the color for those locations that are in the schedule
			ArrayList<Schedule> schedules = dbc.getAllSchedule();
			for (MapModel m : mapModel) {
				if (isInSchedule(m, schedules)) {
					m.setColor("green");
				}
			}

			// download the first map from google maps
			googleUtil.downloadImage(lat, lng, width, height, ZOOM);
			googleImagefilePath = this.getFileStreamPath("mapImage.png");

			// ccreate the radar
			radar = metaioSDK.createRadar();

			// set radar background
			radar.setBackgroundTexture(googleImagefilePath.toString());
			radar.setRelativeToScreen(IGeometry.ANCHOR_TL);
			radar.setVisible(true);
			
			// setting camra calibration
			cameraCalibrationFile = AssetsManager
					.getAssetPath("streetmap/CameraCalibration.xml");
			metaioSDK.setCameraParameters(cameraCalibrationFile);

		} catch (Exception e) {

		}

		// set up the tracking configuration
		@SuppressWarnings("unused")
		boolean result = metaioSDK.setTrackingConfiguration("GPS");

	}

	// detect if a specific location is set up in schedule
	private boolean isInSchedule(MapModel m, ArrayList<Schedule> schedules) {

		for (Schedule s : schedules) {
			if (s.getPlace().contains(m.getGeometry().getName())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * This method has the responsibility of updating the location on the radar
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

			// We create the view
			LayoutInflater layoutInflater = LayoutInflater.from(this);
			View dialogView = layoutInflater.inflate(
					R.layout.schedule_prompt_layout, null); // set up the dialog
															// builder
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					this);
			alertDialogBuilder.setView(dialogView);

			alertDialogBuilder.setInverseBackgroundForced(true);

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
									intent.putExtra("token", "streetmap");
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
		distance = distance * 1.6;
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
		ArrayList<Schedule> lastSchedule = dbc.getLastSchedule();

		schedule.setTime(streetMapUtil.getTimeText().getText().toString());
		schedule.setDate(streetMapUtil.getDateText().getText().toString());
		schedule.setPlace(mapModel.get(streetMapUtil.getCurrentPosition())
				.getTopic().getName()
				+ "\n"
				+ mapModel.get(streetMapUtil.getCurrentPosition()).getTopic()
						.getAddress());
		if (lastSchedule.isEmpty()) {
			schedule.setAlarmnr(1);
		} else {
			schedule.setAlarmnr(lastSchedule.get(0).getAlarmnr() + 1);
		}

		streetMapUtil.setScheduledAllarm(getApplicationContext(), schedule);
		toastMessage("date: " + schedule.getDate() + " time: "
				+ schedule.getTime() + " place " + schedule.getPlace()
				+ " alarmnr:" + schedule.getAlarmnr());

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
					displayAlertOnce = true;
					onGeometryTouched(geometry);

				} catch (Exception e) {

				}
			}

			break;

		}

		return true;
	}

}
