package com.smartcampus.roomusagedatabase.local;

import java.util.Date;
import java.sql.Time;

public interface RUDSystem {

	public void createEvent(EventData aEventData);

	public boolean modifyEvent(String aEventId, EventData aEventData);

	public void deleteEvent(String aEventId);

	public EventData[] searchEvent(String aRoomId, Date aDate, Time aStartTime, String aEventType);

	public void insertRoomCharacteristics(RoomCharacteristics aRoomCharacteristics);

	public RoomCharacteristics getRoomCharacteristics(String aRoomId);
}