/**
 * @author Matei Mircea
 * 
 * This class is used to help with streetmap functionality
 */

package ro.mmp.tic.activities.streetmap.util;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import ro.mmp.tic.R;
import ro.mmp.tic.adapter.model.CustomMapModel;
import ro.mmp.tic.adapter.model.MapModel;
import ro.mmp.tic.domain.Schedule;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.metaio.tools.io.AssetsManager;

public class StreetMapUtil {

	// date elements
	private int year;
	private int month;
	private int day;
	// time elements
	private int hour;
	private int minute;
	// Take care of schedule part
	private int currentPosition;
	// gui elements
	private Button prevButton;
	private Button nextButton;
	private TextView prevText;
	private TextView nextText;
	private TextView currentText;

	private TextView dateText;
	private TextView timeText;
	// legend alert dialog
	private TextView legendText;

	public void setUpAlertDialogBox(ArrayList<MapModel> mapModel,
			String locatioNname, String distance) {
		// get the position in the array list of the geoemrty the user touched
		for (int j = 0; j < mapModel.size(); j++) {
			if (mapModel.get(j).getTopic().getName().equals(locatioNname)) {
				currentPosition = j;
			}
		}

		// set up the current text

		currentText.setText(mapModel.get(currentPosition).getTopic().getName()
				+ " \n" + mapModel.get(currentPosition).getTopic().getAddress()
				+ "\n" + "Distance:" + distance);

		// set up the next and previouse buttons
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

	public void setUpAlertDialogBoxCustomMap(
			ArrayList<CustomMapModel> customMapModel, String locatioNname,
			String distance) {
		// get the position in the array list of the geoemrty the user touched
		for (int j = 0; j < customMapModel.size(); j++) {
			if (customMapModel.get(j).getUserTopic().getName()
					.equals(locatioNname)) {
				currentPosition = j;
			}
		}

		// set up the current text

		currentText.setText(customMapModel.get(currentPosition).getUserTopic()
				.getName()
				+ " \n" + "Distance:" + distance);

		// set up the next and previouse buttons
		if (customMapModel.size() == 1) {
			prevButton.setVisibility(View.GONE);
			prevText.setVisibility(View.GONE);

			nextButton.setVisibility(View.GONE);
			nextText.setVisibility(View.GONE);
		} else if (customMapModel.size() >= 2) {

			if (currentPosition == customMapModel.size() - 1) {
				nextButton.setVisibility(View.GONE);
				nextText.setVisibility(View.GONE);

				prevButton.setVisibility(View.VISIBLE);
				prevText.setVisibility(View.VISIBLE);

				prevText.setText(customMapModel.get(currentPosition - 1)
						.getUserTopic().getName());
			}

			else if (currentPosition == 0) {
				prevButton.setVisibility(View.GONE);
				prevText.setVisibility(View.GONE);

				nextButton.setVisibility(View.VISIBLE);
				nextText.setVisibility(View.VISIBLE);

				nextText.setText(customMapModel.get(currentPosition + 1)
						.getUserTopic().getName());
			}

			else {
				nextButton.setVisibility(View.VISIBLE);
				nextText.setVisibility(View.VISIBLE);
				nextText.setText(customMapModel.get(currentPosition + 1)
						.getUserTopic().getName());
				prevButton.setVisibility(View.VISIBLE);
				prevText.setVisibility(View.VISIBLE);
				prevText.setText(customMapModel.get(currentPosition - 1)
						.getUserTopic().getName());
			}
		}

	}

	public void goToPreviouse(ArrayList<MapModel> mapModel, String distance) {
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
				+ "\n" + mapModel.get(prevPosition).getTopic().getAddress()
				+ "\n" + "Distance:" + distance);

	}

	public void goToNext(ArrayList<MapModel> mapModel, String distance) {
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
				+ "\n" + mapModel.get(nextPosition).getTopic().getAddress()
				+ "\n" + "Distance:" + distance);

	}

	public void goToPreviouseCustomMapModel(
			ArrayList<CustomMapModel> customMapModel, String distance) {

		int prevPosition = currentPosition - 1;
		if (prevPosition == 0) {

			prevButton.setVisibility(View.GONE);
			prevText.setVisibility(View.GONE);

			nextButton.setVisibility(View.VISIBLE);
			nextText.setVisibility(View.VISIBLE);

			nextText.setText(customMapModel.get(prevPosition + 1)
					.getUserTopic().getName());
			currentPosition = prevPosition;

		} else {

			nextButton.setVisibility(View.VISIBLE);
			nextText.setVisibility(View.VISIBLE);
			nextText.setText(customMapModel.get(prevPosition + 1)
					.getUserTopic().getName());
			prevButton.setVisibility(View.VISIBLE);
			prevText.setVisibility(View.VISIBLE);
			prevText.setText(customMapModel.get(prevPosition - 1)
					.getUserTopic().getName());
			currentPosition = prevPosition;

		}

		currentText.setText(customMapModel.get(prevPosition).getUserTopic()
				.getName()
				+ "\n" + "Distance:" + distance);

	}

	public void goToNextCustomMapModel(
			ArrayList<CustomMapModel> customMapModel, String distance) {
		int nextPosition = currentPosition + 1;
		if (nextPosition == customMapModel.size() - 1) {

			prevButton.setVisibility(View.VISIBLE);
			prevText.setVisibility(View.VISIBLE);

			nextButton.setVisibility(View.GONE);
			nextText.setVisibility(View.GONE);

			prevText.setText(customMapModel.get(nextPosition - 1)
					.getUserTopic().getName());
			currentPosition = nextPosition;

		} else {

			nextButton.setVisibility(View.VISIBLE);
			nextText.setVisibility(View.VISIBLE);
			nextText.setText(customMapModel.get(nextPosition + 1)
					.getUserTopic().getName());
			prevButton.setVisibility(View.VISIBLE);
			prevText.setVisibility(View.VISIBLE);
			prevText.setText(customMapModel.get(nextPosition - 1)
					.getUserTopic().getName());
			currentPosition = nextPosition;

		}

		currentText.setText(customMapModel.get(nextPosition).getUserTopic()
				.getName()

				+ "\n" + "Distance:" + distance);

	}

	/**
	 * SET UP THE DATE
	 */

	/**
	 * Set up the selected user date
	 * 
	 * @param selectedYear
	 * @param selectedMonth
	 * @param selectedDay
	 * @param dateText
	 */

	public void setSelectedDate(int selectedYear, int selectedMonth,
			int selectedDay) {
		year = selectedYear;
		month = selectedMonth;
		day = selectedDay;

		// set selected date into Text View
		dateText.setText((month + 1) + "/" + day + "/" + year);

	}

	/**
	 * set up the current date
	 * 
	 * @param dateText
	 */
	public void setCurrentDate() {
		final Calendar calendar = Calendar.getInstance();

		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);

		dateText.setText(day + "/" + (month + 1) + "/" + year);

	}

	/**
	 * SET THE TIME
	 */

	/**
	 * Set the selected Time
	 * 
	 * @param selectedHour
	 * @param selectedMinute
	 * @param timeText
	 */

	public void setSelectedTime(int selectedHour, int selectedMinute) {
		hour = selectedHour;
		minute = selectedMinute;

		// set selected time into Text View

		if (minute < 10) {

			timeText.setText(hour + ":0" + minute);
		} else {
			timeText.setText(hour + ":" + minute);
		}

	}

	/**
	 * Set the Current Time
	 * 
	 * @param timeText
	 */

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

	public void setScheduledAllarm(Context context, Schedule schedule) {

		Calendar currentTime = Calendar.getInstance();
		Calendar calendar = Calendar.getInstance();

		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.clear();

		calendar.set(year, month, day, hour - 1, minute, 00);

		if (calendar.compareTo(currentTime) < 0) {
			Log.d("StreetmapUtil", "Incorrect time");
		} else {

			ScheduleAlarm scheduleAlarm = new ScheduleAlarm(context);
			scheduleAlarm.setScheduleAllarm(calendar.getTimeInMillis(),
					schedule);
		}

	}

	/**
	 * THE legend alert dialog box
	 * 
	 * @param context
	 */
	public void setUpLegendAlertDialog(Context context) {
		// We create the view
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		View dialogView = layoutInflater.inflate(R.layout.legend_prompt_layout,
				null); // set up the dialog
						// builder

		legendText = (TextView) dialogView.findViewById(R.id.legendText);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
		alertDialogBuilder.setView(dialogView);
		alertDialogBuilder.setTitle("Legend");
		alertDialogBuilder.setIcon(R.drawable.ic_launcher);
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

		legendText.setText("Landmarks : yellow" + "\n" + "Restaurants : red"
				+ "\n" + " Museums: blue" + "\n" + "Schedule items : green"
				+ "\n\n" + "Instructions:" + "\n"
				+ "\t Tap on the radar to change the radar type" + "\n"
				+ "\t Slide top-down/bottom-up to change radar perspective");

		// create the alert dialog
		AlertDialog alert = alertDialogBuilder.create();
		// display the dialog
		alert.show();

	}

	// initiates the texts and buttons from the alert schedule prompt
	public void initiateDialogViewElements(View dialogView) {
		prevButton = (Button) dialogView.findViewById(R.id.prevButton);
		nextButton = (Button) dialogView.findViewById(R.id.nextButton);

		prevText = (TextView) dialogView.findViewById(R.id.prevText);
		nextText = (TextView) dialogView.findViewById(R.id.nextText);
		dateText = (TextView) dialogView.findViewById(R.id.dateText);
		timeText = (TextView) dialogView.findViewById(R.id.timeText);
		currentText = (TextView) dialogView.findViewById(R.id.currentText);

	}

	/**
	 * This method creates the sign the user sees on the device screen while
	 * using the street map activities
	 * 
	 * @param title
	 * @return
	 */
	public String createSign(Context context, String title) {

		try {

			final String texture = context.getCacheDir() + "/" + title + ".png";
			Paint paint = new Paint();
			/**
			 * The background image is POI_bg2
			 */

			Bitmap sign = null;
			/**
			 * Get the image from the assets folder
			 */

			String file = AssetsManager.getAssetPath("streetmap/POI_bg2.png");
			// bacground image
			Bitmap background = BitmapFactory.decodeFile(file);

			// set the configuration, decide if it can be mutable
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

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMinute() {
		return minute;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}

	public int getCurrentPosition() {
		return currentPosition;
	}

	public void setCurrentPosition(int currentPosition) {
		this.currentPosition = currentPosition;
	}

	public TextView getDateText() {
		return dateText;
	}

	public void setDateText(TextView dateText) {
		this.dateText = dateText;
	}

	public TextView getTimeText() {
		return timeText;
	}

	public void setTimeText(TextView timeText) {
		this.timeText = timeText;
	}

}
