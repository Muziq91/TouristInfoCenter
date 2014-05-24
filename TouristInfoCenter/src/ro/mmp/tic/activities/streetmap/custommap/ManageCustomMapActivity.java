package ro.mmp.tic.activities.streetmap.custommap;

import java.util.ArrayList;
import java.util.HashMap;

import ro.mmp.tic.R;
import ro.mmp.tic.activities.streetmap.util.ImageUtil;
import ro.mmp.tic.adapter.CustomMapAdapter;
import ro.mmp.tic.adapter.model.CustomMapModel;
import ro.mmp.tic.service.UserService;
import ro.mmp.tic.service.interfaces.UserTopicAddFinishedListener;
import ro.mmp.tic.service.interfaces.UserTopicGetFinishedListener;
import ro.mmp.tic.service.sqlite.DataBaseConnection;
import ro.mmp.tic.service.userservice.UserTopicAdd;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ManageCustomMapActivity extends Activity implements
		UserTopicAddFinishedListener, UserTopicGetFinishedListener {

	private ArrayList<CustomMapModel> customMapModel;
	private DataBaseConnection dbc;
	private ListView listView;
	private HashMap<String, String> clickPosition;
	private AlertDialog customMapAlertDialog;
	private TextView nameUpdateText;
	private TextView descriptionUpdateText;
	private TextView latUpdateText;
	private TextView lngUpdateText;
	private Spinner customColorSpinner;
	private String customColorString;
	private String username;
	private ProgressDialog loadDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_custom_map);

		/**
		 * Create connection to database
		 */
		dbc = new DataBaseConnection(this);
		// build the custom map model
		customMapModel = new ArrayList<CustomMapModel>(0);
		Intent intent = getIntent();
		username = intent.getStringExtra("loggedUser");

		customMapModel = dbc.getUserCustomMapModel(username);

		final ArrayList<HashMap<String, String>> userLocations = new ArrayList<HashMap<String, String>>(
				0);

		for (CustomMapModel c : customMapModel) {

			Log.d("ManageCustomMapActivity", c.getUserTopic().getImage());
			HashMap<String, String> map = new HashMap<String, String>(0);

			map.put("IDUSERTOPIC",
					String.valueOf(c.getUserTopic().getIdusertopic()));
			map.put("IDUSER", String.valueOf(c.getUserTopic().getIduser()));
			map.put("NAME", c.getUserTopic().getName());
			map.put("DESCRIPTION", c.getUserTopic().getDescription());
			map.put("LAT", String.valueOf(c.getUserTopic().getLat()));
			map.put("LNG", String.valueOf(c.getUserTopic().getLng()));
			map.put("IMAGE", c.getUserTopic().getImage());
			map.put("COLOR", c.getUserTopic().getColor());

			userLocations.add(map);

		}

		listView = (ListView) findViewById(R.id.customMapListView);
		final CustomMapAdapter adapter = new CustomMapAdapter(this,
				userLocations);

		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View parent,
					int position, long id) {

				createDialogView(userLocations.get(position));
				toastMessage("Am apasat pe: "
						+ userLocations.get(position).get("IMAGE"));
			}
		});

		loadDialog = new ProgressDialog(this);
		/*
		 * UserService us = new UserGetAllUserTopic(this, this);
		 * us.execute("Execute");
		 */
	}

	private void createDialogView(HashMap<String, String> selectedMap) {

		this.clickPosition = selectedMap;
		// We create the view
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		View dialogView = layoutInflater.inflate(
				R.layout.custom_map_props_prompt_layout, null);
		// set up the dialog builder
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setView(dialogView);
		alertDialogBuilder.setTitle("Update Location");
		alertDialogBuilder.setIcon(R.drawable.ic_launcher);

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
		customMapAlertDialog = alertDialogBuilder.create();
		// display the dialog
		customMapAlertDialog.show();

	}

	private void initiateViewElements(View dialogView) {

		nameUpdateText = (TextView) dialogView
				.findViewById(R.id.nameUpdateText);
		descriptionUpdateText = (TextView) dialogView
				.findViewById(R.id.descriptionUpdateText);
		latUpdateText = (TextView) dialogView.findViewById(R.id.latUpdateText);
		lngUpdateText = (TextView) dialogView.findViewById(R.id.lngUpdateText);
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

		nameUpdateText.setText(clickPosition.get("NAME"));
		descriptionUpdateText.setText(clickPosition.get("DESCRIPTION"));
		latUpdateText.setText(clickPosition.get("LAT"));
		lngUpdateText.setText(clickPosition.get("LNG"));

	}

	public void onUpdateButtonClick(View view) {
		DataBaseConnection dbc = new DataBaseConnection(this);
		clickPosition.put("NAME", nameUpdateText.getText().toString());
		clickPosition.put("DESCRIPTION", descriptionUpdateText.getText()
				.toString());
		clickPosition.put("LAT", latUpdateText.getText().toString());
		clickPosition.put("LNG", lngUpdateText.getText().toString());

		if (customColorString != null) {
			clickPosition
					.put("COLOR", customColorString.toLowerCase() + ".png");
		} else {
			clickPosition.put("COLOR", clickPosition.get("COLOR"));
		}

		dbc.updateCustomMap(clickPosition);

		customMapModel = dbc.getUserCustomMapModel(username);

		final ArrayList<HashMap<String, String>> userLocations = new ArrayList<HashMap<String, String>>(
				0);

		for (CustomMapModel c : customMapModel) {

			Log.d("ManageCustomMapActivity", c.getUserTopic().getImage());
			HashMap<String, String> map = new HashMap<String, String>(0);

			map.put("NAME", c.getUserTopic().getName());
			map.put("DESCRIPTION", c.getUserTopic().getDescription());
			map.put("LAT", String.valueOf(c.getUserTopic().getLat()));
			map.put("LNG", String.valueOf(c.getUserTopic().getLng()));
			map.put("IMAGE", c.getUserTopic().getImage());
			map.put("COLOR", c.getUserTopic().getColor());
			userLocations.add(map);

		}

		listView = (ListView) findViewById(R.id.customMapListView);
		final CustomMapAdapter adapter = new CustomMapAdapter(this,
				userLocations);

		listView.setAdapter(adapter);
		customMapAlertDialog.cancel();

	}

	public void onDeleteButtonClick(View view) {

		DataBaseConnection dbc = new DataBaseConnection(this);
		clickPosition.put("NAME", nameUpdateText.getText().toString());
		clickPosition.put("DESCRIPTION", descriptionUpdateText.getText()
				.toString());
		clickPosition.put("LAT", latUpdateText.getText().toString());
		clickPosition.put("LNG", lngUpdateText.getText().toString());
		this.deleteFile(clickPosition.get("IMAGE"));
		dbc.deleteCustomMap(clickPosition);

		customMapModel = dbc.getUserCustomMapModel(username);

		final ArrayList<HashMap<String, String>> userLocations = new ArrayList<HashMap<String, String>>(
				0);

		for (CustomMapModel c : customMapModel) {

			Log.d("ManageCustomMapActivity", c.getUserTopic().getImage());
			HashMap<String, String> map = new HashMap<String, String>(0);

			map.put("NAME", c.getUserTopic().getName());
			map.put("DESCRIPTION", c.getUserTopic().getDescription());
			map.put("LAT", String.valueOf(c.getUserTopic().getLat()));
			map.put("LNG", String.valueOf(c.getUserTopic().getLng()));
			map.put("IMAGE", c.getUserTopic().getImage());
			map.put("COLOR", c.getUserTopic().getColor());

			userLocations.add(map);

		}

		listView = (ListView) findViewById(R.id.customMapListView);
		final CustomMapAdapter adapter = new CustomMapAdapter(this,
				userLocations);

		listView.setAdapter(adapter);
		customMapAlertDialog.cancel();

	}

	public void onSubmitButtonClick(View view) {

		ImageUtil imageUtil = new ImageUtil(this);
		Bitmap image = imageUtil.getThumbnail(clickPosition.get("IMAGE"));

		if (image != null) {
			clickPosition.put("DESCRIPTION", "Inserted by "+username+":\n"+clickPosition.get("DESCRIPTION"));
			UserService addUserTopic = new UserTopicAdd(clickPosition, image,
					username, this, this);
			addUserTopic.execute("UserTopicAdd");
			loadDialog.setTitle("Submit your location");
			loadDialog.setMessage("Please wait.");
			loadDialog.setCancelable(false);
			loadDialog.setIndeterminate(true);

			try {
				loadDialog.show();
			} catch (Exception e) {

			}
		} else {
			toastMessage("Cannot submit, image does not exist");
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.manage_custom_map, menu);
		return true;
	}

	/**
	 * Creates andSends a Toast message
	 * 
	 * @param text
	 */
	private void toastMessage(String text) {

		Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();

	}

	@Override
	public void onTaskFinished(boolean canAdd) {
		loadDialog.dismiss();
		if (canAdd) {
			toastMessage("The lcoation was added");
		} else {
			toastMessage("Could not add this topic, it already exists");
		}

	}

	@Override
	public void onTaskFinished(ArrayList<HashMap<String, String>> userTopicList) {

		listView = (ListView) findViewById(R.id.customMapListView);
		final CustomMapAdapter adapter = new CustomMapAdapter(this,
				userTopicList);

		listView.setAdapter(adapter);

	}

}
