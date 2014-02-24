package ro.mmp.tic.service.userservice;

import java.sql.DriverManager;
import java.sql.SQLException;

import ro.mmp.tic.domain.Like;
import ro.mmp.tic.domain.Topic;
import ro.mmp.tic.domain.User;
import ro.mmp.tic.service.UserService;
import ro.mmp.tic.service.userservice.strategy.OperationGetLike;
import ro.mmp.tic.service.userservice.strategy.OperationSetLike;
import ro.mmp.tic.service.userservice.strategy.Strategy;
import android.content.Context;
import android.util.Log;

public class UserUpdateLikeService extends UserService {

	private Like like;
	private Topic topic;
	private boolean exists;

	public UserUpdateLikeService(User user, Topic topic, Context context,
			Like like, boolean exists) {
		this.user = user;
		this.topic = topic;
		this.context = context;
		this.like = like;
		this.exists = exists;
		TAG = "UserUpdateLikeService";
	}

	@Override
	protected String doInBackground(String... params) {

		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager
					.getConnection(
							"jdbc:mysql://ec2-50-19-213-178.compute-1.amazonaws.com:3306/center",
							"Muziq91", "vasilecaine09");

			Strategy getLike = new OperationSetLike();
			if (exists) {
				getLike.execute(user.getUsername(), topic.getName(), like,
						connection);
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

}
