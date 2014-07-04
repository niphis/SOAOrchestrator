package com.smartcampus.acc.local;

public class IndoorStatus {
	public float temperature;
	public float humidity;
	public float fanSpeed;
	public int timer;
	public String roomID;
	public float co2level;

	public void setTemperature(float aTemperature) {
		this.temperature = aTemperature;
	}

	public float getTemperature() {
		return this.temperature;
	}

	public void setHumidity(float aHumidity) {
		this.humidity = aHumidity;
	}

	public float getHumidity() {
		return this.humidity;
	}

	public void setFanSpeed(float aFanSpeed) {
		this.fanSpeed = aFanSpeed;
	}

	public float getFanSpeed() {
		return this.fanSpeed;
	}

	public void setTimer(int aTimer) {
		this.timer = aTimer;
	}

	public int getTimer() {
		return this.timer;
	}

	public void setRoomID(String aRoomID) {
		this.roomID = aRoomID;
	}

	public String getRoomID() {
		return this.roomID;
	}
	
	public float getCo2level() {
		return this.co2level;
	}
	
	public void setCo2level(float aCo2level) {
		this.co2level = aCo2level;
	}
}
