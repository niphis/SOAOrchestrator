package com.smartcampus.roomusagedatabase;

public interface RUDClient {

	public boolean eventBooked();

	public boolean eventModified(EventData aEventData);

	public boolean deletionConfirmed();

	public void eventSearched(EventData[] aEventData);

	public boolean roomCharacteriticsInserted();

	public void roomCharacteristicsRetrieved(RoomCharacteristics aRoomCharacteristics);
}