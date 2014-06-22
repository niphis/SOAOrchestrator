package com.smartcampus.acc;

public interface ArtificialClimateControlSystem {

	public indoorStatus getIndoorStatus(String aRoomId);

	public void setIndoorParameters(indoorStatus aIndoorStatus);
}