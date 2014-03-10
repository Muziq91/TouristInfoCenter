package ro.mmp.tic.adapter.model;

import ro.mmp.tic.domain.Topic;

import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.LLACoordinate;

public class MapModel {

	private Topic topic;
	private String color;
	private IGeometry geometry;
	private LLACoordinate coordinate;

	public MapModel() {

	}

	public MapModel(Topic topic, String color) {

		this.topic = topic;
		this.color = color;
	}

	public Topic getTopic() {
		return topic;
	}

	public void setTopic(Topic topic) {
		this.topic = topic;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
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
