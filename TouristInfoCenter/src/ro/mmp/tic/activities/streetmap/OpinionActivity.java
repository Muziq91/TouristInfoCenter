/**
 * @author Matei Mircea
 * 
 * This Activity will display a chart of likes and unlike to the user. 
 * It will also display a comment section and give the user the possibility to comment on a landmark he sees
 */
package ro.mmp.tic.activities.streetmap;

import java.util.ArrayList;
import java.util.HashMap;

import ro.mmp.tic.R;
import ro.mmp.tic.domain.Topic;
import ro.mmp.tic.domain.User;
import ro.mmp.tic.service.UserService;
import ro.mmp.tic.service.interfaces.UserCommentLoadFinishedListener;
import ro.mmp.tic.service.userservice.UserCommentService;
import ro.mmp.tic.service.userservice.UserSaveCommentService;
import ro.mmp.tic.service.userservice.UserTopicCommentService;
import ro.mmp.tic.service.userservice.UserTopicSaveCommentService;
import android.annotation.TargetApi;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class OpinionActivity extends ListActivity implements
		UserCommentLoadFinishedListener {

	private User user;
	private Topic topic;
	private String token;
	private EditText commentText;
	private ProgressDialog loadDialog;

	private ArrayList<HashMap<String, String>> commentList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_opinion);
		// Show the Up button in the action bar.
		setupActionBar();

		loadDialog = new ProgressDialog(this);
		loadDialog.setTitle("Loading Content...");
		loadDialog.setMessage("Please wait.");
		loadDialog.setCancelable(false);
		loadDialog.setIndeterminate(true);

		try {
			loadDialog.show();
		} catch (Exception e) {
			// WindowManager$BadTokenException will be caught and the app
			// would not display
			// the 'Force Close' message
		}
		commentText = (EditText) findViewById(R.id.commentText);

		Intent intent = getIntent();
		token = intent.getStringExtra("token");

		String username = intent.getStringExtra("loggedUser");
		user = new User();
		user.setUsername(username);

		String topicName = intent.getStringExtra("name");
		topic = new Topic();
		topic.setName(topicName);

		setTitle(topicName);

		if (token.equals("streetmap")) {
			UserService getComment = new UserCommentService(topic, this);
			getComment.execute("");
		} else if (token.equals("userstreetmap")) {
			UserService getComment = new UserTopicCommentService(topic, this);
			getComment.execute("");
		}

	}

	public void sendComment(View v) {

		loadDialog.show();
		if (token.equals("streetmap")) {
			UserService saveComment = new UserSaveCommentService(user, topic,
					commentText.getText().toString());
			saveComment.execute("");

			UserService getComment = new UserCommentService(topic, this);
			getComment.execute("");
		} else if (token.equals("userstreetmap")) {

			UserService saveComment = new UserTopicSaveCommentService(user, topic,
					commentText.getText().toString());
			saveComment.execute("");

			UserService getComment = new UserTopicCommentService(topic, this);
			getComment.execute("");

		}

	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.opinion, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
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
	public void onTaskFinished(ArrayList<HashMap<String, String>> commentList) {
		this.commentList = commentList;

		setList();
	}

	private void setList() {

		ListAdapter adapter = new SimpleAdapter(OpinionActivity.this,
				commentList, R.layout.comment_list_layout, new String[] {
						"userName", "comment" }, new int[] { R.id.userName,
						R.id.comment });

		// setListAdapter provides the Cursor for the ListView
		// The Cursor provides access to the database data

		setListAdapter(adapter);
		loadDialog.dismiss();
	}

}
