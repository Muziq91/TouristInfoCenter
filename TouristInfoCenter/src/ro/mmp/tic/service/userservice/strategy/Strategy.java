/**
 * @author Matei Mircea
 * 
 * This abstract class is used to create the strategy design pattern.It is declares an execute method which will be overriden
 * by the classes the extends it.
 *  
 */
package ro.mmp.tic.service.userservice.strategy;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import ro.mmp.tic.domain.Like;
import ro.mmp.tic.domain.User;

public abstract class Strategy {

	// user by OperationAdd, OperatiobloginVerif,OperationVerif
	public boolean execute(User user, Connection connection) {
		return false;
	}

	// used by OperationGetLike
	public Like execute(String username, String topic, Connection connection) {
		return null;
	}

	// used by OperationSetLike
	public void execute(String username, String topic, Like like,
			Connection connection) {
	}

	// used by OperationGetLikeCount
	public int execute(String topicName, Connection connection, String type) {

		return 0;
	}

	// this one is used by operationGetComment
	public ArrayList<HashMap<String, String>> execute(String topicName,
			Connection connection) {
		// TODO Auto-generated method stub
		return null;
	}

}
