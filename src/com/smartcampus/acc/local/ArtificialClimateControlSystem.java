package com.smartcampus.acc.local;

public interface ArtificialClimateControlSystem {

	public IndoorStatus getIndoorStatus(String aRoomId);

	public void setIndoorParameters(IndoorStatus aIndoorStatus);
}
