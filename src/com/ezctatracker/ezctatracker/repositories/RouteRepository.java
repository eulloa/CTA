package com.ezctatracker.ezctatracker.repositories;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.ezctatracker.ezctatracker.databases.SQLiteHelper;
import com.ezctatracker.ezctatracker.models.Route;

public class RouteRepository 
{
	private SQLiteDatabase database;
	private SQLiteHelper dbHelper;
	private String[] allColumns = { dbHelper.COLUMN_ID, dbHelper.COLUMN_ROUTE_NAME,
			dbHelper.COLUMN_ROUTE_TYPE };
	
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
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.COLUMN_ROUTE_NAME, routeName);
		values.put(SQLiteHelper.COLUMN_ROUTE_TYPE, routeType);
		long id = database.insert(SQLiteHelper.TABLE_ROUTES, null, values);
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
		database.delete(SQLiteHelper.TABLE_ROUTES, SQLiteHelper.COLUMN_ID
				+ " = " + id, null);
	}
	
	public void deleteRoute(long id)
	{
		database.delete(SQLiteHelper.TABLE_ROUTES, SQLiteHelper.COLUMN_ID
				+ " = " + id, null);
	}
	
	public void udpateRoute(long id, String routeName, String routeType)
	{
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.COLUMN_ROUTE_NAME, routeName);
		values.put(SQLiteHelper.COLUMN_ROUTE_TYPE, routeType);
		database.update(SQLiteHelper.TABLE_ROUTES, values, SQLiteHelper.COLUMN_ID
				+ " = " + id, null);
	}
	
	public ArrayList<Route> getAllRoutes()
	{
		ArrayList<Route> routes = new ArrayList<Route>();
		Cursor cursor = database.query(SQLiteHelper.TABLE_ROUTES, allColumns,
				null, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast())
		{
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
		route.setId(cursor.getLong(0));				//based on column headers, 
		route.setRouteName(cursor.getString(1));    //0, 1, 2, etc...
		route.setRouteType(cursor.getString(2));
		
		return route;
	}
}