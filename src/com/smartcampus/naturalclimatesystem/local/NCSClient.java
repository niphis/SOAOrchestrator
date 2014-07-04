package com.smartcampus.naturalclimatesystem.local;

public interface NCSClient {

	public boolean windowOpened();

	public boolean windowClosed();

	public void currentWeatherCondition(WeatherCondition aWeatherCondition);
}