/**
 * @author Matei Mircea
 * 
 * extends User service and is used to get all comments
 * 
 */

package ro.mmp.tic.service.userservice;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import ro.mmp.tic.domain.Topic;
import ro.mmp.tic.service.UserService;
import ro.mmp.tic.service.interfaces.UserCommentLoadFinishedListener;
import ro.mmp.tic.service.userservice.strategy.Strategy;
import ro.mmp.tic.service.userservice.strategy.comment.OperationGetUserTopicComment;
import android.util.Log;

public class UserTopicCommentService extends UserService {

	private Topic topic;
	private UserCommentLoadFinishedListener finishedListener;
	private ArrayList<HashMap<String, String>> commentList;

	public UserTopicCommentService(Topic topic,
			UserCommentLoadFinishedListener finishedListener) {
		this.topic = topic;
		this.finishedListener = finishedListener;

	}

	@Override
	protected String doInBackground(String... params) {
		try {

			connection = super.getConnection();
			Strategy getComment = new OperationGetUserTopicComment();
			commentList = getComment.execute(topic.getName(), connection);

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
		finishedListener.onTaskFinished(commentList);
	}

}
