/**
 * @author Matei Mircea
 * 
 * This activity will present to the user facts about a landmark and it will also let the suer like or unlike
 * the specific landmark. It will display a button that will give the user access to the comment section of the landmark
 */

package ro.mmp.tic.activities.streetmap;

import java.util.HashMap;

import ro.mmp.tic.R;
import ro.mmp.tic.activities.streetmap.util.ImageUtil;
import ro.mmp.tic.domain.Like;
import ro.mmp.tic.domain.Presentation;
import ro.mmp.tic.domain.Topic;
import ro.mmp.tic.domain.User;
import ro.mmp.tic.domain.UserTopic;
import ro.mmp.tic.service.UserService;
import ro.mmp.tic.service.interfaces.UserLikeCountFinishedListener;
import ro.mmp.tic.service.interfaces.UserUpdateLikeFinishedListener;
import ro.mmp.tic.service.sqlite.DataBaseConnection;
import ro.mmp.tic.service.userservice.UserLikeCountService;
import ro.mmp.tic.service.userservice.UserTopicLikeCountService;
import ro.mmp.tic.service.userservice.UserTopicUpdateLikeService;
import ro.mmp.tic.service.userservice.UserUpdateLikeService;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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

	private TextView descriptionText;
	private ImageView imageView;
	private Button likeButton;
	private Button unlikeButton;
	private WebView chartView;
	private String username;
	private String topicName;
	private String token;
	private Like like;
	private User user;
	private Topic topic;
	private UserTopic userTopic;
	private ProgressDialog loadDialog;
	private boolean exists = false;
	private DataBaseConnection dataBaseConnection;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_presentation);
		// Show the Up button in the action bar.
		setupActionBar();

		setupInterface();

	}

	// This method is Used to prepare the interface for the presentation for the
	// selected location
	private void setupInterface() {
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

		chartView = (WebView) findViewById(R.id.chartView);
		descriptionText = (TextView) findViewById(R.id.descriptionText);
		imageView = (ImageView) findViewById(R.id.imageView);
		likeButton = (Button) findViewById(R.id.likeButton);
		unlikeButton = (Button) findViewById(R.id.unlikeButton);

		Intent intent = getIntent();
		token = intent.getStringExtra("token");
		topicName = intent.getStringExtra("name");
		username = intent.getStringExtra("loggedUser");

		// set the title based on the location selected
		setTitle(topicName);

		if (token.equals("streetmap")) {

			// sets the like and unlike buttons
			setLikeButtons();

			// gets the presentation to be displayed
			Presentation presentation = dataBaseConnection
					.getPresentaion(topicName);

			// set up the interface elements information
			descriptionText.setText(presentation.getDescription());

			Context context = imageView.getContext();
			// get the id of the image for this presentation
			int id = context.getResources().getIdentifier(
					presentation.getImage(), "drawable",
					context.getPackageName());
			imageView.setImageResource(id);

			user = new User();
			user.setUsername(username);

			topic = new Topic();
			topic.setName(topicName);

			UserService likeCountService = new UserLikeCountService(topic,
					getApplicationContext(), this);
			likeCountService.execute("");
		} else if (token.equals("userstreetmap")) {
			// sets the like and unlike buttons
			setUserTopicLikeButtons();

			// gets the presentation that will be displayed
			Presentation presentation = dataBaseConnection
					.getUserTopicPresentaion(topicName);

			// set up the interface elements information
			descriptionText.setText(presentation.getDescription());

			ImageUtil iu = new ImageUtil(this);
			// gets the iamge that was saved for this presentation
			imageView.setImageBitmap(iu.getThumbnail(presentation.getImage()));

			user = new User();
			user.setUsername(username);

			userTopic = new UserTopic();
			userTopic.setName(topicName);

			UserService likeCountService = new UserTopicLikeCountService(
					userTopic, getApplicationContext(), this);
			likeCountService.execute("");

		}

	}

	/**
	 * This method is used to prepare the buttons for a topic
	 */

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

			if (like.getLike() == 1) {
				likeButton.setVisibility(View.GONE);
				unlikeButton.setVisibility(View.VISIBLE);
			} else if (like.getLike() == 0) {
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
	 * This method is used to prepare the buttons for a user topic
	 */
	private void setUserTopicLikeButtons() {

		HashMap<String, String> likes = dataBaseConnection.getUserTopicLike(
				username, topicName);
		like = new Like();
		if (likes.get("likes") != null) {
			exists = true;

			like.setIduser(Integer.parseInt(likes.get("iduser")));
			like.setIdtopic(Integer.parseInt(likes.get("idtopic")));
			like.setIdusertopic(Integer.parseInt(likes.get("idusertopic")));
			like.setLike(Integer.parseInt(likes.get("likes")));
			like.setUnlike(Integer.parseInt(likes.get("unlikes")));

			if (like.getLike() == 1) {
				likeButton.setVisibility(View.GONE);
				unlikeButton.setVisibility(View.VISIBLE);
			} else if (like.getLike() == 0) {
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
	public void onLikeTopicButtonClick(View v) {

		loadDialog.show();

		if (token.equals("streetmap")) {
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
			setLikeButtons();
		} else if (token.equals("userstreetmap")) {

			likeButton.setVisibility(View.GONE);
			unlikeButton.setVisibility(View.VISIBLE);

			like.setLike(1);
			like.setUnlike(0);
			// updates the cloud database like column when the likebutton is
			// pressed
			UserService userUpdateLike = new UserTopicUpdateLikeService(user,
					userTopic, getApplicationContext(), like, exists, this);
			userUpdateLike.execute("");

			// updates the sqlite database like column when the likebutton is
			// pressed
			if (exists == false) {
				dataBaseConnection.insertUserTopicLike(username, topicName,
						like);
				exists = true;
			} else {
				dataBaseConnection.updateLike(username, topicName, like);
			}
			setUserTopicLikeButtons();
		}
	}

	/**
	 * When the user presses the unlike button
	 */
	public void onUnlikeTopicButtonClick(View v) {
		loadDialog.show();

		if (token.equals("streetmap")) {
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
			setLikeButtons();
		} else if (token.equals("userstreetmap")) {

			unlikeButton.setVisibility(View.GONE);
			likeButton.setVisibility(View.VISIBLE);

			like.setLike(0);
			like.setUnlike(1);

			// updates the cloud database like column when the unlikebutton is
			// pressed
			UserService userUpdateLike = new UserTopicUpdateLikeService(user,
					userTopic, getApplicationContext(), like, exists, this);
			userUpdateLike.execute("");

			// updates the sqlite database like column when the unlikebutton is
			// pressed
			if (exists == false) {
				dataBaseConnection.insertUserTopicLike(username, topicName,
						like);
				exists = true;
			} else {
				dataBaseConnection.updateLike(username, topicName, like);
			}
			setUserTopicLikeButtons();
		}

	}

	/**
	 * Whent the user presses the comment button he will be sent to the page
	 * where users can see a chart of likes and unlikes and a comment section
	 * 
	 * @param v
	 */

	public void onCommentButtonPush(View v) {
		Intent intent = new Intent(PresentationActivity.this,
				OpinionActivity.class);

		intent.putExtra("loggedUser", user.getUsername());
		intent.putExtra("name", topicName);
		intent.putExtra("token", token);
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

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

	/**
	 * When the count of the likes has finished it returns here to display the
	 * chart with all the likes and unlikes
	 */
	@Override
	public void onTaskFinished(int likeCount, int unlikeCount) {

		loadDialog.dismiss();

		// here we load the chart with information from the database about the
		// number of likes and unlikes
		if (token.equals("streetmap")) {
			String mUrl = "http://chart.apis.google.com/chart?" + "cht=p3&" + // type
					// of
					// graph
					"chs=500x200&" + // pixel dimension of chart
					"chd=t:" + likeCount + "," + unlikeCount + "&" + // data
					// to
					// display
					// in
					// chart
					"chts=000000,24&" + // specifies the font colour and size of
										// the
					// title
					"chtt=Like+Unlike+Chart+of+" + topic.getName() + "&" + // specifies
					// the title
					// of the
					// graph
					"chl=Like|Unlike" + // chart labels
					"&chco=335423,9011D3&" + // chart color
					"chdl=Like|Unlike";
			chartView.loadUrl(mUrl);
		} else if (token.equals("userstreetmap")) {

			String mUrl = "http://chart.apis.google.com/chart?" + "cht=p3&" + // type
					// of
					// graph
					"chs=500x200&" + // pixel dimension of chart
					"chd=t:" + likeCount + "," + unlikeCount + "&" + // data
					// to
					// display
					// in
					// chart
					"chts=000000,24&" + // specifies the font colour and size of
										// the
					// title
					"chtt=Like+Unlike+Chart+of+" + userTopic.getName() + "&" + // specifies
					// the title
					// of the
					// graph
					"chl=Like|Unlike" + // chart labels
					"&chco=335423,9011D3&" + // chart color
					"chdl=Like|Unlike";
			chartView.loadUrl(mUrl);
		}

	}

	/**
	 * Creates andSends a Toast message
	 * 
	 * @param text
	 */
	private void toastMessage(String text) {

		Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT)
				.show();

	}

	/**
	 * When the like or unlike has been updated in the database it comes here in
	 * order to update the chart
	 */
	@Override
	public void onTaskFinished() {
		loadDialog.dismiss();

		
		
		if (token.equals("streetmap")) {
			setLikeButtons();
			UserService likeCountService = new UserLikeCountService(topic,
					getApplicationContext(), this);
			likeCountService.execute("");
		} else if (token.equals("userstreetmap")) {
			setUserTopicLikeButtons();
			UserService likeCountService = new UserTopicLikeCountService(
					userTopic, getApplicationContext(), this);
			likeCountService.execute("");
		}
	}

}
