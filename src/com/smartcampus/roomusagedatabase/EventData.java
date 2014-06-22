package com.smartcampus.roomusagedatabase;

import java.util.Vector;
import com.smartcampus.roomusagedatabase.Event;

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