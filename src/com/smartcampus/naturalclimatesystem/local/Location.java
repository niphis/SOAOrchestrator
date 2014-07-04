package com.smartcampus.naturalclimatesystem.local;

public class Location {
	public String _roomId;
	public int _windowsId;

	public Location() {
	}
	
	public String getRoomId() {
		return this._roomId;
	}

	public void setRoomId(String aRoomId) {
		this._roomId = aRoomId;
	}

	public int getWindowsId() {
		return this._windowsId;
	}

	public void setWindowsId(int aWindowsId) {
		this._windowsId = aWindowsId;
	}
	public String getFullLocation(){
		return this.getRoomId() + ";" + Integer.toString(this.getWindowsId());
	}
}