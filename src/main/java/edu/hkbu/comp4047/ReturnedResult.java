package edu.hkbu.comp4047;
/* This is a simple result wrapper
* @param url    The address.
* @param title  The title.
*/
public class ReturnedResult {
	private String url;
	private String title;
	public ReturnedResult (String url, String title) {
		this.url = url;
		this.title = title;
	}
	//getter
	public String getUrl() {
		return url;
	}
	//getter
	public String getTitle() {
		return title;
	}
	//to String
	public String toString() {
		return "url : " + this.url + "\ntitle : " + this.title;
	}
}

