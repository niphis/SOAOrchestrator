package com.smartcampus.acc.local;

public interface ArtificialClimateControlSystem {

	public indoorStatus getIndoorStatus(String aRoomId);

	public void setIndoorParameters(indoorStatus aIndoorStatus);
}