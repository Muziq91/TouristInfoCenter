/**
 * @author Matei Mircea
 * 
 * This is an AsynchTask extendingUserServie that gets the likes from a certain landmark by a certain user
 */

package ro.mmp.tic.service.userservice;

import java.sql.DriverManager;
import java.sql.SQLException;

import ro.mmp.tic.domain.Like;
import ro.mmp.tic.domain.Topic;
import ro.mmp.tic.domain.User;
import ro.mmp.tic.service.UserService;
import ro.mmp.tic.service.interfaces.UserLikeServiceFinishedListener;
import ro.mmp.tic.service.userservice.strategy.Strategy;
import ro.mmp.tic.service.userservice.strategy.like.OperationGetLike;
import android.content.Context;
import android.util.Log;

public class UserLikeService extends UserService {

	private Like like;
	private Topic topic;

	private final UserLikeServiceFinishedListener finishedListener;

	public UserLikeService(User user, Topic topic, Context context,
			UserLikeServiceFinishedListener finishedListener) {
		this.user = user;
		this.topic = topic;

		this.context = context;
		this.finishedListener = finishedListener;
		TAG = "UserLikeService";
	}

	@Override
	protected String doInBackground(String... arg0) {

		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager
					.getConnection(
							"jdbc:mysql://ec2-50-19-213-178.compute-1.amazonaws.com:3306/center",
							"Muziq91", "vasilecaine09");

			Strategy getLike = new OperationGetLike();
			like = getLike.execute(user.getUsername(), topic.getName(),
					connection);

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

	/**
	 * We call the listener that will tell us when the doInBackground body has
	 * finished. This way we know that the execution finished and we can
	 * continue with our work in the activity
	 */

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		finishedListener.onTaskFinished(like);
	}

}
