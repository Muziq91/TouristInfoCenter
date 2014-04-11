/**
 * @author Matei Mircea
 * 
 * Domain model of the type table
 */
package ro.mmp.tic.domain;

public class Type {

	private int idtype;
	private String type;

	public Type() {

	}

	public Type(int idtype, String type) {
		this.idtype = idtype;
		this.type = type;
	}

	public int getIdtype() {
		return idtype;
	}

	public void setIdtype(int idtype) {
		this.idtype = idtype;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
