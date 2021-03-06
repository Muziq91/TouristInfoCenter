/**
 *@author Matei Mircea
 *
 * The domain model of the like table
 */
package ro.mmp.tic.domain;

public class Like {

	private int idlike;
	private int iduser;
	private int idtopic;
	private int idusertopic;
	private int like;
	private int unlike;

	public Like() {

	}

	public Like(int idlike, int iduser, int idtopic, int idusertopic, int like,
			int unlike) {
		this.idlike = idlike;
		this.iduser = iduser;
		this.idtopic = idtopic;
		this.idusertopic = idusertopic;
		this.like = like;
		this.unlike = unlike;

	}

	public int getIdlike() {
		return idlike;
	}

	public void setIdlike(int idlike) {
		this.idlike = idlike;
	}

	public int getIduser() {
		return iduser;
	}

	public void setIduser(int iduser) {
		this.iduser = iduser;
	}

	public int getIdtopic() {
		return idtopic;
	}

	public void setIdtopic(int idtopic) {
		this.idtopic = idtopic;
	}

	public int getLike() {
		return like;
	}

	public void setLike(int like) {
		this.like = like;
	}

	public int getUnlike() {
		return unlike;
	}

	public void setUnlike(int unlike) {
		this.unlike = unlike;
	}

	public int getIdusertopic() {
		return idusertopic;
	}

	public void setIdusertopic(int idusertopic) {
		this.idusertopic = idusertopic;
	}

}
