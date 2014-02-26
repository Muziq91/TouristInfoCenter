package ro.mmp.tic.domain;

public class Comment {

	private int idcomment;
	private int idu;
	private int idt;
	private String comment;

	public Comment() {

	}

	public Comment(int idcomment, int idu, int idt, String comment) {
		this.idcomment = idcomment;
		this.idu = idu;
		this.idt = idt;
		this.comment = comment;
	}

	public int getIdcomment() {
		return idcomment;
	}

	public void setIdcomment(int idcomment) {
		this.idcomment = idcomment;
	}

	public int getIdu() {
		return idu;
	}

	public void setIdu(int idu) {
		this.idu = idu;
	}

	public int getIdt() {
		return idt;
	}

	public void setIdt(int idt) {
		this.idt = idt;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}
