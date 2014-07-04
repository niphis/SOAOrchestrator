package com.smartcampus.acc.local;

public class ArtificialClimateControlService implements ArtificialClimateControlSystem {
	private ACCClient user;
	private ArtificialClimateControlSystem system;

	/*public int getIndoorStatus(String aRoomId) {
		double ran = Math.random();
		if (ran < 0.01)
			return -1;
		int temperature = 10 + (int) ran * 20;
		return temperature;
	}*/
	
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