/**
 * 
 * @author Matei Mircea
 * This interface is used to retrieve information once the asycnh task is finished
 */
package ro.mmp.tic.service.interfaces;

import ro.mmp.tic.domain.Like;

public interface UserLikeServiceFinishedListener {

	// the activity will implement this itnerface and we will pass the like
	// argument back to it

	void onTaskFinished(Like like);

}
