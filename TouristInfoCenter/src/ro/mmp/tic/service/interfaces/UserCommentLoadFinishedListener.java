/**
 * @author Matei Mircea
 * 
 * Used to detect when the comment table has been updated 
 */

package ro.mmp.tic.service.interfaces;

import java.util.ArrayList;
import java.util.HashMap;

public interface UserCommentLoadFinishedListener {

	void onTaskFinished(ArrayList<HashMap<String, String>> commentList);

}
