package com.smartcampus.roomusagedatabase.local;

public interface RUDClient {

	public void eventBooked(boolean aResult);

	public void eventModified(boolean aResult);

	public void eventDeleted(boolean aResult);

	public void searchResults(EventList aEventList);

	public void roomCharacteriticsInserted(boolean aResult);

	public void roomCharacteristics(RoomCharacteristics aRoomCharacteristics);
}