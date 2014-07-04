package com.smartcampus.roomusagedatabase.local;

public class RoomCharacteristics {
	public String _roomId;
	public float _roomSize;
	public float _roomHeight;
	public int _numberOfWindows;
	public float _windowSize;
	public String _windowsOrientation;
	public int _seatingCapacity;
	public EventList _unnamed_EventList_;

	public String getRoomId() {
		return this._roomId;
	}

	public void setRoomId(String aRoomId) {
		this._roomId = aRoomId;
	}

	public float getRoomSize() {
		return this._roomSize;
	}

	public void setRoomSize(float aRoomSize) {
		this._roomSize = aRoomSize;
	}

	public float getRoomHeight() {
		return this._roomHeight;
	}

	public void setRoomHeight(float aRoomHeight) {
		this._roomHeight = aRoomHeight;
	}

	public int getNumberOfWindows() {
		return this._numberOfWindows;
	}

	public void setNumberOfWindows(int aNumberOfWindows) {
		this._numberOfWindows = aNumberOfWindows;
	}

	public float getWindowSize() {
		return this._windowSize;
	}

	public void setWindowSize(float aWindowSize) {
		this._windowSize = aWindowSize;
	}

	public String getWindowsOrientation() {
		return this._windowsOrientation;
	}

	public void setWindowsOrientation(String aWindowsOrientation) {
		this._windowsOrientation = aWindowsOrientation;
	}

	public int getSeatingCapacity() {
		return this._seatingCapacity;
	}

	public void setSeatingCapacity(int aSeatingCapacity) {
		this._seatingCapacity = aSeatingCapacity;
	}
}