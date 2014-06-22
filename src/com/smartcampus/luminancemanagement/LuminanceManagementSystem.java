package com.smartcampus.luminancemanagement;

public interface LuminanceManagementSystem {

	public void calibrateSpotlight(RoomSettings aRegulator);

	public void regulateBlind(RoomSettings aRegulator);

	public float getIndoorLuminance(String aRoomId);

	public float getOutdoorLuminance(String aRoomId);
}