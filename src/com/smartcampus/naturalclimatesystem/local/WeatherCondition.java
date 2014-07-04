package com.smartcampus.naturalclimatesystem.local;

public class WeatherCondition {
	public float _humidity;
	public float _noiseLevel;
	public float _co2level;
	public float _wind;
	public float _temperature;
	public Location _location;

	public void setHumidity(float aHumidity) {
		this._humidity = aHumidity;
	}

	public float getHumidity() {
		return this._humidity;
	}

	public void setNoiseLevel(float aNoiseLevel) {
		this._noiseLevel = aNoiseLevel;
	}

	public float getNoiseLevel() {
		return this._noiseLevel;
	}

	public void setCo2level(float aCo2level) {
		this._co2level = aCo2level;
	}

	public float getCo2level() {
		return this._co2level;
	}

	public void setWind(float aWind) {
		this._wind = aWind;
	}

	public float getWind() {
		return this._wind;
	}

	public void setTemperature(float aTemperature) {
		this._temperature = aTemperature;
	}

	public float getTemperature() {
		return this._temperature;
	}

	public void setLocation(Location aLocation) {
		this._location = aLocation;
	}

	public Location getLocation() {
		return this._location;
	}
}
