package ro.mmp.tic.service.userservice;

import java.sql.SQLException;
import java.util.HashMap;

import ro.mmp.tic.service.UserService;
import ro.mmp.tic.service.interfaces.UserTopicAddFinishedListener;
import ro.mmp.tic.service.userservice.strategy.Strategy;
import ro.mmp.tic.service.userservice.strategy.usertopic.OperationAddUserTopic;
import ro.mmp.tic.service.userservice.strategy.usertopic.OperationVerifUserTopic;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

public class UserTopicAdd extends UserService {

	private UserTopicAddFinishedListener finishedListener;
	private HashMap<String, String> location;
	private Bitmap image;
	private String username;

	public UserTopicAdd(HashMap<String, String> location, Bitmap image,
			String username, Context context,
			UserTopicAddFinishedListener finishedListener) {

		this.location = location;
		this.image = image;
		this.username = username;
		this.context = context;
		this.finishedListener = finishedListener;

	}

	@Override
	protected String doInBackground(String... params) {
		try {
			connection = super.getConnection();

			Strategy verif = new OperationVerifUserTopic();
			if (verif.execute(location, username, connection)) {
				Strategy add = new OperationAddUserTopic();
				add.execute(location, image, username, connection);
				return "add";
			}

		} catch (Exception e) {
			Log.i("TAG", "ERROR " + e.toString());
		} finally {
			try {
				connection.close();

			} catch (SQLException e) {
				Log.i("TAG", "ERROR " + e.toString());
			}

		}

		return "noadd";
	}

	@Override
	protected void onPostExecute(String result) {

		boolean canAdd = false;
		if (result.equals("noadd")) {
			canAdd = false;

		} else {

			canAdd = true;

		}

		finishedListener.onTaskFinished(canAdd);
	}
}
