package com.smartcampus.naturalclimatesystem.local;

public interface NaturalClimateSystem {

	/**
	 * Opens the specified window windowId in the room roomId. Returns False if
	 * the operation fails, True otherwise.
	 */
	public boolean openWindow(Location aLocation);

	/**
	 * Closes the specified window windowId in the room roomId. Returns False if
	 * the operation fails, True otherwise.
	 */
	public boolean closeWindow(Location aLocation);

	public WeatherCondition getWeatherCondition(Location aLocation);
}