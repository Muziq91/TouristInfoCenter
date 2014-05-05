/**
 * @author Matei Mircea
 * 
 * 
 * Domain mdoel of the schedule
 */
package ro.mmp.tic.domain;

public class Schedule {

	private int idschedule;
	private String date;
	private String time;
	private String place;
	private int alarmnr;

	public Schedule() {

	}

	public Schedule(int idschedule, String date, String time, String place,
			int alarmnr) {

		this.idschedule = idschedule;
		this.date = date;
		this.time = time;
		this.place = place;
		this.setAlarmnr(alarmnr);

	}

	public int getIdschedule() {
		return idschedule;
	}

	public void setIdschedule(int idschedule) {
		this.idschedule = idschedule;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public int getAlarmnr() {
		return alarmnr;
	}

	public void setAlarmnr(int alarmnr) {
		this.alarmnr = alarmnr;
	}
}
