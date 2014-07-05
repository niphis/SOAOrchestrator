package com.smartcampus.acc.local;

public class ArtificialClimateControlService implements ArtificialClimateControlSystem {
	private ACCClient user;
	private ArtificialClimateControlSystem system;

	public IndoorStatus getIndoorStatus(String aRoomId) {
		
		if (Math.random() < 0.01)
			return null;
		
		IndoorStatus is = new IndoorStatus();
		
		is.setTemperature(10 + (float) Math.random() * 20);
		is.setHumidity(20 + (float) Math.random() * 60);
		is.setFanSpeed(0.0f);
		is.setTimer(0);
		is.setCo2level(100 + (float) Math.random() * 1000);
		is.setRoomID(aRoomId);
		
		return is;
	}

	public void setIndoorParameters(IndoorStatus aIndoorStatus) {
		return;
	}
}
