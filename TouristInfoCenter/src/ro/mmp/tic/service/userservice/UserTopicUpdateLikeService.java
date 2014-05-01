/**
 * @author Matei Mircea
 * 
 * 
 * extends UserService and is used to update the user like table
 */

package ro.mmp.tic.service.userservice;

import java.sql.SQLException;

import ro.mmp.tic.domain.Like;
import ro.mmp.tic.domain.User;
import ro.mmp.tic.domain.UserTopic;
import ro.mmp.tic.service.UserService;
import ro.mmp.tic.service.interfaces.UserUpdateLikeFinishedListener;
import ro.mmp.tic.service.userservice.strategy.Strategy;
import ro.mmp.tic.service.userservice.strategy.like.OperationSetUserTopicLike;
import android.content.Context;
import android.util.Log;

public class UserTopicUpdateLikeService extends UserService {

	private Like like;
	private UserTopic userTopic;
	private boolean exists;
	private UserUpdateLikeFinishedListener finishedListener;

	public UserTopicUpdateLikeService(User user, UserTopic userTopic,
			Context context, Like like, boolean exists,
			UserUpdateLikeFinishedListener finishedListener) {
		this.user = user;
		this.userTopic = userTopic;
		this.context = context;
		this.like = like;
		this.exists = exists;
		this.finishedListener = finishedListener;

		TAG = "UserUpdateLikeService";
	}

	@Override
	protected String doInBackground(String... params) {

		try {

			connection = super.getConnection();

			Log.d(TAG, "1 am ajuns aici");
			Strategy getLike = new OperationSetUserTopicLike();
			getLike.execute(user.getUsername(), userTopic.getName(), like, exists,
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

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		finishedListener.onTaskFinished();
	}

}
