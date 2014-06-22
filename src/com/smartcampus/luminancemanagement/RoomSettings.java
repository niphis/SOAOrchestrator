package com.smartcampus.luminancemanagement;

import java.util.Vector;
import com.smartcampus.luminancemanagement.Spotlight;
import com.smartcampus.luminancemanagement.Window;

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