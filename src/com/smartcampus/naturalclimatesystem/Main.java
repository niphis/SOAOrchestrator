package com.smartcampus.naturalclimatesystem;

import java.text.DecimalFormat;

public class Main {
	public static void main(String[] args) {
		Location l = new Location();
		l.setRoomId("A12");
		l.setWindowsId(2);
		
		System.out.println("TEST Locale");
				
		System.out.println("getFullLocation(): ");
		System.out.println("  " + l.getFullLocation());
		
		NaturalClimateSystemService ncss = new NaturalClimateSystemService();
		
    	System.out.println("openWindow():");
    	System.out.println("  " + ncss.openWindow(l));
    	System.out.println("closeWindow():");
    	System.out.println("  " + ncss.closeWindow(l));
    	
    	System.out.println("getWeatherCondition:");
    	WeatherCondition we = ncss.getWeatherCondition(l);
    	
    	
    	DecimalFormat df = new DecimalFormat("#.00");
    	
    	System.out.println("  temperature " + df.format(we.get_temperature()));
    	System.out.println("  humidity    " + df.format(we.get_humidity()));
    	System.out.println("  wind        " + df.format(we.get_wind()));
    	System.out.println("  noise level " + df.format(we.get_noiseLevel()));
    	System.out.println("  pollution   " + df.format(we.get_pollution()));
		
		
	}

}
