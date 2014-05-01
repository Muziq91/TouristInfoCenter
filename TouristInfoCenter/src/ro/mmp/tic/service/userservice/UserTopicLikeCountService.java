/**
 * @author Matei Mircea
 * 
 * this class extends UserService and is an asynch task, it gets the count of the likes and unlikes
 * for a certain landmark. It returns the result to opinionActivity using the finishedlistener
 */
package ro.mmp.tic.service.userservice;

import java.sql.SQLException;

import ro.mmp.tic.domain.UserTopic;
import ro.mmp.tic.service.UserService;
import ro.mmp.tic.service.interfaces.UserLikeCountFinishedListener;
import ro.mmp.tic.service.userservice.strategy.Strategy;
import ro.mmp.tic.service.userservice.strategy.like.OperationGetUserTopicLikeCount;
import android.content.Context;
import android.util.Log;

public class UserTopicLikeCountService extends UserService {

	private UserTopic userTopic;
	private int likeCount;
	private int unlikeCount;
	private final UserLikeCountFinishedListener finishedListener;

	public UserTopicLikeCountService(UserTopic userTopic, Context context,
			UserLikeCountFinishedListener finishedListener) {

		this.userTopic = userTopic;
		this.context = context;
		this.finishedListener = finishedListener;
		TAG = "UserLikeService";
	}

	@Override
	protected String doInBackground(String... arg0) {
		try {
			connection = super.getConnection();

			Strategy getLikeCount = new OperationGetUserTopicLikeCount();
			likeCount = getLikeCount.execute(userTopic.getName(), connection,
					"like");
			unlikeCount = getLikeCount.execute(userTopic.getName(), connection,
					"unlike");

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
		finishedListener.onTaskFinished(likeCount, unlikeCount);
	}

}
