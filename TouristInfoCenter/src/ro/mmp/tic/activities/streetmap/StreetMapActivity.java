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
import java.util.Calendar;

import ro.mmp.tic.R;
import ro.mmp.tic.activities.ScheduleActivity;
import ro.mmp.tic.activities.streetmap.fragment.LocationFragment;
import ro.mmp.tic.adapter.model.MapModel;
import ro.mmp.tic.domain.Schedule;
import ro.mmp.tic.metaio.ARViewActivity;
import ro.mmp.tic.service.sqlite.DataBaseConnection;
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
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
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
	// used to create the street map
	private ArrayList<MapModel> mapModel;
	// used to order the elements seen on the street map
	private IBillboardGroup billboardGroup;

	/**
	 * radar component
	 */

	private IRadar radar;

	// Take care of schedule part
	private int currentPosition;

	// date elements
	private int year;
	private int month;
	private int day;

	// time elements
	private int hour;
	private int minute;

	static final int DATE_DIALOG_ID = 100;
	static final int TIME_DIALOG_ID = 999;

	// alert view elements

	private Button prevButton;
	private Button nextButton;

	private TextView prevText;
	private TextView nextText;
	private TextView dateText;
	private TextView timeText;
	private TextView currentText;

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

		// We create the view
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		View dialogView = layoutInflater.inflate(
				R.layout.schedule_prompt_layout, null);
		// set up the dialog builder
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setView(dialogView);

		// setUpAlert(geometry.getName());
		// say what the buttons do
		alertDialogBuilder
				.setCancelable(false)
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
								intent.putExtra("name",
										mapModel.get(currentPosition)
												.getTopic().getName());
								startActivity(intent);

							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();

							}
						});

		initiateDialogViewElements(dialogView);
		setUpAlert(geometry.getName());
		setCurrentDate();
		setCurrentTime();

		// create the alert dialog
		AlertDialog alert = alertDialogBuilder.create();
		// display the dialog
		alert.show();

		/*
		 * if (geometry.getName().equals("Botanical")) {
		 * toastMessage("You accessed the Botanical Garden Page"); Intent intent
		 * = new Intent(StreetMapActivity.this, PresentationActivity.class);
		 * intent.putExtra("loggedUser", username); intent.putExtra("name",
		 * "Botanical Garden"); startActivity(intent);
		 * 
		 * } else if (geometry.getName().equals("Statue")) {
		 * toastMessage("You accessed the Matei Corvin Statue page"); Intent
		 * intent = new Intent(StreetMapActivity.this,
		 * PresentationActivity.class); intent.putExtra("loggedUser", username);
		 * intent.putExtra("name", "Matei Corvin Statue");
		 * startActivity(intent); } else if
		 * (geometry.getName().equals("Cathedral")) { toastMessage(
		 * "You accessed the Orthodox Cathedral Page \n Accros from it you can see the National Theatre"
		 * ); Intent intent = new Intent(StreetMapActivity.this,
		 * PresentationActivity.class); intent.putExtra("loggedUser", username);
		 * intent.putExtra("name", "Orthodox Cathedral"); startActivity(intent);
		 * } else if (geometry.getName().equals("Bastion")) {
		 * toastMessage("You accessed the Bastionul Croitorilor page"); Intent
		 * intent = new Intent(StreetMapActivity.this,
		 * PresentationActivity.class); intent.putExtra("loggedUser", username);
		 * intent.putExtra("name", "Bastionul Croitorilor");
		 * startActivity(intent); }
		 */

	}

	public void setUpAlert(String locatioNname) {

		for (int j = 0; j < mapModel.size(); j++) {
			if (mapModel.get(j).getTopic().getName().equals(locatioNname)) {
				currentPosition = j;
			}
		}

		currentText
				.setText(mapModel.get(currentPosition).getTopic().getName()
						+ " \n"
						+ mapModel.get(currentPosition).getTopic().getAddress());

		if (mapModel.size() == 1) {
			prevButton.setVisibility(View.GONE);
			prevText.setVisibility(View.GONE);

			nextButton.setVisibility(View.GONE);
			nextText.setVisibility(View.GONE);
		} else if (mapModel.size() >= 2) {

			if (currentPosition == mapModel.size() - 1) {
				nextButton.setVisibility(View.GONE);
				nextText.setVisibility(View.GONE);

				prevButton.setVisibility(View.VISIBLE);
				prevText.setVisibility(View.VISIBLE);

				prevText.setText(mapModel.get(currentPosition - 1).getTopic()
						.getName());
			}

			else if (currentPosition == 0) {
				prevButton.setVisibility(View.GONE);
				prevText.setVisibility(View.GONE);

				nextButton.setVisibility(View.VISIBLE);
				nextText.setVisibility(View.VISIBLE);

				nextText.setText(mapModel.get(currentPosition + 1).getTopic()
						.getName());
			}

			else {
				nextButton.setVisibility(View.VISIBLE);
				nextText.setVisibility(View.VISIBLE);
				nextText.setText(mapModel.get(currentPosition + 1).getTopic()
						.getName());
				prevButton.setVisibility(View.VISIBLE);
				prevText.setVisibility(View.VISIBLE);
				prevText.setText(mapModel.get(currentPosition - 1).getTopic()
						.getName());
			}
		}

	}

	private void initiateDialogViewElements(View dialogView) {
		prevButton = (Button) dialogView.findViewById(R.id.prevButton);
		nextButton = (Button) dialogView.findViewById(R.id.nextButton);

		prevText = (TextView) dialogView.findViewById(R.id.prevText);
		nextText = (TextView) dialogView.findViewById(R.id.nextText);
		dateText = (TextView) dialogView.findViewById(R.id.dateText);
		timeText = (TextView) dialogView.findViewById(R.id.timeText);
		currentText = (TextView) dialogView.findViewById(R.id.currentText);

	}

	// calculates the previouse location and sets up the GUI
	public void goToPreviouse(View v) {

		int prevPosition = currentPosition - 1;
		if (prevPosition == 0) {

			prevButton.setVisibility(View.GONE);
			prevText.setVisibility(View.GONE);

			nextButton.setVisibility(View.VISIBLE);
			nextText.setVisibility(View.VISIBLE);

			nextText.setText(mapModel.get(prevPosition + 1).getTopic()
					.getName());
			currentPosition = prevPosition;

		} else {

			nextButton.setVisibility(View.VISIBLE);
			nextText.setVisibility(View.VISIBLE);
			nextText.setText(mapModel.get(prevPosition + 1).getTopic()
					.getName());
			prevButton.setVisibility(View.VISIBLE);
			prevText.setVisibility(View.VISIBLE);
			prevText.setText(mapModel.get(prevPosition - 1).getTopic()
					.getName());
			currentPosition = prevPosition;

		}

		currentText.setText(mapModel.get(prevPosition).getTopic().getName()
				+ "\n" + mapModel.get(prevPosition).getTopic().getAddress());

	}

	// calculates the next location and sets up the GUI
	public void goToNext(View v) {
		int nextPosition = currentPosition + 1;
		if (nextPosition == mapModel.size() - 1) {

			prevButton.setVisibility(View.VISIBLE);
			prevText.setVisibility(View.VISIBLE);

			nextButton.setVisibility(View.GONE);
			nextText.setVisibility(View.GONE);

			prevText.setText(mapModel.get(nextPosition - 1).getTopic()
					.getName());
			currentPosition = nextPosition;

		} else {

			nextButton.setVisibility(View.VISIBLE);
			nextText.setVisibility(View.VISIBLE);
			nextText.setText(mapModel.get(nextPosition + 1).getTopic()
					.getName());
			prevButton.setVisibility(View.VISIBLE);
			prevText.setVisibility(View.VISIBLE);
			prevText.setText(mapModel.get(nextPosition - 1).getTopic()
					.getName());
			currentPosition = nextPosition;

		}

		currentText.setText(mapModel.get(nextPosition).getTopic().getName()
				+ "\n" + mapModel.get(nextPosition).getTopic().getAddress());
	}

	// this method is called when we user showDialog(int id)
	@Override
	protected Dialog onCreateDialog(int id) {

		switch (id) {
		case DATE_DIALOG_ID:

			// set date picker as current date

			return new DatePickerDialog(this, datePickerListener, year, month,
					day);

		case TIME_DIALOG_ID:
			return new TimePickerDialog(this, timePickerListener, hour, minute,
					true);

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

	// we set the current date in the text view
	public void setCurrentDate() {

		final Calendar calendar = Calendar.getInstance();

		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);

		dateText.setText(calendar.get(Calendar.DAY_OF_MONTH) + "/"
				+ calendar.get(Calendar.MONTH) + "/"
				+ calendar.get(Calendar.YEAR));

	}

	// the date picker listener
	private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

		// when dialog box is closed, below method will be called

		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {

			year = selectedYear;
			month = selectedMonth;
			day = selectedDay;

			// set selected date into Text View
			dateText.setText((month + 1) + "/" + day + "/" + year);

		}

	};

	/**
	 * SET TIME
	 * 
	 * @param view
	 */
	@SuppressWarnings("deprecation")
	public void setTime(View view) {

		showDialog(TIME_DIALOG_ID);

	}

	public void setCurrentTime() {

		final Calendar c = Calendar.getInstance();

		hour = c.get(Calendar.HOUR_OF_DAY);
		minute = c.get(Calendar.MINUTE);

		// set current time into textview

		if (minute < 10) {

			timeText.setText(hour + ":0" + minute);
		} else {
			timeText.setText(hour + ":" + minute);
		}

	}

	// time picker listener
	private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {

		public void onTimeSet(TimePicker view, int selectedHour,
				int selectedMinute) {

			hour = selectedHour;
			minute = selectedMinute;

			// set current time into textview

			timeText.setText(hour + " : " + minute);

		}

	};

	// save schedule
	public void saveSchedule(View view) {

		Schedule schedule = new Schedule();
		schedule.setTime(timeText.getText().toString());
		schedule.setDate(dateText.getText().toString());
		schedule.setPlace(mapModel.get(currentPosition).getTopic().getName()
				+ "\n" + mapModel.get(currentPosition).getTopic().getAddress());

		DataBaseConnection dbc = new DataBaseConnection(this);
		dbc.saveSchedule(schedule);
	}

	// schedule button
	public void onScheduleButtonClick(View view) {

		Intent intent = new Intent(this, ScheduleActivity.class);
		startActivityForResult(intent, 0);

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
	@SuppressWarnings("unused")
	private void toastMessage(String text) {

		Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();

	}

}
