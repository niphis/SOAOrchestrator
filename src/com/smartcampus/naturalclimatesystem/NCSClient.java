package com.smartcampus.naturalclimatesystem;

public interface NCSClient {

	public boolean windowOpened();

	public boolean windowClosed();

	public void currentWeatherCondition(WeatherCondition aWeatherCondition);
}