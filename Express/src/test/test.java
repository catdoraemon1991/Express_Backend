package test;

import java.util.*;

import entity.Location;

import java.lang.*;
import java.io.*;

class test
{
	public static void main (String[] args) throws java.lang.Exception
	{
		System.out.println(sphr_time(new Location(37.7765484,-122.45065192), new Location(37.777575,-122.473499)) + " mins\n");
	}
	public static double speed = 12;
	private static double sphr_time(Location start, Location destination) {
		double lat1 = start.getLatitude();
		double lon1 = start.getLongitude();
		double lat2 = destination.getLatitude();
		double lon2 = destination.getLongitude();
		if ((lat1 == lat2) && (lon1 == lon2)) {
			return 0;
		}
		else {
			double theta = lon1 - lon2;
			double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
			dist = Math.acos(dist);
			dist = Math.toDegrees(dist);
			dist = dist * 60 * 1.1515 * 1.609344 *1000;
			return (dist / speed / 60.0);
		}
	}
}