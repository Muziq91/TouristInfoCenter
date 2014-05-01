package ro.mmp.tic.service.userservice;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import ro.mmp.tic.domain.UserTopic;
import ro.mmp.tic.service.UserService;
import ro.mmp.tic.service.interfaces.UserTopicGetFinishedListener;
import ro.mmp.tic.service.sqlite.DataBaseConnection;
import ro.mmp.tic.service.userservice.strategy.Strategy;
import ro.mmp.tic.service.userservice.strategy.usertopic.OperationGetUserTopic;
import android.content.Context;
import android.util.Log;

public class UserGetAllUserTopic extends UserService {

	private UserTopicGetFinishedListener finishedListener;
	private ArrayList<HashMap<String, String>> userTopicList;
	private Context context;
	private DataBaseConnection dbc;

	public UserGetAllUserTopic(UserTopicGetFinishedListener finishedListener,
			Context context) {
		this.finishedListener = finishedListener;
		this.context = context;
		dbc = new DataBaseConnection(context);
	}

	@Override
	protected String doInBackground(String... params) {
		try {

			connection = super.getConnection();
			Strategy getUserTopic = new OperationGetUserTopic();
			userTopicList = getUserTopic.execute(connection, context);

			for (HashMap<String, String> ut : userTopicList) {

				Log.d("USERGETALLUSERTOPIC", "" + ut.get("IDUSER"));

				if (dbc.getCustomMapModel(ut.get("NAME")) == null) {

					UserTopic u = new UserTopic();
					u.setIdusertopic(Integer.parseInt(ut.get("IDUSERTOPIC")));
					u.setIduser(Integer.parseInt(ut.get("IDUSER")));
					u.setName(ut.get("NAME"));
					u.setDescription(ut.get("DESCRIPTION"));
					u.setLat(Double.parseDouble(ut.get("LAT")));
					u.setLng(Double.parseDouble(ut.get("LNG")));
					u.setImage(ut.get("IMAGE"));
					u.setColor(ut.get("COLOR"));

					dbc.insertUserTopic(u);

				}

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

		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		finishedListener.onTaskFinished(userTopicList);
	}

}
