package ro.mmp.tic.adapter.model;

import ro.mmp.tic.domain.Topic;

public class LocationModel {

	private Topic topic;
	private boolean selected;

	public LocationModel() {
		topic = new Topic();
	}

	public Topic getTopic() {
		return topic;
	}

	public void setTopic(Topic topic) {
		this.topic = topic;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

}
