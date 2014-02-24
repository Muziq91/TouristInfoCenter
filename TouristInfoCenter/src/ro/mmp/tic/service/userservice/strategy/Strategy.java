/**
 * @author Matei Mircea
 * 
 * This abstract class is used to create the strategy design pattern.It is declares an execute method which will be overriden
 * by the classes the extends it.
 *  
 */
package ro.mmp.tic.service.userservice.strategy;

import java.sql.Connection;

import ro.mmp.tic.domain.Like;
import ro.mmp.tic.domain.User;

public abstract class Strategy {

	public boolean execute(User user, Connection connection) {
		return false;
	}

	public Like execute(String username, String topic, Connection connection) {
		return null;
	}

	public void execute(String username, String topic, Like like,
			Connection connection) {
	}

}
