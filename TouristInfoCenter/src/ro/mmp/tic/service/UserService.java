/**
 * @author Matei Mircea
 * 
 * This is an abstract class used by the service part of the program
 */

package ro.mmp.tic.service;

import java.sql.Connection;

import ro.mmp.tic.domain.User;
import android.content.Context;
import android.os.AsyncTask;

public abstract class UserService extends AsyncTask<String, Void, String> {

	protected String TAG = "";
	protected User user;
	protected Connection connection;
	protected Context context;
}
