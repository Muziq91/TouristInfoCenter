package ro.mmp.tic.adapter.model;

import ro.mmp.tic.domain.Category;

public class CategoryModel {

	private Category category;
	private boolean selected;

	public CategoryModel() {
		category = new Category();
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

}
