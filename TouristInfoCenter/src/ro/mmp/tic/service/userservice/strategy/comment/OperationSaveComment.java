/**
 * @author Matei Mircea
 * 
 * Used as part of the strategy design pattern and saves the comments
 * 
 */

package ro.mmp.tic.service.userservice.strategy.comment;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import ro.mmp.tic.service.userservice.strategy.Strategy;

public class OperationSaveComment extends Strategy {

	private Statement addStatement = null;

	public void execute(String username, String topicName, String comment,
			Connection connection) {
		try {

			String insertTableSQL = "Insert into `center`.`comment` (idu,idt,idut,comment) "
					+ "VALUES( (Select u.iduser from `center`.`user` u  where u.username='"
					+ username
					+ "'),"
					+ "(Select t.idtopic from `center`.`topic` t where t.name='"
					+ topicName + "'), 1," + "'" + comment + "')";

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
