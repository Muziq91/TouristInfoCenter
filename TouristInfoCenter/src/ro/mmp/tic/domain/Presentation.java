package ro.mmp.tic.domain;

import android.graphics.Bitmap;

public class Presentation {

	private int idpresentation;
	private int idtopic;
	private String image;
	private String description;

	public Presentation() {
	}

	public Presentation(int idpresentation, int idtopic, String image,
			String description) {
		this.idpresentation = idpresentation;
		this.idtopic = idtopic;
		this.image = image;
		this.description = description;

	}

	public int getIdpresentation() {
		return idpresentation;
	}

	public void setIdpresentation(int idpresentation) {
		this.idpresentation = idpresentation;
	}

	public int getIdtopic() {
		return idtopic;
	}

	public void setIdtopic(int idtopic) {
		this.idtopic = idtopic;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	

}
