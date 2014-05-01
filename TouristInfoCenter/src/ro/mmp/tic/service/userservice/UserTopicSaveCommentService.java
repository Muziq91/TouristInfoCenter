package ro.mmp.tic.service.userservice;

import java.sql.SQLException;

import ro.mmp.tic.domain.Topic;
import ro.mmp.tic.domain.User;
import ro.mmp.tic.service.UserService;
import ro.mmp.tic.service.userservice.strategy.Strategy;
import ro.mmp.tic.service.userservice.strategy.comment.OperationSaveUserTopicComment;
import android.util.Log;

public class UserTopicSaveCommentService extends UserService {

	private Topic topic;
	private String comment;

	public UserTopicSaveCommentService(User user, Topic topic, String comment) {
		this.user = user;
		this.topic = topic;
		this.comment = comment;
	}

	@Override
	protected String doInBackground(String... arg0) {

		try {

			connection = super.getConnection();

			Strategy saveComment = new OperationSaveUserTopicComment();
			saveComment.execute(user.getUsername(), topic.getName(), comment,
					connection);

			Log.i("UserSaveCommentService", "5");
		} catch (Exception e) {
			Log.i("UserSaveCommentService", "ERROR " + e.toString());
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
