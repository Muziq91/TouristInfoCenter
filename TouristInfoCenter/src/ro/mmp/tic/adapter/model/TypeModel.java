package ro.mmp.tic.adapter.model;

import ro.mmp.tic.domain.Type;

public class TypeModel {

	private Type type;
	private boolean selected;

	public TypeModel() {
		type = new Type();
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

}
