/**
 * This is a buffer activity between the MainActivity and the AuthenticationActivity.
 * It is a splash screen that  displays an image for 4 seconds.
 */

package ro.mmp.tic.activities;

import ro.mmp.tic.R;
import ro.mmp.tic.activities.authenticate.AuthenticationActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;

public class SplashScreenActivity extends Activity {

	// timer
	/**
	 * time it waits until it passes to the next activity
	 */
	private static int SPLASHTIME = 4000;

	/**
	 * We use this handler to start the timer
	 */
	private Handler handler = new Handler();

	/**
	 * This is the Runnable task we used to send the applciation to the enxt
	 * activity
	 */
	private Runnable splashTask = new Runnable() {

		@Override
		public void run() {
			// This method will be executed once the timer is over

			Intent i = new Intent(SplashScreenActivity.this,
					AuthenticationActivity.class);
			startActivity(i);

			// close this activity
			finish();

		}
	};

	/**
	 * Starts the Handler and it give it the activity and the needed time to
	 * wait until it goes to AuthenticationActivity
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);

		setTitle("Welcome to TouristInfoCenter");

		handler.postDelayed(splashTask, SPLASHTIME);
	}

	/**
	 * We need to make sure that the activity returns to the MainActivity when
	 * the user pushes the back button.
	 * 
	 * we use remove callback to remove our task from the timer and it will
	 * return to the MainActivity without openning AutenticationActivity
	 */
	@Override
	public void onBackPressed() {
		handler.removeCallbacks(splashTask);
		finish();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash_screen, menu);
		return true;
	}

}
