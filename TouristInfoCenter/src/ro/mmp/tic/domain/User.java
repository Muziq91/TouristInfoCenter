/**
 * @author Matei Mircea
 * 
 * This class has the role to mimic the cloud database in order to work with user objects.
 * It is useful for other classes to use and create objects to interact with the database
 * 
 */
package ro.mmp.tic.domain;

public class User {

	private int iduser;
	private String name;
	private String username;
	private String password;
	private String email;
	private String country;

	public User() {

	}

	public User(int iduser, String name, String username, String password,
			String email, String coutnry) {

		this.iduser = iduser;
		this.name = name;
		this.username = username;
		this.password = password;
		this.email = email;
		this.country = coutnry;
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

}
