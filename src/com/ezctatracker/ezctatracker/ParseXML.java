package com.ezctatracker.ezctatracker;

import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class ParseXML {
	
	//public String routeName;
	
	/*public ParseXML (String routeName){
		this.routeName = routeName;
	}*/
	
	public ArrayList<String> getRouteNumbers(XmlPullParser parser) throws 
	XmlPullParserException, IOException {
		ArrayList<String> routesNumbers = new ArrayList<String>();
		int eventType = parser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			
			if (eventType == XmlPullParser.START_TAG) {
				String name = parser.getName();
				if (name.equals("rt")) {
					parser.next();
					routesNumbers.add(parser.getText());
				}
			}
			eventType = parser.next();
		}
		return routesNumbers;	
	}
	
	public ArrayList<String> getRouteNames(XmlPullParser parser) throws 
	XmlPullParserException, IOException {
		ArrayList<String> routeNames = new ArrayList<String>();
		int eventType = parser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			
			if (eventType == XmlPullParser.START_TAG) {
				String name = parser.getName();
				if (name.equals("rtnm")) {
					parser.next();
					routeNames.add(parser.getText());
				}
			}
			eventType = parser.next();
		}
		return routeNames;	
	}
	
	public ArrayList<String> getRouteDirections(XmlPullParser parser) throws
	XmlPullParserException, IOException {
		ArrayList<String> directions = new ArrayList<String>();
		int eventType = parser.getEventType();
		
		while(eventType != XmlPullParser.END_DOCUMENT) {
			
			if (eventType == XmlPullParser.START_TAG) {
				String dir = parser.getName();
				if (dir.equals("dir")) {
					parser.next();
					directions.add(parser.getText());
				}
			}
			eventType = parser.next();
		}		
		return directions;
	}
	
	public ArrayList<String> getStopNames(XmlPullParser parser) throws
	XmlPullParserException, IOException {
		ArrayList<String> stops = new ArrayList<String>();
		int eventType = parser.getEventType();
		
		while(eventType != XmlPullParser.END_DOCUMENT) {
			
			if (eventType == XmlPullParser.START_TAG) {
				String dir = parser.getName();
				if (dir.equals("stpnm")) {
					parser.next();
					stops.add(parser.getText());
				}
			}
			eventType = parser.next();
		}		
		return stops;
	}
	
	public ArrayList<String> getStopIDs(XmlPullParser parser) throws
	XmlPullParserException, IOException {
		ArrayList<String> stopIDs = new ArrayList<String>();
		int eventType = parser.getEventType();
		
		while(eventType != XmlPullParser.END_DOCUMENT) {
			
			if (eventType == XmlPullParser.START_TAG) {
				String dir = parser.getName();
				if (dir.equals("stpid")) {
					parser.next();
					stopIDs.add(parser.getText());
				}
			}
			eventType = parser.next();
		}		
		return stopIDs;
	}
	
	public ArrayList<String> getPredictions(XmlPullParser parser) throws
	XmlPullParserException, IOException {
		ArrayList<String> predictions = new ArrayList<String>();
		int eventType = parser.getEventType();
		
		while(eventType != XmlPullParser.END_DOCUMENT) {
			
			if (eventType == XmlPullParser.START_TAG) {
				String dir = parser.getName();
				if (dir.equals("prdtm")) {
					parser.next();
					
					//get current timestamp and convert to date object
					Timestamp currentTS = new Timestamp(System.currentTimeMillis());
					Date date1 = new Date(currentTS.getTime());
					
					//get cta string timestamp and format it correctly
					String prediction = parser.getText();
					prediction = prediction.substring(0, 4) + "-" + prediction.substring(4, prediction.length());
					prediction = prediction.substring(0, 7) + "-" + prediction.substring(7, prediction.length());
					prediction = prediction.substring(0, prediction.length()) + ":00";
					
					//convert cta string timestamp into timestamp and convert to date object
					Timestamp predictedTS = Timestamp.valueOf(prediction);
					Date date2 = new Date(predictedTS.getTime());
					
					//get difference in minutes between the two date objects
					long time = getDateDiff(date1, date2, TimeUnit.MINUTES);
					
					String timeDiffString = time <= 1 ? "Approaching" : String.valueOf(time) + " minutes";
					predictions.add(timeDiffString);
				}
			}
			eventType = parser.next();
		}		
		return predictions;
	}
	
	public long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
		long diffInMillis = date2.getTime() - date1.getTime();
		return timeUnit.convert(diffInMillis, TimeUnit.MILLISECONDS);
	}
	
	//------------------------- TRAINS ----------------------------------//
	
	public ArrayList<String> getTrainDestinations(XmlPullParser parser) throws
	XmlPullParserException, IOException {
		ArrayList<String> destinations = new ArrayList<String>();
		int eventType = parser.getEventType();
		
		while(eventType != XmlPullParser.END_DOCUMENT) {
			
			if (eventType == XmlPullParser.START_TAG) {
				String dir = parser.getName();
				if (dir.equals("destNm")) {
					parser.next();
					destinations.add(parser.getText());
				}
			}
			eventType = parser.next();
		}		
		return destinations;
	}
	
	public ArrayList<String> getTrainPredictions(XmlPullParser parser) throws
	XmlPullParserException, IOException {
		ArrayList<String> predictions = new ArrayList<String>();
		int eventType = parser.getEventType();
		
		while(eventType != XmlPullParser.END_DOCUMENT) {
			
			if (eventType == XmlPullParser.START_TAG) {
				String dir = parser.getName();
				if (dir.equals("arrT")) {
					parser.next();
					
					//get current timestamp and convert to date object
					Timestamp currentTS = new Timestamp(System.currentTimeMillis());
					Date date1 = new Date(currentTS.getTime());
					
					//get cta string timestamp and format it correctly
					String prediction = parser.getText();
					prediction = prediction.substring(0, 4) + "-" + prediction.substring(4, prediction.length());
					prediction = prediction.substring(0, 7) + "-" + prediction.substring(7, prediction.length());
					
					//convert cta string timestamp into timestamp and convert to date object
					Timestamp predictedTS = Timestamp.valueOf(prediction);
					Date date2 = new Date(predictedTS.getTime());
					
					//get difference in minutes between the two date objects
					long time = getDateDiff(date1, date2, TimeUnit.MINUTES);
					
					String timeDiffString = time <= 1 ? "Approaching" : String.valueOf(time) + " minutes";
					
					predictions.add(timeDiffString);
				}
			}
			eventType = parser.next();
		}		
		return predictions;
	}
	
	public ArrayList<String> getLineColor(XmlPullParser parser) throws
	XmlPullParserException, IOException {
		ArrayList<String> colors = new ArrayList<String>();
		int eventType = parser.getEventType();
		
		while(eventType != XmlPullParser.END_DOCUMENT) {
			
			if (eventType == XmlPullParser.START_TAG) {
				String dir = parser.getName();
				if (dir.equals("rt")) {
					parser.next();
					colors.add(parser.getText());
				}
			}
			eventType = parser.next();
		}		
		return colors;
	}
	
}
