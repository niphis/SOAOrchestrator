package com.smartcampus.roomusagedatabase.local;

public class RoomUsageDatabaseService implements RUDSystem {
	public boolean createEvent(EventData aEventData) {
		if (failed())
			return false;
		return true;
	}

	public boolean modifyEvent(EventData aEventData) {
		if (failed())
			return false;
		return true;
	}

	public void deleteEvent(String aEventId) {
		return;
	}

	public EventList searchEvent(EventData aEventData) {
		if (failed())
			return null;
		
		EventList el = new EventList();
		EventData[] events =  new EventData[3];
		
		events[0] = new EventData();
		events[0].setRoomId("ADInform1");
		events[0].setDate(System.currentTimeMillis());
		events[0].setStartTime(System.currentTimeMillis());
		events[0].setEndTime(System.currentTimeMillis() + 1000*60*60*2);
		events[0].setExpectedPeople(45);
		events[0].setEventType("Conference");
		
		events[1] = new EventData();
		events[1].setRoomId("Aula Magna");
		events[1].setDate(System.currentTimeMillis());
		events[1].setStartTime(System.currentTimeMillis() + 1000*60*60*3);
		events[1].setEndTime(System.currentTimeMillis() + 1000*60*60*5);
		events[1].setExpectedPeople(200);
		events[1].setEventType("Degree");
		
		events[2] = new EventData();
		events[2].setRoomId("ADInform1");
		events[2].setDate(System.currentTimeMillis() + 1000*60*60*24*2);
		events[2].setStartTime(System.currentTimeMillis() + 1000*60*60*24*2);
		events[2].setEndTime(System.currentTimeMillis() + 1000*60*60*24*2 + 1000*60*60*3);
		events[2].setExpectedPeople(75);
		events[2].setEventType("Conference");
		
		
		el.setEvents(events);
		return el;
	}

	public void insertRoomCharacteristics(RoomCharacteristics aRoomCharacteristics) {
		return;
	}

	public RoomCharacteristics getRoomCharacteristics(String aRoomId) {
		if (failed())
			return null;
		
		RoomCharacteristics rchar = new RoomCharacteristics();
		
		rchar.setNumberOfWindows(3);
		rchar.setRoomHeight(3.2f);
		rchar.setRoomId(aRoomId);
		rchar.setRoomSize(102.4f);
		rchar.setSeatingCapacity(50);
		rchar.setWindowSize(60);
		rchar.setWindowsOrientation("north");
		return rchar;
	}
	
	private boolean failed() {
		return Math.random() < 0.01;
	}
}
