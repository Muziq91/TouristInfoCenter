/**
 * @author Matei Mircea
 * 
 * This activity will present to the user facts about a landmark and it will also let the suer like or unlike
 * the specific landmark. It will display a button that will give the user access to the comment section of the landmark
 */

package ro.mmp.tic.activities.streetmap;

import java.util.HashMap;

import ro.mmp.tic.R;
import ro.mmp.tic.domain.Like;
import ro.mmp.tic.domain.Topic;
import ro.mmp.tic.domain.User;
import ro.mmp.tic.service.UserService;
import ro.mmp.tic.service.interfaces.UserLikeCountFinishedListener;
import ro.mmp.tic.service.interfaces.UserUpdateLikeFinishedListener;
import ro.mmp.tic.service.sqlite.DataBaseConnection;
import ro.mmp.tic.service.userservice.UserLikeCountService;
import ro.mmp.tic.service.userservice.UserUpdateLikeService;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PresentationActivity extends Activity implements
		UserLikeCountFinishedListener, UserUpdateLikeFinishedListener {

	private TextView textView;
	private ImageView imageView;
	private Button likeButton;
	private Button unlikeButton;
	private WebView mCharView;
	private int likeCount;
	private int unlikeCount;
	private String username;
	private String topicName;
	private Like like;
	private User user;
	private Topic topic;
	private ProgressDialog loadDialog;
	private boolean exists = false;
	private DataBaseConnection dataBaseConnection;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_presentation);
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

		dataBaseConnection = new DataBaseConnection(this);

		mCharView = (WebView) findViewById(R.id.chartView);
		textView = (TextView) findViewById(R.id.textView);
		imageView = (ImageView) findViewById(R.id.imageView);
		likeButton = (Button) findViewById(R.id.likeButton);
		unlikeButton = (Button) findViewById(R.id.unlikeButton);

		Intent intent = getIntent();
		topicName = intent.getStringExtra("name");
		username = intent.getStringExtra("loggedUser");

		setLikeButtons();

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

		UserService likeCount = new UserLikeCountService(topic,
				getApplicationContext(), this);
		likeCount.execute("");

	}

	private void setLikeButtons() {
		HashMap<String, String> likes = dataBaseConnection.getLike(username,
				topicName);
		like = new Like();
		if (likes.get("likes") != null) {
			exists = true;

			like.setIduser(Integer.parseInt(likes.get("iduser")));
			like.setIdtopic(Integer.parseInt(likes.get("idtopic")));
			like.setLike(Integer.parseInt(likes.get("likes")));
			like.setUnlike(Integer.parseInt(likes.get("unlikes")));

			toastMessage("iduser:" + like.getIduser() + " idtopic:"
					+ like.getIdtopic() + " likes:" + like.getLike()
					+ " unlike:" + like.getUnlike());
			if (like.getLike() == 1) {
				likeButton.setVisibility(View.GONE);
				unlikeButton.setVisibility(View.VISIBLE);
			} else if (like.getIdlike() == 0) {
				likeButton.setVisibility(View.VISIBLE);
				unlikeButton.setVisibility(View.GONE);
			}
		} else {

			likeButton.setVisibility(View.VISIBLE);
			unlikeButton.setVisibility(View.VISIBLE);
			exists = false;

		}

	}

	/**
	 * When the user presses the like button
	 */
	public void likeTopic(View v) {
		loadDialog.show();
		likeButton.setVisibility(View.GONE);
		unlikeButton.setVisibility(View.VISIBLE);

		like.setLike(1);
		like.setUnlike(0);
		UserService userUpdateLike = new UserUpdateLikeService(user, topic,
				getApplicationContext(), like, exists, this);
		userUpdateLike.execute("");

		if (exists == false) {
			dataBaseConnection.insertLike(username, topicName, like);
			exists = true;
		} else {
			dataBaseConnection.updateLike(username, topicName, like);
		}

	}

	/**
	 * When the user presses the unlike button
	 */
	public void unlikeTopic(View v) {
		loadDialog.show();
		unlikeButton.setVisibility(View.GONE);
		likeButton.setVisibility(View.VISIBLE);
		like.setLike(0);
		like.setUnlike(1);
		UserService userUpdateLike = new UserUpdateLikeService(user, topic,
				getApplicationContext(), like, exists, this);

		userUpdateLike.execute("");

		if (exists == false) {
			dataBaseConnection.insertLike(username, topicName, like);
			exists = true;
		} else {
			dataBaseConnection.updateLike(username, topicName, like);
		}

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
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		loadDialog.dismiss();
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

	/*
	 * @Override public void onTaskFinished(Like like) { this.like = like;
	 * 
	 * toastMessage("Am terminat de cautat likeurile " + like.getIdlike());
	 * 
	 * if (like.getIdlike() != 0) { exists = true;
	 * 
	 * if (like.getLike() == 1) { likeButton.setVisibility(View.GONE);
	 * unlikeButton.setVisibility(View.VISIBLE); } else if (like.getUnlike() ==
	 * 1) { unlikeButton.setVisibility(View.GONE);
	 * likeButton.setVisibility(View.VISIBLE); } else {
	 * unlikeButton.setVisibility(View.VISIBLE);
	 * likeButton.setVisibility(View.VISIBLE);
	 * 
	 * } } else { likeButton.setVisibility(View.VISIBLE);
	 * unlikeButton.setVisibility(View.VISIBLE); exists = false; }
	 * 
	 * }
	 */

	@Override
	public void onTaskFinished(int likeCount, int unlikeCount) {
		this.likeCount = likeCount;
		this.unlikeCount = unlikeCount;
		loadDialog.dismiss();
		// here we load the chart with information from the database about the
		// number of likes and unlikes
		String mUrl = "http://chart.apis.google.com/chart?" + "cht=p3&" + // type
				// of
				// graph
				"chs=500x200&" + // pixel dimension of chart
				"chd=t:" + this.likeCount + "," + this.unlikeCount + "&" + // data
				// to
				// display
				// in
				// chart
				"chts=000000,24&" + // specifies the font colour and size of the
				// title
				"chtt=Like+Unlike+Chart+of+" + topic.getName() + "&" + // specifies
				// the title
				// of the
				// graph
				"chl=Like|Unlike" + // chart labels
				"&chco=335423,9011D3&" + // chart color
				"chdl=Like|Unlike";
		mCharView.loadUrl(mUrl);

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
	public void onTaskFinished() {
		loadDialog.dismiss();
		UserService likeCount = new UserLikeCountService(topic,
				getApplicationContext(), this);
		likeCount.execute("");

	}

}
