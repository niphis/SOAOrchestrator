package com.smartcampus.naturalclimatesystem.local;

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
		
		if (we != null) {
			System.out.println("  temperature "	+ df.format(we.getTemperature()));
			System.out.println("  humidity    " + df.format(we.getHumidity()));
			System.out.println("  wind        " + df.format(we.getWind()));
			System.out.println("  noise level " + df.format(we.getNoiseLevel()));
			System.out.println("  co2level   " + df.format(we.getCo2level()));
		} else {
			System.out.println("An error occurred at the weather sensor");
		}

	}

}
