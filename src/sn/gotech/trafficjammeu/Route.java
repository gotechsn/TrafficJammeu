package sn.gotech.trafficjammeu;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

public class Route {

	private LatLng firstLatLng;
	private LatLng secondLatLng;
	private int typeAlert;
	private String desc;
	private String user;
	private long id;
	private String routeId;

	public Route(LatLng firstLatLng, LatLng secondLatLng, int typelert,
			String desc, String routeId, String user) {
		super();
		this.firstLatLng = firstLatLng;
		this.secondLatLng = secondLatLng;
		this.typeAlert = typelert;
		this.desc = desc;
		this.user = user;
		this.routeId = routeId;
	}

	/**
	 * @return the typeAlert
	 */
	public int getTypeAlert() {
		return typeAlert;
	}

	/**
	 * @param typeAlert the typeAlert to set
	 */
	public void setTypeAlert(int typeAlert) {
		this.typeAlert = typeAlert;
	}

	/**
	 * @return the routeId
	 */
	public String getRouteId() {
		return routeId;
	}

	/**
	 * @param routeId the routeId to set
	 */
	public void setRouteId(String routeId) {
		this.routeId = routeId;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
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
		return typeAlert;
	}

	public void setTypelert(int typelert) {
		this.typeAlert = typelert;
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

	public Route save(Context context) {
		DataSource d = MapActivity.dataSource;
		d.addRoute(this);
		return this;
	}
}
