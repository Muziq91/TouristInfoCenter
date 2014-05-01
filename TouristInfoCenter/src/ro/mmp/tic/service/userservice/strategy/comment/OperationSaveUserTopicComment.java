package ro.mmp.tic.service.userservice.strategy.comment;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import ro.mmp.tic.service.userservice.strategy.Strategy;

public class OperationSaveUserTopicComment extends Strategy {

	private Statement addStatement = null;

	public void execute(String username, String topicName, String comment,
			Connection connection) {
		try {

			String insertTableSQL = "Insert into `center`.`comment` (idu,idt,idut,comment) "
					+ "VALUES( (Select u.iduser from `center`.`user` u  where u.username='"
					+ username
					+ "'), 1,"
					+ "(Select t.idusertopic from `center`.`usertopic` t where t.name='"
					+ topicName + "')," + "'" + comment + "')";

			addStatement = connection.createStatement();
			addStatement.executeUpdate(insertTableSQL);

		} catch (Exception exc) {

		} finally {
			try {

				addStatement.close();
			} catch (SQLException e) {
			}

		}

	}

}
