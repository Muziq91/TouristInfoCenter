package ro.mmp.tic.service.userservice;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import ro.mmp.tic.domain.Topic;
import ro.mmp.tic.service.UserService;
import ro.mmp.tic.service.interfaces.UserCommentLoadFinishedListener;
import ro.mmp.tic.service.userservice.strategy.Strategy;
import ro.mmp.tic.service.userservice.strategy.comment.OperationGetComment;
import android.util.Log;

public class UserCommentService extends UserService {

	private Topic topic;
	private UserCommentLoadFinishedListener finishedListener;
	private ArrayList<HashMap<String, String>> commentList;

	public UserCommentService(Topic topic,
			UserCommentLoadFinishedListener finishedListener) {
		this.topic = topic;
		this.finishedListener = finishedListener;

	}

	@Override
	protected String doInBackground(String... params) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager
					.getConnection(
							"jdbc:mysql://ec2-50-19-213-178.compute-1.amazonaws.com:3306/center",
							"Muziq91", "vasilecaine09");

			Strategy getComment = new OperationGetComment();
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
