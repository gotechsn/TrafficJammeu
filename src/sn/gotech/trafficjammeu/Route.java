package sn.gotech.trafficjammeu;

import com.google.android.gms.maps.model.LatLng;

public class Route {

	private LatLng firstLatLng;
	private LatLng secondLatLng;
	private int typealert;
	private String desc;
	private String user;

	public Route(LatLng firstLatLng, LatLng secondLatLng, int typelert,
			String desc, String user) {
		super();
		this.firstLatLng = firstLatLng;
		this.secondLatLng = secondLatLng;
		this.typealert = typelert;
		this.desc = desc;
		this.user = user;

	}

	public LatLng getFirstLatLng() {
		return firstLatLng;
	}

	public void setFirstLatLng(LatLng firstLatLng) {
		this.firstLatLng = firstLatLng;
	}

	public LatLng getSecondLatLng() {
		return secondLatLng;
	}

	public void setSecondLatLng(LatLng secondLatLng) {
		this.secondLatLng = secondLatLng;
	}

	public int getTypealert() {
		return typealert;
	}

	public void setTypelert(int typelert) {
		this.typealert = typelert;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

}
