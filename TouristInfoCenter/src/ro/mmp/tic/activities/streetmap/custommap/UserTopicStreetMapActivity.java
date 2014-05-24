package ro.mmp.tic.activities.streetmap.custommap;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import ro.mmp.tic.R;
import ro.mmp.tic.activities.ScheduleActivity;
import ro.mmp.tic.activities.streetmap.PresentationActivity;
import ro.mmp.tic.activities.streetmap.custommap.usertopics.ManageUserTopicsActivity;
import ro.mmp.tic.activities.streetmap.util.StreetMapUtil;
import ro.mmp.tic.adapter.model.CustomMapModel;
import ro.mmp.tic.domain.Schedule;
import ro.mmp.tic.metaio.ARViewActivity;
import ro.mmp.tic.service.sqlite.DataBaseConnection;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
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

public class UserTopicStreetMapActivity extends ARViewActivity implements
		SensorsComponentAndroid.Callback {

	private final String TAG = "CustomMapActivity";
	// database connection
	private DataBaseConnection dbc;
	// used to create the street map
	private ArrayList<CustomMapModel> customMapModel;
	// used to order the elements seen on the street map
	private IBillboardGroup billboardGroup;
	/**
	 * radar component
	 */
	private IRadar radar;
	static final int DATE_DIALOG_ID = 100;
	static final int TIME_DIALOG_ID = 999;

	// util
	private StreetMapUtil streetMapUtil;
	private String customMapFile;
	private String streetMapFile;
	private String cameraCalibrationFile;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(TAG, "onCreate");

		setupInterface();

	}

	private void setupInterface() {
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

		// build the custom map model
		customMapModel = new ArrayList<CustomMapModel>(0);

		for (String s : ManageUserTopicsActivity.getUserCustomLocation()) {

			CustomMapModel cm = dbc.getCustomMapModel(s);
			customMapModel.add(cm);
		}

		// create the StreetMapUtil object
		streetMapUtil = new StreetMapUtil();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.custom_map, menu);
		return true;
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
	protected int getGUILayout() {
		return R.layout.activity_user_custom_topic;
	}

	@Override
	public void onLocationSensorChanged(LLACoordinate location) {

		updateGeometriesLocation(mSensors.getLocation());

	}

	@Override
	protected void loadContents() {
		Log.d(TAG, "loadContents");
		loadGPSInformation();

	}

	// this determines what happens when the user clicks the schedule button
	public void onScheduleButtonClick(View view) {
		Intent intent = new Intent(this, ScheduleActivity.class);
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		this.onResume();

	}

	// This method loads the information on the map
	private void loadGPSInformation() {

		try {

			Log.d(TAG, "loadGPSinformation");
			// set up the tracking configuration
			@SuppressWarnings("unused")
			boolean result = metaioSDK.setTrackingConfiguration("GPS");

			// set up the bilboard in order for geometries not to overlap
			billboardGroup = metaioSDK.createBillboardGroup(720, 850);
			billboardGroup.setBillboardExpandFactors(1, 5, 30);
			metaioSDK.setRendererClippingPlaneLimits(50, 100000000);
			metaioSDK.setLLAObjectRenderingLimits(10, 10000); // to reduce
																// flickering
			Log.d(TAG, "set Billboard");
			// create geometry
			for (CustomMapModel cm : customMapModel) {
				Log.d(TAG, "setGeometry " + cm.getUserTopic().getName());

				cm.setGeometry(metaioSDK.createGeometryFromImage(streetMapUtil
						.createSign(getApplicationContext(), cm.getUserTopic()
								.getName()), true));
				cm.getGeometry().setName(cm.getUserTopic().getName());
				billboardGroup.addBillboard(cm.getGeometry());
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

			// st up the color for those lcoations that are in the schedule
			ArrayList<Schedule> schedules = dbc.getAllSchedule();
			for (CustomMapModel cm : customMapModel) {
				if (isInSchedule(cm, schedules)) {
					cm.getUserTopic().setColor("green.png");
				}
			}

			// ccreate the radar
			radar = metaioSDK.createRadar();

			// set radar background
			customMapFile = AssetsManager.getAssetPath("streetmap/radar.png");
			radar.setBackgroundTexture(customMapFile);
			radar.setRelativeToScreen(IGeometry.ANCHOR_TL);
			radar.setVisible(true);

			for (CustomMapModel cm : customMapModel) {
				radar.add(cm.getGeometry());

				String colorFile = AssetsManager.getAssetPath("streetmap/"
						+ cm.getUserTopic().getColor());
				radar.setObjectTexture(cm.getGeometry(), colorFile);
				cm.getGeometry().setVisible(true);
			}

			// setting camra calibration
			cameraCalibrationFile = AssetsManager
					.getAssetPath("streetmap/CameraCalibration.xml");
			metaioSDK.setCameraParameters(cameraCalibrationFile);

			Log.i(TAG, "Set up everything ");

		} catch (Exception e) {

		}

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

		for (CustomMapModel mm : customMapModel) {
			mm.setCoordinate(new LLACoordinate(mm.getUserTopic().getLat(), mm
					.getUserTopic().getLng(), currPos.getAltitude(), currPos
					.getAltitude(), currPos.getAccuracy()));

			if (mm.getGeometry() != null) {
				mm.getGeometry().setTranslationLLA(mm.getCoordinate());
				mm.getGeometry().setLLALimitsEnabled(true);
			}

		}

	}

	// detect if a specific lcoation is set up in schedule
	private boolean isInSchedule(CustomMapModel m, ArrayList<Schedule> schedules) {

		for (Schedule s : schedules) {
			if (s.getPlace().equals(m.getGeometry().getName())) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void onGravitySensorChanged(float[] arg0) {

	}

	@Override
	public void onHeadingSensorChanged(float[] orientation) {

	}

	@Override
	protected IMetaioSDKCallback getMetaioSDKCallbackHandler() {
		return null;
	}

	@Override
	protected void onGeometryTouched(IGeometry geometry) {
		Log.d("StreetMap", "Enter onGeometry Touched");
		// We create the view
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		View dialogView = layoutInflater.inflate(
				R.layout.schedule_prompt_layout, null); // set up the dialog
														// builder
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
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
										UserTopicStreetMapActivity.this,
										PresentationActivity.class);
								intent.putExtra("token", "userstreetmap");
								intent.putExtra("loggedUser", username);
								intent.putExtra(
										"name",
										customMapModel
												.get(streetMapUtil
														.getCurrentPosition())
												.getUserTopic().getName());
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
		streetMapUtil.setUpAlertDialogBoxCustomMap(customMapModel,
				geometry.getName(), getDistance());
		streetMapUtil.setCurrentDate();
		streetMapUtil.setCurrentTime();

		// create the alert dialog
		AlertDialog alert = alertDialogBuilder.create();
		// display the dialog
		Log.d("StreetMap", "Show dialog");
		alert.show();

	}

	private String getDistance() {
		LLACoordinate target = new LLACoordinate();
		target.setLatitude(mSensors.getLocation().getLatitude());
		target.setLongitude(mSensors.getLocation().getLongitude());
		double distance = customMapModel
				.get(streetMapUtil.getCurrentPosition()).getCoordinate()
				.distanceTo(target);
		distance = (double) distance / 1000;
		distance = distance * 1.6;
		String result = "";
		result = new DecimalFormat("##.##").format(distance) + " KM";
		return result;
	}

	/**
	 * Creates andSends a Toast message
	 * 
	 * @param text
	 */
	private void toastMessage(String text) {

		Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();

	}

	// calculates the previouse location and sets up the GUI
	public void goToPreviouse(View v) {

		streetMapUtil
				.goToPreviouseCustomMapModel(customMapModel, getDistance());
	}

	// calculates the next location and sets up the GUI
	public void goToNext(View v) {
		streetMapUtil.goToNextCustomMapModel(customMapModel, getDistance());
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
		schedule.setPlace(customMapModel
				.get(streetMapUtil.getCurrentPosition()).getUserTopic()
				.getName()
				+ "\n"
				+ customMapModel.get(streetMapUtil.getCurrentPosition())
						.getUserTopic().getDescription());

		if (lastSchedule.isEmpty()) {
			schedule.setAlarmnr(1);
		} else {
			schedule.setAlarmnr(lastSchedule.get(0).getAlarmnr() + 1);
		}

		streetMapUtil.setScheduledAllarm(getApplicationContext(), schedule);
		toastMessage("date: " + schedule.getDate() + " time: "
				+ schedule.getTime() + " place " + schedule.getPlace());

		dbc.saveSchedule(schedule);

		customMapModel.get(streetMapUtil.getCurrentPosition()).getUserTopic()
				.setColor("green.png");

		radar.remove(customMapModel.get(streetMapUtil.getCurrentPosition())
				.getGeometry());
		radar.add(customMapModel.get(streetMapUtil.getCurrentPosition())
				.getGeometry());
		streetMapFile = AssetsManager.getAssetPath("streetmap/green.png");
		radar.setObjectTexture(
				customMapModel.get(streetMapUtil.getCurrentPosition())
						.getGeometry(), streetMapFile);

	}

}
