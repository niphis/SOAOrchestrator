package com.smartcampus.acc;

public class indoorStatus {
	public String roomID;
	public float temperaure;
	public float humidity;
	public float fanSpeed;
	public int timer;

	public String getRoomID() {
		return this.roomID;
	}

	public void setRoomID(String aRoomID) {
		this.roomID = aRoomID;
	}

	public float getTemperaure() {
		return this.temperaure;
	}

	public void setTemperaure(float aTemperaure) {
		this.temperaure = aTemperaure;
	}

	public float getHumidity() {
		return this.humidity;
	}

	public void setHumidity(float aHumidity) {
		this.humidity = aHumidity;
	}

	public float getFanSpeed() {
		return this.fanSpeed;
	}

	public void setFanSpeed(float aFanSpeed) {
		this.fanSpeed = aFanSpeed;
	}

	public int getTimer() {
		return this.timer;
	}

	public void setTimer(int aTimer) {
		this.timer = aTimer;
	}

	public indoorStatus(String aRoomId, float aTemperaure, float aHumidity, float aFanSpeed, int aTimer) {
		setRoomID(aRoomId);
		setTemperaure(aTemperaure);
		setHumidity(aHumidity);
		setFanSpeed(aFanSpeed);
		setTimer(aTimer);
	}
	
	public indoorStatus() {}
}