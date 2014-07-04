package com.smartcampus.roomusagedatabase.local;

import java.util.Vector;

public class EventData {
	public Event _events;
	public RoomCharacteristics _unnamed_RoomCharacteristics_;
	public Vector<Event> _unnamed_Event_ = new Vector<Event>();

	public Event getEvents() {
		return this._events;
	}

	public void setEvents(Event aEvents) {
		this._events = aEvents;
	}
}