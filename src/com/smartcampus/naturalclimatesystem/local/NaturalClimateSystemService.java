package com.smartcampus.naturalclimatesystem.local;


public class NaturalClimateSystemService implements NaturalClimateSystem {
	private NCSClient _user;
	private NaturalClimateSystem _system;

	/**
	 * Opens the specified window windowId in the room roomId. Returns False if the operation fails, True otherwise.
	 */
	public boolean openWindow(Location aLocation) {
		return true;
	}

	/**
	 * Closes the specified window windowId in the room roomId. Returns False if the operation fails, True otherwise.
	 */
	public boolean closeWindow(Location aLocation) {
		return true;
	}

	public WeatherCondition getWeatherCondition(Location aLocation) {
		float temp = 10 + (float) Math.random() * 50 ;
        float hum = 1 + (float) Math.random() * 100;
        float wind = 1 + (float) Math.random() * 50;
        float noise = 1 + (float) Math.random() * 100;
        int poll = (int) (1 + Math.random() * 100);
        
		return new WeatherCondition(aLocation, temp, hum, wind, noise, poll);
	}
}