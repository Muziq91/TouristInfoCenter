package ro.mmp.tic.activities.streetmap.util;

import java.util.ArrayList;
import java.util.Calendar;

import ro.mmp.tic.R;
import ro.mmp.tic.adapter.model.MapModel;
import ro.mmp.tic.domain.Schedule;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

		calendar.set(year, month, day, hour, minute, 00);
		Log.d("StreetmapUtil", "Set time by the user " + calendar.getTime()
				+ " current time: " + currentTime.getTime());

		if (calendar.compareTo(currentTime) < 0) {
			Log.d("StreetmapUtil", "Incorrect time");
		} else {
			Log.d("StreetmapUtil",
					"time in milis :"
							+ calendar.getTimeInMillis()
							+ " currentTime in Milis"
							+ currentTime.getTimeInMillis()
							+ " their difference: "
							+ (calendar.getTimeInMillis() - currentTime
									.getTimeInMillis()));

			ScheduleAlarm scheduleAlarm = new ScheduleAlarm();
			scheduleAlarm.setScheduleAllarm(context,
					calendar.getTimeInMillis(), schedule);
		}

	}

	/**
	 * THE legend alert dialog box
	 * 
	 * @param context
	 */
	public void setUpLegendAlertDialog(Context context) {
		Log.d("StreetMap", "Enter onLegendButtonClick Touched");
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

		Log.d("StreetMap", "set the view");

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
				+ "\n" + "Schedule items : green" + "\n\n" + "Instructions:"
				+ "\n" + "\t Tap on the radar to change the radar type" + "\n"
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
