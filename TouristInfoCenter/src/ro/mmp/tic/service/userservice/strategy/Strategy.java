package ro.mmp.tic.service.userservice.strategy;

import java.sql.Connection;

import ro.mmp.tic.domain.User;

public abstract class Strategy {

	public boolean execute(User user, Connection connection) {
		return false;
	}

}
