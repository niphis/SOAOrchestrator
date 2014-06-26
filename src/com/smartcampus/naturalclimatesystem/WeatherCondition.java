package com.smartcampus.naturalclimatesystem;

public class WeatherCondition {
	public float _humidity;
	public float _noiseLevel;
	public int _pollution;
	public float _wind;
	public float _temperature;
	public Location _location;

	public WeatherCondition(Location l, float t, float h, float w, float n, int p) {
		this._location = l;
		this._temperature = t;
		this._humidity = h;
		this._wind = w;
		this._noiseLevel = n;
		this._pollution = p;
	}

	public float get_humidity() {
		return _humidity;
	}

	public void set_humidity(float _humidity) {
		this._humidity = _humidity;
	}

	public float get_noiseLevel() {
		return _noiseLevel;
	}

	public void set_noiseLevel(float _noiseLevel) {
		this._noiseLevel = _noiseLevel;
	}

	public int get_pollution() {
		return _pollution;
	}

	public void set_pollution(int _pollution) {
		this._pollution = _pollution;
	}

	public float get_wind() {
		return _wind;
	}

	public void set_wind(float _wind) {
		this._wind = _wind;
	}

	public float get_temperature() {
		return _temperature;
	}

	public void set_temperature(float _temperature) {
		this._temperature = _temperature;
	}

	public Location getLocation() {
		return _location;
	}

	public void setLocation(Location l) {
		this._location = l;
	}

}
