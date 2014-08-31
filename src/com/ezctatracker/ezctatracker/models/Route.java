package com.ezctatracker.ezctatracker.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Route implements Parcelable {
	private long id;
	private String name; 		//bus name or train line name (kimball - orange)
	private String routeType;					//both
	private String routeNumber; 				//bus
	private String direction;					//bus
	private String stopId;						//bus
	private String stop;						//train
	private String parentId;					//train
	private boolean isFav;						//both
	
	public void setId(long id) {
		this.id = id;
	}
	
	public long getId() {
		return this.id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setRouteType(String type) {
		this.routeType = type;
	}
	
	public String getRouteType() {
		return this.routeType;
	}
	
	public void setRouteNumber(String number)
	{
		this.routeNumber = number;
	}
	
	public String getRouteNumber()
	{
		return this.routeNumber;
	}
	
	public void setDirection(String direction)
	{
		this.direction = direction;
	}
	
	public String getDirection()
	{
		return this.direction;
	}
	
	public void setStopId(String id)
	{
		this.stopId = id;
	}
	
	public String getStopId()
	{
		return this.stopId;
	}
	
	public void setStop(String stop)
	{
		this.stop = stop;
	}
	
	public String getStop()
	{
		return this.stop;
	}
	
	public void setParentId(String id)
	{
		this.parentId = id;
	}
	
	public String getParentId()
	{
		return this.parentId;
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