package ro.mmp.tic.domain;

public class Category {

	private int idcategory;
	private String category;

	public Category() {

	}

	public Category(int idacategory, String category) {
		this.idcategory = idacategory;
		this.category = category;

	}

	public int getIdcategory() {
		return idcategory;
	}

	public void setIdcategory(int idcategory) {
		this.idcategory = idcategory;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

}
