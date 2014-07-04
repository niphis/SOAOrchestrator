package com.smartcampus.naturalclimatesystem.local;

public class NaturalClimateSystemService implements NaturalClimateSystem {
	private NCSClient _user;
	private NaturalClimateSystem _system;

	/**
	 * Opens the specified window windowId in the room roomId. Returns False if
	 * the operation fails, True otherwise.
	 */
	public boolean openWindow(Location aLocation) {
		if (failed(0.01))
			return false;
		return true;
	}

	/**
	 * Closes the specified window windowId in the room roomId. Returns False if
	 * the operation fails, True otherwise.
	 */
	public boolean closeWindow(Location aLocation) {
		if (failed(0.01))
			return false;
		return true;
	}

	public WeatherCondition getWeatherCondition(Location aLocation) {
		if (failed(0.01))
			return null;

		float temp = 10 + (float) Math.random() * 50;
		float hum = 1 + (float) Math.random() * 100;
		float wind = 1 + (float) Math.random() * 50;
		float noise = 1 + (float) Math.random() * 100;
		float poll = 1 + (float) Math.random() * 1000;
		
		
		WeatherCondition w = new WeatherCondition();
		
		w.setTemperature(temp);
		w.setHumidity(hum);
		w.setWind(wind);
		w.setNoiseLevel(noise);
		w.setCo2level(poll);

		return w;
	}

	private boolean failed(double threshold) {
		return Math.random() < threshold;
	}
}
