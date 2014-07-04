package com.smartcampus.roomusagedatabase.local;

public interface RUDSystem {

	public boolean createEvent(EventData aEventData);

	public boolean modifyEvent(EventData aEventData);

	public void deleteEvent(String aEventId);

	public EventList searchEvent(EventData aEventData);

	public void insertRoomCharacteristics(RoomCharacteristics aRoomCharacteristics);

	public RoomCharacteristics getRoomCharacteristics(String aRoomId);
}