/**
 * @author Matei Mircea
 * 
 * This interface is used to retrieve information once the asycnh task is finished
 */
package ro.mmp.tic.service.interfaces;

public interface UserLoginServiceFinishedListener {

	void onTaskFinished(boolean canLogin);
}
