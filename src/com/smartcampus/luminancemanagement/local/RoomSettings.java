package com.smartcampus.luminancemanagement.local;

public class RoomSettings {
	private String _roomId;
	private Spotlight[] _spotlights;
	private Window[] _windows;
	
	public RoomSettings(String aRoomId, int numSpotlight, int numWindow) {
		this._roomId = aRoomId;
		this._spotlights = new Spotlight[numSpotlight];
		this._windows = new Window[numWindow];
	}
	
	public void setRoomId(String aRoomId) {
		this._roomId = aRoomId;
	}

	public String getRoomId() {
		return this._roomId;
	}
	
	public void setSpotlight(Spotlight aSpotlight, int id) {
		if (id > -1 && id < this._spotlights.length) {
			this._spotlights[id] = aSpotlight;
		}
	}
	
	public Spotlight[] getSpotlights() {
		return this._spotlights;
	}
	
	public void setWindows(Window aWindow, int id) {
		if (id > -1 && id < this._windows.length) {
			this._windows[id] = aWindow;
		}
	}
	
	public Window[] getWindows() {
		return this._windows;
	}
}