package ro.mmp.tic.adapter.model;

import ro.mmp.tic.domain.UserTopic;

public class UserLocationModel {

	private UserTopic userLocation;
	private boolean isChecked;

	public UserLocationModel() {
		this.userLocation = new UserTopic();
	}

	public UserLocationModel(UserTopic ut, boolean isChecked) {
		this.userLocation = ut;
		this.isChecked = isChecked;
	}

	public UserTopic getUserLocation() {
		return userLocation;
	}

	public void setUserLocation(UserTopic userLocation) {
		this.userLocation = userLocation;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

}
