package com.smartcampus.luminancemanagement.local;

import java.util.Vector;

public class RoomSettings {
	public String _roomId;
	public Vector<Spotlight> _spotlights = new Vector<Spotlight>();
	public Vector<Window> _windows = new Vector<Window>();

	public void setRoomId(String aRoomId) {
		this._roomId = aRoomId;
	}

	public String getRoomId() {
		return this._roomId;
	}
}