/**
 * @author Matei Mircea
 * 
 * Domain model of the usertopic
 */
package ro.mmp.tic.domain;

public class UserTopic {

	private int idusertopic;
	private int iduser;
	private String name;
	private String description;
	private String image;
	private String color;
	private double lat;
	private double lng;

	public UserTopic() {
	}

	public UserTopic(int idusertopic, int iduser, String name,
			String description, String image, String color, double lat,
			double lng) {
		this.idusertopic = idusertopic;
		this.iduser = iduser;
		this.name = name;
		this.description = description;
		this.image = image;
		this.color = color;
		this.lat = lat;
		this.lng = lng;
	}

	public int getIdusertopic() {
		return idusertopic;
	}

	public void setIdusertopic(int idusertopic) {
		this.idusertopic = idusertopic;
	}

	public int getIduser() {
		return iduser;
	}

	public void setIduser(int iduser) {
		this.iduser = iduser;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

}
