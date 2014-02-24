package ro.mmp.tic.activities;

import ro.mmp.tic.R;
import ro.mmp.tic.activities.streetmap.StreetMapActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class CentralActivity extends Activity {

	private String username;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_central);

		Intent i = getIntent();
		username = i.getStringExtra("loggedUser");
	}

	public void displayStreetMap(View view) {
		Intent intent = new Intent(CentralActivity.this,
				StreetMapActivity.class);
		intent.putExtra("loggedUser", username);
		startActivity(intent);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.central, menu);
		return true;
	}

}
