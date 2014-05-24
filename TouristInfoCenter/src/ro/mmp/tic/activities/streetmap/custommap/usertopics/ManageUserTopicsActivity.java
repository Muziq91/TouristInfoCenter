package ro.mmp.tic.activities.streetmap.custommap.usertopics;

import java.util.ArrayList;
import java.util.HashMap;

import ro.mmp.tic.R;
import ro.mmp.tic.activities.streetmap.custommap.UserTopicStreetMapActivity;
import ro.mmp.tic.adapter.UserLocationsAdapter;
import ro.mmp.tic.adapter.model.UserLocationModel;
import ro.mmp.tic.service.UserService;
import ro.mmp.tic.service.interfaces.UserTopicGetFinishedListener;
import ro.mmp.tic.service.sqlite.DataBaseConnection;
import ro.mmp.tic.service.userservice.UserGetAllUserTopic;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class ManageUserTopicsActivity extends Activity implements
		UserTopicGetFinishedListener {

	private static ArrayList<String> userCustomLocation = new ArrayList<String>(
			0);
	private ListView listView;
	private String username;
	private ProgressDialog loadDialog;
	private DataBaseConnection dbc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_user_locations);

		setupInterface();

	}

	private void setupInterface() {

		dbc = new DataBaseConnection(this);
		UserService us = new UserGetAllUserTopic(this, this);
		us.execute("Execute");

		loadDialog = new ProgressDialog(this);
		loadDialog.setTitle("Fetching locations");
		loadDialog.setMessage("Please wait.");
		loadDialog.setCancelable(false);
		loadDialog.setIndeterminate(true);

		try {
			loadDialog.show();
		} catch (Exception e) {

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
	public void onTaskFinished(ArrayList<HashMap<String, String>> userTopicList) {
		loadDialog.dismiss();

		ArrayList<UserLocationModel> userModel = new ArrayList<UserLocationModel>(
				0);

		for (HashMap<String, String> hm : userTopicList) {

			Log.d("dsadsadsa", hm.get("NAME"));

			UserLocationModel um = new UserLocationModel();

			um.getUserLocation().setIdusertopic(
					Integer.parseInt(hm.get("IDUSERTOPIC")));
			um.getUserLocation().setIduser(Integer.parseInt(hm.get("IDUSER")));
			um.getUserLocation().setName(hm.get("NAME"));
			um.getUserLocation().setDescription(hm.get("DESCRIPTION"));
			um.getUserLocation().setLat(Double.parseDouble(hm.get("LAT")));
			um.getUserLocation().setLng(Double.parseDouble(hm.get("LNG")));
			um.getUserLocation().setDescription(hm.get("DESCRIPTION"));
			um.getUserLocation().setImage(hm.get("IMAGE"));
			um.getUserLocation().setColor(hm.get("COLOR"));

			userModel.add(um);

		}

		final UserLocationsAdapter adapter = new UserLocationsAdapter(this,
				R.layout.activity_manage_user_locations, userModel);
		listView = (ListView) findViewById(R.id.userLocationListView);
		listView.setAdapter(adapter);

	}

	public void goToMap(View view) {
		Intent intent = getIntent();
		username = (String) intent.getStringExtra("loggedUser");

		Intent i = new Intent(this, UserTopicStreetMapActivity.class);
		i.putExtra("loggedUser", username);
		startActivityForResult(i, 0);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		loadDialog.dismiss();
		userCustomLocation.clear();

		ArrayList<UserLocationModel> userModel = new ArrayList<UserLocationModel>(
				0);

		ArrayList<HashMap<String, String>> userTopicList = dbc.getUserTopicModel();
		for (HashMap<String, String> hm : userTopicList) {

			Log.d("dsadsadsa", hm.get("NAME"));

			UserLocationModel um = new UserLocationModel();

			um.getUserLocation().setIdusertopic(
					Integer.parseInt(hm.get("IDUSERTOPIC")));
			um.getUserLocation().setIduser(Integer.parseInt(hm.get("IDUSER")));
			um.getUserLocation().setName(hm.get("NAME"));
			um.getUserLocation().setDescription(hm.get("DESCRIPTION"));
			um.getUserLocation().setLat(Double.parseDouble(hm.get("LAT")));
			um.getUserLocation().setLng(Double.parseDouble(hm.get("LNG")));
			um.getUserLocation().setDescription(hm.get("DESCRIPTION"));
			um.getUserLocation().setImage(hm.get("IMAGE"));
			um.getUserLocation().setColor(hm.get("COLOR"));

			userModel.add(um);

		}

		final UserLocationsAdapter adapter = new UserLocationsAdapter(this,
				R.layout.activity_manage_user_locations, userModel);
		listView = (ListView) findViewById(R.id.userLocationListView);
		listView.setAdapter(adapter);

	}

	public static ArrayList<String> getUserCustomLocation() {
		return userCustomLocation;
	}

	public static void setUserCustomLocation(
			ArrayList<String> userCustomLocation) {
		ManageUserTopicsActivity.userCustomLocation = userCustomLocation;
	}

}
