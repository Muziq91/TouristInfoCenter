/**
 * @author Matei Mircea
 * 
 * The central activity will display all the available options to the user. It has the role of updating the sqlite database
 * with information from the cloud database
 */

package ro.mmp.tic.activities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import ro.mmp.tic.R;
import ro.mmp.tic.activities.defaultschedule.DefaultScheduleActivity;
import ro.mmp.tic.activities.streetmap.SelectActivity;
import ro.mmp.tic.activities.streetmap.custommap.CustomStreetMapActivity;
import ro.mmp.tic.activities.streetmap.custommap.usertopics.ManageUserTopicsActivity;
import ro.mmp.tic.domain.Category;
import ro.mmp.tic.domain.Like;
import ro.mmp.tic.domain.Presentation;
import ro.mmp.tic.domain.Topic;
import ro.mmp.tic.domain.Type;
import ro.mmp.tic.service.UserService;
import ro.mmp.tic.service.interfaces.UpdateFinishedListener;
import ro.mmp.tic.service.sqlite.DataBaseConnection;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class CentralActivity extends Activity implements UpdateFinishedListener {

	private String username;
	private DataBaseConnection dbc;
	private ProgressDialog loadDialog;
	private static boolean canUpdate = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_central);

		loadDialog = new ProgressDialog(this);
		loadDialog.setTitle("Updating");
		loadDialog.setMessage("Please wait.");
		loadDialog.setCancelable(false);
		loadDialog.setIndeterminate(true);

		try {
			loadDialog.show();
		} catch (Exception e) {

		}

		dbc = new DataBaseConnection(this);

		if (canUpdate) {
			Log.d("Central Activity", "UPDATIGN");
			Log.d("Central Activity", "canUpdate is true");
			UpdateDB udb = new UpdateDB(dbc, this);
			udb.execute("");
		}else{
			Log.d("Central Activity", "canUpdate is false");
		}
		Intent i = getIntent();
		username = i.getStringExtra("loggedUser");
	}

	public void onStreetMapButtonClick(View view) {
		Intent intent = new Intent(CentralActivity.this, SelectActivity.class);
		intent.putExtra("loggedUser", username);
		canUpdate = false;
		Log.d("Central Activity", "canUpdate is false");
		startActivityForResult(intent, 0);

	}

	public void onScheduleButtonClick(View view) {
		Intent intent = new Intent(CentralActivity.this, ScheduleActivity.class);
		intent.putExtra("loggedUser", username);
		canUpdate = false;
		Log.d("Central Activity", "canUpdate is false");
		startActivityForResult(intent, 1);

	}

	public void onCustomMapButtonClick(View view) {
		Intent intent = new Intent(CentralActivity.this,
				CustomStreetMapActivity.class);
		intent.putExtra("loggedUser", username);
		canUpdate = false;
		Log.d("Central Activity", "canUpdate is false");
		startActivityForResult(intent, 2);

	}

	public void onUserTopicButtonClick(View view) {

		Intent intent = new Intent(CentralActivity.this,
				ManageUserTopicsActivity.class);
		intent.putExtra("loggedUser", username);
		canUpdate = false;
		Log.d("Central Activity", "canUpdate is false");
		startActivityForResult(intent, 3);

	}

	public void onDefaultSchdeuleButtonClick(View view) {
		Intent intent = new Intent(CentralActivity.this,
				DefaultScheduleActivity.class);
		intent.putExtra("loggedUser", username);
		canUpdate = false;
		Log.d("Central Activity", "canUpdate is false");
		startActivityForResult(intent, 4);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		
		loadDialog.dismiss();
		this.deleteFile("mapImage.png");
	}

	@Override
	public void onFinish() {
		if (loadDialog != null) {
			loadDialog.dismiss();

		}

		toastMessage("Update finished");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.central, menu);
		return true;
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

	// This is a private inner class used to update the sqlite database with
	// information from the cloud database

	private class UpdateDB extends UserService {

		// database connection
		private Connection connection;

		// Statement used to update the category database
		private PreparedStatement categoryStatement;
		// Statement used to update the type database
		private PreparedStatement typeStatement;
		// Statement used to update the topic database
		private PreparedStatement topicStatement;
		// Statement used to update the presentation database
		private PreparedStatement presentationStatement;
		// Statement used to update the like database
		private PreparedStatement likeStatement;
		// Next are the result sets that hold the information from the cloud
		// database
		private ResultSet categooryResult;
		private ResultSet typeResult;
		private ResultSet topicResult;
		private ResultSet presentationResult;
		private ResultSet likeResult;
		private DataBaseConnection dbc;
		private UpdateFinishedListener finished;

		public UpdateDB(DataBaseConnection dbc, UpdateFinishedListener finished) {
			this.dbc = dbc;
			this.finished = finished;

		}

		@Override
		protected String doInBackground(String... arg0) {
			try {

				ArrayList<Category> categories = new ArrayList<Category>(0);
				ArrayList<Type> types = new ArrayList<Type>(0);
				ArrayList<Topic> topics = new ArrayList<Topic>(0);
				ArrayList<Presentation> presentations = new ArrayList<Presentation>(
						0);
				ArrayList<Like> likes = new ArrayList<Like>(0);

				connection = super.getConnection();

				// We get all the categories from the database
				String categoryQuery = "SELECT c.category,c.color FROM `center`.`category` c";
				categoryStatement = connection.prepareStatement(categoryQuery);
				categooryResult = categoryStatement.executeQuery();

				// We populate the categories ArrayList
				while (categooryResult.next()) {
					Category c = new Category();

					c.setCategory(categooryResult.getString("category"));
					c.setColor(categooryResult.getString("color"));
					categories.add(c);
				}

				// We insert all the categories in the sqlite databae
				if (dbc.insertCategory(categories)) {

					// if everything went good and no errors were displayed we
					// can now move forward and get all types
					String typeQuery = "SELECT t.type FROM `center`.`type` t";
					typeStatement = connection.prepareStatement(typeQuery);
					typeResult = typeStatement.executeQuery();

					while (typeResult.next()) {
						Type t = new Type();

						t.setType(typeResult.getString("type"));
						types.add(t);
					}

					if (dbc.insertType(types)) {

						// if everything went good and no errors were displayed
						// we
						// can now move forward and get all topics
						String topicQuery = "SELECT t.idcategory,t.idtype,t.name,t.address,t.lat,t.lng FROM `center`.`topic` t";
						topicStatement = connection
								.prepareStatement(topicQuery);
						topicResult = topicStatement.executeQuery();

						while (topicResult.next()) {
							Topic t = new Topic();

							t.setIdcategory(topicResult.getInt("idcategory"));
							t.setIdtype(topicResult.getInt("idtype"));
							t.setName(topicResult.getString("name"));
							t.setAddress(topicResult.getString("address"));
							t.setLat(topicResult.getDouble("lat"));
							t.setLng(topicResult.getDouble("lng"));
							topics.add(t);

						}

					}
					if (dbc.insertTopic(topics)) {

						// if everything went good and no errors were displayed
						// we can now move forward and get all presentations
						String presentaionQuery = "SELECT p.idpresentation,p.idtopic,p.image,p.description FROM `center`.`presentation` p";

						presentationStatement = connection
								.prepareStatement(presentaionQuery);
						presentationResult = presentationStatement
								.executeQuery();

						while (presentationResult.next()) {

							Presentation p = new Presentation();

							p.setIdpresentation(presentationResult
									.getInt("idpresentation"));
							p.setIdtopic(presentationResult.getInt("idtopic"));
							p.setImage(presentationResult.getString("image"));
							p.setDescription(presentationResult
									.getString("description"));

							presentations.add(p);

						}

					}
					if (dbc.insertPresentation(presentations)) {
						// if everything went good and no errors were displayed
						// we can now move forward and get all presentations
						String likeQuery = "SELECT l.idlike,l.iduser,l.idtopic,l.idusertopic,l.likes,l.unlikes FROM `center`.`like` l "
								+ "join `center`.`user` u ON l.iduser=u.iduser where u.username='"
								+ username + "'";

						likeStatement = connection.prepareStatement(likeQuery);
						likeResult = likeStatement.executeQuery();

						while (likeResult.next()) {

							Like l = new Like();

							l.setIdlike(likeResult.getInt("idlike"));
							l.setIduser(likeResult.getInt("iduser"));
							l.setIdtopic(likeResult.getInt("idtopic"));
							l.setIdusertopic(likeResult.getInt("idusertopic"));
							l.setLike(likeResult.getInt("likes"));
							l.setUnlike(likeResult.getInt("unlikes"));
							Log.d("CentralActivity",
									"IDTopic: " + l.getIdtopic()
											+ " IDUserTopic:"
											+ l.getIdusertopic());

							likes.add(l);

						}

					}
					dbc.insertLikes(likes, username);
				}

			} catch (Exception e) {
				Log.i("TAG", "ERROR " + e.toString());
			} finally {
				try {
					// we close all statements and the conenctio
					connection.close();
					categooryResult.close();
					categoryStatement.close();

					typeResult.close();
					typeStatement.close();

					topicResult.close();
					topicStatement.close();

					presentationResult.close();
					presentationStatement.close();

					likeResult.close();
					likeStatement.close();

				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			finished.onFinish();
		}
	}

	public static boolean isCanUpdate() {
		return canUpdate;
	}

	public static void setCanUpdate(boolean canUpdate) {
		CentralActivity.canUpdate = canUpdate;
	}

}
