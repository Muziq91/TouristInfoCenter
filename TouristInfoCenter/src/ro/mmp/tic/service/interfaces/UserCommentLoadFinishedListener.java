package ro.mmp.tic.service.interfaces;

import java.util.ArrayList;
import java.util.HashMap;

public interface UserCommentLoadFinishedListener {

	void onTaskFinished(ArrayList<HashMap<String, String>> commentList);

}
