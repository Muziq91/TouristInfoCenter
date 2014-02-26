/**
 * @author Matei Mircea
 * 
 * This activity will present to the user facts about a landmark and it will also let the suer like or unlike
 * the specific landmark. It will display a button that will give the user access to the comment section of the landmark
 */

package ro.mmp.tic.activities.streetmap;

import ro.mmp.tic.R;
import ro.mmp.tic.domain.Like;
import ro.mmp.tic.domain.Topic;
import ro.mmp.tic.domain.User;
import ro.mmp.tic.service.UserService;
import ro.mmp.tic.service.interfaces.UserLikeServiceFinishedListener;
import ro.mmp.tic.service.userservice.UserLikeService;
import ro.mmp.tic.service.userservice.UserUpdateLikeService;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class PresentationActivity extends Activity implements
		UserLikeServiceFinishedListener {

	private TextView textView;
	private ImageView imageView;
	private Button likeButton;
	private Button unlikeButton;

	private String username;
	private String topicName;
	private Like like;
	private User user;
	private Topic topic;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_presentation);
		// Show the Up button in the action bar.
		setupActionBar();

		textView = (TextView) findViewById(R.id.textView);
		imageView = (ImageView) findViewById(R.id.imageView);
		likeButton = (Button) findViewById(R.id.likeButton);
		unlikeButton = (Button) findViewById(R.id.unlikeButton);

		likeButton.setVisibility(View.GONE);
		unlikeButton.setVisibility(View.GONE);

		Intent intent = getIntent();
		topicName = intent.getStringExtra("name");
		username = intent.getStringExtra("loggedUser");

		if (topicName.equals("Botanical Garden")) {

			setTitle("Botanical Garden");
			textView.setText(getString(R.string.botanicalText));
			imageView.setImageResource(R.drawable.botanical);

		} else if (topicName.equals("Matei Corvin Statue")) {

			setTitle("Matei Corvin Statue");
			textView.setText(getString(R.string.statueText));
			imageView.setImageResource(R.drawable.statue);

		} else if (topicName.equals("Orthodox Cathedral")) {

			setTitle("Orthodox Cathedral");
			textView.setText(getString(R.string.cathedralText));
			imageView.setImageResource(R.drawable.cathedral);

		} else if (topicName.equals("Bastionul Croitorilor")) {

			setTitle("Bastionul Croitorilor");
			textView.setText(getString(R.string.bastionText));
			imageView.setImageResource(R.drawable.bastion);

		}

		user = new User();
		user.setUsername(username);

		topic = new Topic();
		topic.setName(topicName);

		UserService userLikeService = new UserLikeService(user, topic,
				getApplicationContext(), this);
		userLikeService.execute("");

	}

	/**
	 * When the user presses the like button
	 */
	public void likeTopic(View v) {
		likeButton.setVisibility(View.GONE);
		unlikeButton.setVisibility(View.VISIBLE);

		like.setLike(1);
		like.setUnlike(0);
		UserService userUpdateLike = new UserUpdateLikeService(user, topic,
				getApplicationContext(), like);
		userUpdateLike.execute("");

	}

	/**
	 * When the user presses the unlike button
	 */
	public void unlikeTopic(View v) {
		unlikeButton.setVisibility(View.GONE);
		likeButton.setVisibility(View.VISIBLE);
		like.setLike(0);
		like.setUnlike(1);
		UserService userUpdateLike = new UserUpdateLikeService(user, topic,
				getApplicationContext(), like);

		userUpdateLike.execute("");
	}

	/**
	 * Whent the user presses the comment button he will be sent to the page
	 * where users can see a chart of likes and unlikes and a comment section
	 * 
	 * @param v
	 */

	public void displayComment(View v) {
		Intent intent = new Intent(PresentationActivity.this,
				OpinionActivity.class);
		intent.putExtra("loggedUser", user.getUsername());
		intent.putExtra("name", topicName);
		startActivity(intent);
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
		getMenuInflater().inflate(R.menu.presentation, menu);
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

			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTaskFinished(Like like) {
		this.like = like;

		if (like.getIdlike() != 0) {

			if (like.getLike() == 1) {
				likeButton.setVisibility(View.GONE);
				unlikeButton.setVisibility(View.VISIBLE);
			} else if (like.getUnlike() == 1) {
				unlikeButton.setVisibility(View.GONE);
				likeButton.setVisibility(View.VISIBLE);
			} else {
				unlikeButton.setVisibility(View.VISIBLE);
				likeButton.setVisibility(View.VISIBLE);

			}
		}

	}

}
