package com.smartcampus.luminancemanagement.local;

public class LuminanceManagementService implements LuminanceManagementSystem {

	public void calibrateSpotlight(RoomSettings aRegulator) {
	
	}

	public void regulateBlind(RoomSettings aRegulator) {
	
	}

	public float getIndoorLuminance(String aRoomId) {
		return (float) 0.7;
	}

	public float getOutdoorLuminance(String aRoomId) {
		return (float) 0.32;
	}
}