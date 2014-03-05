package ro.mmp.tic.domain;

public class Topic {

	private int idtopic;
	private int idcategory;
	private int idtype;
	private String name;
	private String address;
	private double lat;
	private double lng;

	public Topic() {

	}

	public Topic(int idtopic, int idcategory, int idtype, String name,
			String address, double lat, double lng) {

		this.idtopic = idtopic;
		this.idcategory = idcategory;
		this.name = name;
		this.address = address;
		this.lat = lat;
		this.lng = lng;
	}

	public int getIdcategory() {
		return idcategory;
	}

	public void setIdcategory(int idcategory) {
		this.idcategory = idcategory;
	}

	public int getIdtype() {
		return idtype;
	}

	public void setIdtype(int idtype) {
		this.idtype = idtype;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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

	public int getIdtopic() {
		return idtopic;
	}

	public void setIdtopic(int idtopic) {
		this.idtopic = idtopic;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
