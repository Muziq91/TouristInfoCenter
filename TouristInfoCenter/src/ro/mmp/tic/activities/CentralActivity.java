package ro.mmp.tic.activities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import ro.mmp.tic.R;
import ro.mmp.tic.activities.streetmap.SelectActivity;
import ro.mmp.tic.domain.Category;
import ro.mmp.tic.domain.Presentation;
import ro.mmp.tic.domain.Topic;
import ro.mmp.tic.domain.Type;
import ro.mmp.tic.service.interfaces.UpdateFinishedListener;
import ro.mmp.tic.service.sqlite.DataBaseConnection;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class CentralActivity extends Activity implements UpdateFinishedListener {

	private String username;
	private DataBaseConnection dbc;
	private ProgressDialog loadDialog;

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

		UpdateDB udb = new UpdateDB(dbc, this);
		udb.execute("");

		Intent i = getIntent();
		username = i.getStringExtra("loggedUser");
	}

	public void displayStreetMap(View view) {
		Intent intent = new Intent(CentralActivity.this, SelectActivity.class);
		intent.putExtra("loggedUser", username);
		startActivityForResult(intent, 0);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		loadDialog.dismiss();

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

		Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();

	}

	private class UpdateDB extends AsyncTask<String, Void, String> {

		private Connection connection;

		private PreparedStatement categoryStatement;
		private PreparedStatement typeStatement;
		private PreparedStatement topicStatement;
		private PreparedStatement presentationStatement;
		private ResultSet categooryResult;
		private ResultSet typeResult;
		private ResultSet topicResult;
		private ResultSet presentationResult;
		private DataBaseConnection dbc;
		private UpdateFinishedListener finished;

		public UpdateDB(DataBaseConnection dbc, UpdateFinishedListener finished) {
			this.dbc = dbc;
			this.finished = finished;

		}

		@Override
		protected String doInBackground(String... arg0) {
			try {

				ArrayList<Type> types = new ArrayList<Type>(0);
				ArrayList<Category> categories = new ArrayList<Category>(0);
				ArrayList<Topic> topics = new ArrayList<Topic>(0);
				ArrayList<Presentation> presentations = new ArrayList<Presentation>(
						0);

				Class.forName("com.mysql.jdbc.Driver");
				connection = DriverManager
						.getConnection(
								"jdbc:mysql://ec2-50-19-213-178.compute-1.amazonaws.com:3306/center",
								"Muziq91", "vasilecaine09");

				String categoryQuery = "SELECT c.category,c.color FROM `center`.`category` c";

				categoryStatement = connection.prepareStatement(categoryQuery);
				categooryResult = categoryStatement.executeQuery();

				while (categooryResult.next()) {
					Category c = new Category();

					c.setCategory(categooryResult.getString("category"));
					c.setColor(categooryResult.getString("color"));
					categories.add(c);
				}

				if (dbc.insertCategory(categories)) {

					String typeQuery = "SELECT t.type FROM `center`.`type` t";
					typeStatement = connection.prepareStatement(typeQuery);
					typeResult = typeStatement.executeQuery();

					while (typeResult.next()) {
						Type t = new Type();

						t.setType(typeResult.getString("type"));
						types.add(t);
					}

					if (dbc.insertType(types)) {

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

						Log.d("Central", "Updating presentation");

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
							Log.d("Central",
									"Creating the array list "
											+ p.getIdpresentation());

							presentations.add(p);

						}

						Log.d("Central", "Inserting ");

						dbc.insertPresentation(presentations);

						Log.d("Central", "FINISHED FINISHED");

					}
				}

			} catch (Exception e) {
				Log.i("TAG", "ERROR " + e.toString());
			} finally {
				try {
					connection.close();
					categooryResult.close();
					categoryStatement.close();

					typeResult.close();
					typeStatement.close();

					topicResult.close();
					topicStatement.close();

				} catch (SQLException e) {
					// TODO Auto-generated catch block
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

}
