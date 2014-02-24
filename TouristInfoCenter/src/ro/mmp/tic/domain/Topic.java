package ro.mmp.tic.domain;

public class Topic {

	private int idtopic;
	private String name;

	public Topic() {

	}

	public Topic(int idtopic, String name) {

		this.idtopic = idtopic;
		this.name = name;
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
