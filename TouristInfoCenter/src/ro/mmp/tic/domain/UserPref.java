package ro.mmp.tic.domain;

public class UserPref {

	private int iduserpref;
	private int iduser;
	private String favFood;
	private String favActivity;
	private String likeHistory;

	public UserPref() {
	}

	public UserPref(int iduserpref, int iduser, String favFood,
			String favActivity, String likeHistory) {

		this.iduserpref = iduserpref;
		this.iduser = iduser;
		this.favFood = favFood;
		this.favActivity = favActivity;
		this.likeHistory = likeHistory;
	}

	public int getIduserpref() {
		return iduserpref;
	}

	public void setIduserpref(int iduserpref) {
		this.iduserpref = iduserpref;
	}

	public int getIduser() {
		return iduser;
	}

	public void setIduser(int iduser) {
		this.iduser = iduser;
	}

	public String getFavFood() {
		return favFood;
	}

	public void setFavFood(String favFood) {
		this.favFood = favFood;
	}

	public String getFavActivity() {
		return favActivity;
	}

	public void setFavActivity(String favActivity) {
		this.favActivity = favActivity;
	}

	public String getLikeHistory() {
		return likeHistory;
	}

	public void setLikeHistory(String likeHistory) {
		this.likeHistory = likeHistory;
	}

}
