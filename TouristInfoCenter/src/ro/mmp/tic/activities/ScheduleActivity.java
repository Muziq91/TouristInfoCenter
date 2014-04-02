package ro.mmp.tic.activities;

import java.util.ArrayList;

import org.w3c.dom.Text;

import ro.mmp.tic.R;
import ro.mmp.tic.domain.Schedule;
import ro.mmp.tic.service.sqlite.DataBaseConnection;
import ro.mmp.tic.service.sqlite.UpdateDataBaseService;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.DatabaseErrorHandler;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ScheduleActivity extends ListActivity {

	private ListView listView;
	private ArrayList<Schedule> allSchedule;
	private TextView placeUpdateText;
	private TextView timeUpdateText;
	private TextView dateUpdateText;
	private int clickPosition;
	AlertDialog scheduleAlertDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_schedule);

		allSchedule = new ArrayList<Schedule>(0);
		allSchedule = getAllSchedule();
		// create an ArrayAdaptar from the String Array
		ArrayAdapter<Schedule> dataAdapter = new ScheduleArrayAdapter(
				getApplicationContext(), R.layout.activity_schedule,
				allSchedule);

		/*
		 * listView = (ListView) getActivity().findViewById( R.id.listView1);
		 */

		listView = (ListView) findViewById(android.R.id.list);

		// Assign adapter to ListView
		listView.setAdapter(dataAdapter);

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);

		this.clickPosition = position;
		// We create the view
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		View dialogView = layoutInflater.inflate(
				R.layout.schedule_props_prompt_layout, null);
		// set up the dialog builder
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setView(dialogView);

		// setUpAlert(geometry.getName());
		// say what the buttons do
		alertDialogBuilder.setCancelable(false)

		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();

			}
		});

		initiateViewElements(dialogView);

		// create the alert dialog
		scheduleAlertDialog = alertDialogBuilder.create();
		// display the dialog
		scheduleAlertDialog.show();
	}

	private void initiateViewElements(View dialogView) {

		dateUpdateText = (TextView) dialogView
				.findViewById(R.id.dateUpdateText);
		timeUpdateText = (TextView) dialogView
				.findViewById(R.id.timeUpdateText);
		placeUpdateText = (TextView) dialogView
				.findViewById(R.id.placeUpdateText);

		dateUpdateText.setText(allSchedule.get(clickPosition).getDate());
		timeUpdateText.setText(allSchedule.get(clickPosition).getTime());
		placeUpdateText.setText(allSchedule.get(clickPosition).getPlace());

	}

	public void onUpdateButtonClick(View view) {
		DataBaseConnection dbc = new DataBaseConnection(this);
		Schedule updateSchedule = new Schedule();
		updateSchedule.setIdschedule(allSchedule.get(clickPosition)
				.getIdschedule());
		updateSchedule.setDate(dateUpdateText.getText().toString());
		updateSchedule.setTime(timeUpdateText.getText().toString());
		updateSchedule.setPlace(placeUpdateText.getText().toString());

		dbc.updateSchedule(updateSchedule);

		allSchedule = new ArrayList<Schedule>(0);
		allSchedule = getAllSchedule();

		// create an ArrayAdaptar from the String Array
		ArrayAdapter<Schedule> dataAdapter = new ScheduleArrayAdapter(
				getApplicationContext(), R.layout.activity_schedule,
				allSchedule);

		// Assign adapter to ListView
		listView.setAdapter(dataAdapter);

		scheduleAlertDialog.cancel();

	}

	public void onDeleteButtonClick(View view) {
		DataBaseConnection dbc = new DataBaseConnection(this);

		dbc.deleteSchedule(allSchedule.get(clickPosition));

		allSchedule = new ArrayList<Schedule>(0);
		allSchedule = getAllSchedule();

		// create an ArrayAdaptar from the String Array
		ArrayAdapter<Schedule> dataAdapter = new ScheduleArrayAdapter(
				getApplicationContext(), R.layout.activity_schedule,
				allSchedule);

		// Assign adapter to ListView
		listView.setAdapter(dataAdapter);

		scheduleAlertDialog.cancel();

	}

	private ArrayList<Schedule> getAllSchedule() {
		DataBaseConnection dbc = new DataBaseConnection(this);

		return dbc.getAllSchedule();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.schedule, menu);
		return true;
	}

	private void toastMessage(String text) {

		Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();

	}

	private class ScheduleArrayAdapter extends ArrayAdapter<Schedule> {

		private ArrayList<Schedule> scheduleList;

		public ScheduleArrayAdapter(Context context, int textViewResourceId,
				ArrayList<Schedule> scheduleList) {
			super(context, textViewResourceId, scheduleList);
			this.scheduleList = new ArrayList<Schedule>();
			this.scheduleList.addAll(scheduleList);
		}

		private class ViewHolder {
			TextView text;

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;

			Log.v("ConvertView", String.valueOf(position));

			if (convertView == null) {

				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				convertView = vi.inflate(R.layout.schedule_row_layout, null);

				holder = new ViewHolder();
				holder.text = (TextView) convertView
						.findViewById(R.id.scheduleLocation);

				convertView.setTag(holder);

				Schedule state = scheduleList.get(position);

				String text = state.getPlace() + "\n" + state.getDate() + "\n"
						+ state.getTime();
				holder.text.setText(text);

			}
			return convertView;
		}
	}

}
