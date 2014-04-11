/**
 * @author Matei Mircea
 * 
 * 
 * used as part of the strategy design pattern and is used to get all the comments
 */

package ro.mmp.tic.service.userservice.strategy.comment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import ro.mmp.tic.domain.Comment;
import ro.mmp.tic.service.userservice.strategy.Strategy;
import android.util.Log;

public class OperationGetComment extends Strategy {

	public ArrayList<HashMap<String, String>> execute(String topicName,
			Connection connection) {

		ArrayList<HashMap<String, String>> commentList = new ArrayList<HashMap<String, String>>(
				0);
		PreparedStatement statement = null;
		ResultSet result = null;

		try {

			String sqlQuery = "Select c.idcomment,c.idu,c.idt,c.comment,u.username from `center`.`comment` c "
					+ "join `center`.`topic` t on c.idt = t.idtopic "
					+ "join `center`.`user` u on c.idu = u.iduser "
					+ "where t.name = '"
					+ topicName
					+ "' Order by c.idcomment desc";

			statement = connection.prepareStatement(sqlQuery);

			result = statement.executeQuery();

			while (result.next()) {

				Comment comment = new Comment();

				comment.setIdcomment(result.getInt("idcomment"));
				comment.setIdu(result.getInt("idu"));
				comment.setIdt(result.getInt("idt"));
				comment.setComment(result.getString("comment"));
				String username = result.getString("username");

				HashMap<String, String> hm = new HashMap<String, String>(0);
				hm.put("userName", username);
				hm.put("comment", comment.getComment());

				commentList.add(hm);
			}

		} catch (Exception e) {
			Log.d("OperationGetComment", "ERROR ERROR ERROR " + e.toString());
		} finally {

			try {
				statement.close();
				result.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return commentList;
	}

}
