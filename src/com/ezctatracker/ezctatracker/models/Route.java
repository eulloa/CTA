package com.ezctatracker.ezctatracker.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Route implements Parcelable {
	private long id;
	private String routeName;
	private String routeType;
	private boolean isFav;
	
	public void setId(long id) {
		this.id = id;
	}
	
	public long getId() {
		return this.id;
	}
	
	public void setRouteName(String name) {
		this.routeName = name;
	}
	
	public String getRouteName() {
		return this.routeName;
	}
	
	public void setRouteType(String type) {
		this.routeType = type;
	}
	
	public String getRouteType() {
		return this.routeType;
	}
	
	public boolean getIsFav()
	{
		return this.isFav;
	}
	
	public void setIsFav(boolean isFav)
	{
		this.isFav = isFav;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {		
	}
	
	public static final Parcelable.Creator<Route> CREATOR 
	= new Parcelable.Creator<Route>() {

		@Override
		public Route createFromParcel(Parcel arg0) {
			return new Route();
		}

		@Override
		public Route[] newArray(int arg0) {
			return null;
		}
	};
}