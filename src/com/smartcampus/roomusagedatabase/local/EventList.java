package com.smartcampus.roomusagedatabase.local;

import java.util.Vector;
import com.smartcampus.roomusagedatabase.local.EventData;

public class EventList {
	public EventData[] _events;
	public RoomCharacteristics _unnamed_RoomCharacteristics_;
	public Vector<EventData> _unnamed_EventData_ = new Vector<EventData>();

	public EventData[] getEvents() {
		return this._events;
	}

	public void setEvents(EventData[] aEvents) {
		this._events = aEvents;
	}
}