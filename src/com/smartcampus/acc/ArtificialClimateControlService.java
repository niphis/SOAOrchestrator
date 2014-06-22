package com.smartcampus.acc;

public class ArtificialClimateControlService implements ArtificialClimateControlSystem {
	private ACCClient user;
	private ArtificialClimateControlSystem system;

	public indoorStatus getIndoorStatus(String aRoomId) {
		float temperature = 10 + (float) Math.random() * 20 ;
		float humidity = 20 + (float) Math.random() * 60;
		float fanSpeed = 0;
		int timer = 0;
		return new indoorStatus(aRoomId, temperature, humidity, fanSpeed, timer);
	}

	public void setIndoorParameters(indoorStatus aIndoorStatus) {
		return;
	}
}