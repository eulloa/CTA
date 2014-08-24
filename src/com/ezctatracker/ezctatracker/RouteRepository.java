package com.ezctatracker.ezctatracker;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class RouteRepository 
{
	private SQLiteDatabase database;
	private SQLiteHelper dbHelper;
	private String[] allColumns = {SQLiteHelper.COLUMN_ID, SQLiteHelper.COLUMN_ROUTE_NAME,
			SQLiteHelper.COLUMN_ROUTE_TYPE};
	
	public RouteRepository(Context context) 
	{
		dbHelper = new SQLiteHelper(context);
	}
	
	public void open() throws SQLException
	{
		database = dbHelper.getWritableDatabase();
	}
	
	public void close()
	{
		dbHelper.close();
	}
	
	public Route createRoute(String routeName, String routeType)
	{
		ContentValues contentValues = new ContentValues();
		contentValues.put(SQLiteHelper.COLUMN_ROUTE_NAME, routeName);
		contentValues.put(SQLiteHelper.COLUMN_ROUTE_TYPE, routeType);
		long id = database.insert(SQLiteHelper.TABLE_ROUTES, null, contentValues);
		Cursor cursor = database.query(SQLiteHelper.TABLE_ROUTES, allColumns, 
				SQLiteHelper.COLUMN_ID + " = " + id, null, null, null, null);
		cursor.moveToFirst();
		Route route = cursorToRoute(cursor);
		cursor.close();
		return route;
	}
	
	public void deleteRoute(Route route)
	{
		long id = route.getId();
		database.delete(SQLiteHelper.TABLE_ROUTES, SQLiteHelper.COLUMN_ID + " = " + id, null);
	}
	
	public void deleteRoute(long id)
	{
		database.delete(SQLiteHelper.TABLE_ROUTES, SQLiteHelper.COLUMN_ID + " = " + id, null);
	}
	
	public void updateRoute(long id, String routeName, String routeType)
	{
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.COLUMN_ROUTE_NAME, routeName);
		values.put(SQLiteHelper.COLUMN_ROUTE_TYPE, routeType);
		database.update(SQLiteHelper.TABLE_ROUTES, values, 
				SQLiteHelper.COLUMN_ID + " = " + id, null);
	}
	
	public ArrayList<Route> getAllRoutes()
	{
		ArrayList<Route> routes = new ArrayList<Route>();
		Cursor cursor = database.query(SQLiteHelper.TABLE_ROUTES, allColumns, 
				null, null, null, null, null);
		cursor.moveToFirst();
		
		//isAfterLast checks whether cursor points to the last row
		while(!cursor.isAfterLast())
		{
			//convert cursor to route and store in arraylist
			Route route = cursorToRoute(cursor);
			routes.add(route);
			cursor.moveToNext();			
		}
		cursor.close();
		return routes;
	}
	
	public Route cursorToRoute(Cursor cursor)
	{
		Route route = new Route();
		route.setId(cursor.getLong(0));           //based on column numbers, these are default
		route.setRouteName(cursor.getString(1));  //default column number
		route.setRouteType(cursor.getString(2));
		return route;
	}
}