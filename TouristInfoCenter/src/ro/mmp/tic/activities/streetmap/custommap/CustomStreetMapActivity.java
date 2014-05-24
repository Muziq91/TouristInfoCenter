/**
 * @author Matei Mircea
 * 
 * This activity has the role of helping the user create custom maps for himself
 */

package ro.mmp.tic.activities.streetmap.custommap;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import ro.mmp.tic.R;
import ro.mmp.tic.activities.ScheduleActivity;
import ro.mmp.tic.activities.streetmap.util.ImageUtil;
import ro.mmp.tic.activities.streetmap.util.StreetMapUtil;
import ro.mmp.tic.adapter.model.CustomMapModel;
import ro.mmp.tic.domain.Schedule;
import ro.mmp.tic.domain.UserTopic;
import ro.mmp.tic.metaio.ARViewActivity;
import ro.mmp.tic.service.sqlite.DataBaseConnection;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.metaio.sdk.SensorsComponentAndroid;
import com.metaio.sdk.jni.IBillboardGroup;
import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.IMetaioSDKCallback;
import com.metaio.sdk.jni.IRadar;
import com.metaio.sdk.jni.LLACoordinate;
import com.metaio.tools.io.AssetsManager;

public class CustomStreetMapActivity extends ARViewActivity implements
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
	// alert dialog box
	AlertDialog addPhotoAlert;
	// get photo
	private ImageView imageViewReturnPicture;
	private EditText nameText;
	private EditText descriptionText;
	private EditText latText;
	private EditText lngText;
	private Spinner customColorSpinner;
	private String customColorString;
	private boolean canAdd = false;
	private Bitmap cameraBmp;
	private ImageUtil imageUtil;

	private static String nameField;
	private static String descriptionField;
	private final static int cameraData = 200;

	private String username;
	private boolean wasAdded = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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

		// create Imageutil object for the locations already inserted
		imageUtil = new ImageUtil(getApplicationContext());

		// build the custom map model
		customMapModel = new ArrayList<CustomMapModel>(0);
		Intent intent = getIntent();
		username = intent.getStringExtra("loggedUser");
		customMapModel = dbc.getUserCustomMapModel(username);

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

	@Override
	public void onDrawFrame() {

		super.onDrawFrame();

		// The draw is used to display the new locations added
		if (wasAdded) {
			customMapModel = dbc.getUserCustomMapModel(username);
			loadGPSInformation();
			wasAdded = false;
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
		return R.layout.activity_custom_map;
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

	// when the schedule button is clicked open the schedule activity
	public void onScheduleButtonClick(View view) {
		Intent intent = new Intent(this, ScheduleActivity.class);
		startActivityForResult(intent, 0);
	}

	// when the add button is clicked open the add dialog box
	public void onAddButtonClick(View view) {

		showAddAlertDialogBox();

	}

	// when the image button is clicked send the suer to the camera in order to
	// take a picture of what he wants to add
	public void onImageButtonClick(View view) {
		addPhotoAlert.cancel();
		this.onStop();

		nameField = nameText.getText().toString();
		descriptionField = descriptionText.getText().toString();

		Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(i, cameraData);
	}

	// when the activity comes back
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		this.onResume();

		// if it is from the camera we will add the new image to the Interface
		// so the user can see his iamge
		if (resultCode == RESULT_OK && requestCode != 0) {

			showAddAlertDialogBox();
			// cand inchidem camera va lua extras deci poza
			/**
			 * this is how we take the picture that the phone made
			 */
			// extrsa e poza
			Bundle bundle = data.getExtras();
			cameraBmp = (Bitmap) bundle.get("data");
			imageViewReturnPicture.setImageBitmap(cameraBmp);
		}

	}

	// Opens the alert dialog box when the add button is clicked
	private void showAddAlertDialogBox() {
		// We create the view
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		View dialogView = layoutInflater.inflate(
				R.layout.custom_map_prompt_layout, null); // set up the dialog
															// builder
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setView(dialogView);
		alertDialogBuilder.setIcon(R.drawable.ic_launcher);
		alertDialogBuilder.setInverseBackgroundForced(true);
		alertDialogBuilder.setTitle("Add custom location");

		// setUpAlert(geometry.getName()); // say what the buttons do
		alertDialogBuilder.setCancelable(true)

		// set the cancel button
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();

							}
						});

		// create the alert dialog
		addPhotoAlert = alertDialogBuilder.create();

		// display the dialog
		imageViewReturnPicture = (ImageView) dialogView
				.findViewById(R.id.imageViewReturnPicture);
		nameText = (EditText) dialogView.findViewById(R.id.nameText);

		if (nameField != null) {
			nameText.setText(nameField);
		}

		descriptionText = (EditText) dialogView
				.findViewById(R.id.descriptionText);

		if (descriptionField != null) {
			descriptionText.setText(descriptionField);
		}

		latText = (EditText) dialogView.findViewById(R.id.latText);
		lngText = (EditText) dialogView.findViewById(R.id.lngText);

		latText.setText(String.valueOf(mSensors.getLocation().getLatitude()));
		lngText.setText(String.valueOf(mSensors.getLocation().getLongitude()));

		customColorSpinner = (Spinner) dialogView
				.findViewById(R.id.customColorSpinner);

		customColorSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> adapterView,
							View view, int position, long id) {
						customColorString = customColorSpinner
								.getItemAtPosition(position).toString();

					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						customColorString = null;

					}
				});

		addPhotoAlert.show();

	}

	// when the add button from inside the dialog box is clicked we verify the
	// data.
	public void onAddLocationButtonClick(View view) {

		if (nameText.getText().toString().equals("")) {
			toastMessage("The name cannot be empty");
		} else if (descriptionText.getText().toString().equals("")) {
			toastMessage("Description cannot be empty");
		} else if (latText.getText().toString().equals("")) {

			toastMessage("Lattitude must not be empty");
		} else if (lngText.getText().toString().equals("")) {
			toastMessage("Longitude must not be empty");
		} else if (cameraBmp == null) {
			toastMessage("You must take a picture");
		} else if (customColorString == null) {
			toastMessage("A color must be selected");

		} else {
			canAdd = true;
		}

		try {
			Double.parseDouble(latText.getText().toString());
		} catch (Exception e) {
			toastMessage("Lattitude must be a number");
			canAdd = false;
		}

		try {
			Double.parseDouble(lngText.getText().toString());
		} catch (Exception e) {
			toastMessage("Longitude must be a number");
			canAdd = false;
		}

		// if the data is correctly inserted we can add the new location to the
		// database
		if (canAdd) {

			nameField = null;
			descriptionField = null;
			String imageName = nameText.getText().toString().toLowerCase()
					+ "image";

			imageUtil.saveImageToInternalStorage(cameraBmp, imageName);

			UserTopic ut = new UserTopic();
			ut.setName(nameText.getText().toString());
			ut.setDescription(descriptionText.getText().toString());
			ut.setImage(imageName + ".png");
			ut.setColor(customColorString.toLowerCase() + ".png");
			ut.setLat(Double.parseDouble(latText.getText().toString()));
			ut.setLng(Double.parseDouble(lngText.getText().toString()));

			Intent intent = getIntent();
			String username = intent.getStringExtra("loggedUser");
			dbc.insertUserTopic(ut, username);
			wasAdded = true;

			toastMessage("Location added");
		}

	}

	// When the Mnage button is clicked we send the user to the Manage Activity
	public void onManageButtonClick(View view) {
		Intent intent = getIntent();
		Intent nextActivity = new Intent(this, ManageCustomMapActivity.class);
		nextActivity.putExtra("loggedUser",
				(String) intent.getStringExtra("loggedUser"));

		startActivityForResult(nextActivity, 1);

	}

	// this method is used to load the information and create the radar
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
			if (s.getPlace().contains(m.getGeometry().getName())) {
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

		// We create the view
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		View dialogView = layoutInflater.inflate(
				R.layout.schedule_prompt_layout, null); // set up the dialog
														// builder
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setView(dialogView);

		alertDialogBuilder.setInverseBackgroundForced(true);

		// setUpAlert(geometry.getName()); // say what the buttons do
		alertDialogBuilder.setCancelable(true)
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

	// gets the distance between two points
	// one point is the user's current location and the other point is the
	// selected location.
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
