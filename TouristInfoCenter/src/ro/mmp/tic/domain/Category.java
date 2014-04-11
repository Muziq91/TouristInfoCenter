/**
 *@author Matei Mircea
 * 
 * Domain model of the category table
 */
package ro.mmp.tic.domain;

public class Category {

	private int idcategory;
	private String category;
	private String color;

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

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

}
