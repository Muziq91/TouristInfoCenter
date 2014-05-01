/**
 * @author Matei Mircea
 * 
 * This activity entitled MainActivity will be used as the first screen the user sees. 
 * This activity will also test if the user has the internet and GPS activated. 
 * If these features are not active the program will inform the user to activate 
 * them in order to have access to the rest of the features.
 * 
 */

package ro.mmp.tic.activities;

import java.util.HashMap;

import ro.mmp.tic.R;
import ro.mmp.tic.activities.defaultschedule.DefaultScheduleActivity;
import ro.mmp.tic.service.sqlite.DataBaseConnection;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static int back = 0;
	private int exit = 0;
	private Button startAgainButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		DataBaseConnection dbx = new DataBaseConnection(this);
		dbx.upgrade();

		HashMap<String, String> user = new HashMap<String, String>(0);
		HashMap<String, String> user1 = new HashMap<String, String>(0);
		user.put("name", "a");
		user.put("username", "a");
		user.put("password", "a");
		user.put("email", "a");
		user.put("country", "a");

		user1.put("name", "b");
		user1.put("username", "b");
		user1.put("password", "b");
		user1.put("email", "b");
		user1.put("country", "b");

		dbx.insertUserAtRegister(user);
		dbx.insertUserAtRegister(user1);

		startAgainButton = (Button) findViewById(R.id.startAgainButton);
		activateInternetAndGps();

	}

	public void activateInternetAndGps() {

		if (back == 0) {
			startAgainButton.setVisibility(View.GONE);
		}

		if (isInternetOn(getApplicationContext()) == false) {

			showInternetAlert();

		}

		else if (isGPSOn() == false) {
			showGPSAlert();
		} else {
			if (back == 0) {
				back++;
				startAgainButton.setVisibility(View.VISIBLE);
				toastMessage("Both the internet and GPS are active");
				Intent nextActivity = new Intent(this,
						SplashScreenActivity.class);
				nextActivity.putExtra("loggedUser", "a");
				startActivityForResult(nextActivity, 0);
			}
		}

	}

	@Override
	public void onBackPressed() {
		exit++;
		if (exit == 1) {
			toastMessage("Push again to exit the application");
		} else if (exit == 2) {
			finish();
			back = 0;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * This method tests if the device is connected to the internet
	 * 
	 * @param context
	 * @return
	 */
	private boolean isInternetOn(Context context) {

		ConnectivityManager conManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (conManager != null) {
			NetworkInfo[] netInfo = conManager.getAllNetworkInfo();

			if (netInfo != null) {
				for (NetworkInfo i : netInfo) {
					if (i.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * We use this method to offer the user the possibility of activating the
	 * internet without exiting the application
	 */
	private void showInternetAlert() {

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		alertDialogBuilder
				.setMessage(
						"An internet connection is needed in orther to use this applciation.")
				.setCancelable(false)
				.setPositiveButton("Enable Internet now",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Intent callInternetSettingsIntent = new Intent(
										android.provider.Settings.ACTION_WIRELESS_SETTINGS);
								startActivityForResult(
										callInternetSettingsIntent, 1);
								dialog.cancel();

							}
						});
		alertDialogBuilder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {

						startAgainButton.setVisibility(View.VISIBLE);
						dialog.cancel();

					}
				});

		AlertDialog alert = alertDialogBuilder.create();
		alert.show();
	}

	/**
	 * Tests if GPS is on, on the device
	 * 
	 * @return
	 */
	private boolean isGPSOn() {

		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			return true;
		}
		return false;

	}

	/**
	 * We use this method to offer the user the possibility of activating the
	 * gps without exiting the application
	 */
	private void showGPSAlert() {

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		alertDialogBuilder
				.setMessage(
						"You need GPS to be on in orther to use this applciation.")
				.setCancelable(false)
				.setPositiveButton("Enable GPS now",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Intent callGPSSettingIntent = new Intent(
										android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
								startActivityForResult(callGPSSettingIntent, 2);
								dialog.cancel();
							}
						});
		alertDialogBuilder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {
						startAgainButton.setVisibility(View.VISIBLE);
						dialog.cancel();
					}
				});

		AlertDialog alert = alertDialogBuilder.create();
		alert.show();
	}

	/**
	 * Creates andSends a Toast message
	 * 
	 * @param text
	 */
	private void toastMessage(String text) {

		Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();

	}

	/**
	 * When the user returns to the main page and pushes the button
	 */

	public void startAgain(View v) {

		back = 0;
		activateInternetAndGps();

	}

}
