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

import android.content.Context;
import android.graphics.Bitmap;

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
			boolean exists, Connection connection) {
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

	// used by operationSaveComment
	public void execute(String username, String name, String comment,
			Connection connection) {
		// TODO Auto-generated method stub

	}

	// used by OperationAddUserTopic
	public void execute(HashMap<String, String> location, Bitmap image,
			String username, Connection connection) {
		// TODO Auto-generated method stub

	}

	// used by OperationVerifUserTopic
	public boolean execute(HashMap<String, String> location, String username,
			Connection connection) {
		return false;
		// TODO Auto-generated method stub

	}

	// used by OperationGetUserTopic
	public ArrayList<HashMap<String, String>> execute(Connection connection,
			Context context) {
		return null;
	}
}
