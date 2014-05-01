package ro.mmp.tic.adapter.model;

import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.LLACoordinate;

import ro.mmp.tic.domain.UserTopic;

public class CustomMapModel {

	private UserTopic userTopic;
	private IGeometry geometry;
	private LLACoordinate coordinate;

	public CustomMapModel() {
	}

	public CustomMapModel(UserTopic userTopic) {
		this.userTopic = userTopic;

	}

	public UserTopic getUserTopic() {
		return userTopic;
	}

	public void setUserTopic(UserTopic userTopic) {
		this.userTopic = userTopic;
	}

	public IGeometry getGeometry() {
		return geometry;
	}

	public void setGeometry(IGeometry geometry) {
		this.geometry = geometry;
	}

	public LLACoordinate getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(LLACoordinate coordinate) {
		this.coordinate = coordinate;
	}

}
