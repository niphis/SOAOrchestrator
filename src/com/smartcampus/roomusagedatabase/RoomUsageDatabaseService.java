package com.smartcampus.roomusagedatabase;

import java.util.Date;
import java.sql.Time;

public class RoomUsageDatabaseService implements RUDSystem {

	public void createEvent(EventData aEventData) {
		return;
	}

	public boolean modifyEvent(String aEventId, EventData aEventData) {
		double probability = Math.random();
		if (probability < 0.1)
			return false;
		return true;
	}

	public void deleteEvent(String aEventId) {
		return;
	}

	public EventData[] searchEvent(String aRoomId, Date aDate, Time aStartTime, String aEventType) {
		throw new UnsupportedOperationException();
		
	}

	public void insertRoomCharacteristics(RoomCharacteristics aRoomCharacteristics) {
		return;
	}

	public RoomCharacteristics getRoomCharacteristics(String aRoomId) {
		RoomCharacteristics data = new RoomCharacteristics();
		data.setNumberOfWindows(3);
		data.setRoomHeight(3.2f);
		data.setRoomId(aRoomId);
		data.setRoomSize(102.4f);
		data.setSeatingCapacity(50);
		data.setWindowSize(6);
		data.setWindowsOrientation("nord");
		data.setWindowsPosition("asd");
		return data;
	}
}