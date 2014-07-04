package com.smartcampus.roomusagedatabase.local;

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
		EventData[] evd = new EventData[2];
		Event e;
		
		e = new Event();
		e.setDate(new Date());
		e.setStartTime(new Time(System.currentTimeMillis()));
		e.setEndTime(new Time(System.currentTimeMillis() + 1000*60*3));
		e.setRoomId("ADInform1");
		e.setEventType("Laurea");
		e.setExpectedPeople(150);
		
		evd[0] = new EventData();
		evd[0].setEvents(e);
		
		e = new Event();
		e.setDate(new Date());
		
		e.setStartTime(new Time(System.currentTimeMillis() + 1000*60*3));
		e.setEndTime(new Time(System.currentTimeMillis() + 1000*60*6));
		e.setRoomId("ADInform2");
		e.setEventType("Laurea");
		e.setExpectedPeople(20);
		
		evd[1] = new EventData();
		evd[1].setEvents(e);
		
		return evd;
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